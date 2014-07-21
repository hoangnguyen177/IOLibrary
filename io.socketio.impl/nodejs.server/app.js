
/////////////////////////////////////////////////////
//Constants
/////////////////////////////////////////////////////
var CONNECTION_CONF = {
  proc    : 'http'        ,
  host    : 'localhost'   ,
  port    : 8080          ,
  nsp     : '/'           ,
};

//for localhost postgres database to store messages
var PG_DATABASE_CONST = {
  username: 'hoangnguyen' ,
  password: 'pass'        ,
  port    : 5432          ,
  host    : 'localhost'   ,
  database: 'server'      ,
};
//using mongodb instead
var MONGO_DATABASE_CONST = {
  username: 'hoangnguyen' ,
  password: 'pass'        ,
  port    : 27017         ,
  host    : 'localhost'   ,
  database: 'workways'    ,
  messages_collection: 'messages',
  inputs_collection: 'inputs', 
  definition_collection: 'definitions'
};

//events header from cleint
var CLIENT_CONST = {
  // message is sent from client
  message         : 'message'       ,
  // a client requests for connection
  connect         : 'connection'    ,
  // a client is disconnected
  disconnect      : 'disconnect'    ,
  //authenticate
  auth            : 'authenticate'  ,
  //select
  selectsource    : 'selectsource'  , //for sink to select source to connect
};

//events header from server
var SERVER_CONST = {
  //return of the authentication
  auth_return           : 'authreturn'            ,
  //source connect
  source_connect        : 'sourceconnect'         ,
  //sink connect
  sink_connect          : 'sinkconnect'           ,
  // source disconnect
  source_disconnect     : 'sourcedisconnect'      ,
  // sink disconnect
  sink_disconnect       : 'sinkdisconnect'        ,
  //connection established
  connection_established: 'connectionestablished' ,
  // permission changed
  permission_changed    : 'permissionchanged'     ,
};

var CONNECTION_CONST = {
  // details of the socket
  details   : "details"     ,
    // username
  username  : 'username'    ,
  //password
  password  : 'password'    ,
  //type of client
  clienttype: "clienttype"  ,
  //source
  source    : 'source'      ,
  //source id
  sourceid  : 'sourceid'    ,
  //sink
  sink      : 'sink'        ,
};


var MESSAGE_CONST = {
  //type of the message
  msg_type            : "messagetype"     , 
  //result of the authentication: true or false
  auth_result         : 'authresult'      , 
  //id of the socket
  id                  : 'id'              ,
  //comment
  comment             : 'comment'         ,
  //list of sources
  sourceList          : 'sourcelist'      ,
  // lis of sink id
  sinkList            : 'sinklist'        ,
  // allowed operations
  allowed_operations  : 'allowedoperations',
  //read
  read                : 'r'               ,
  write               : 'w'               ,
  execute             : 'x'               , //maybe
};




/////////////////////////////////////////////////////
//Main methods
/////////////////////////////////////////////////////
//note:  the database was not used to record the definition of sources/sinks
// it is only used to store messages, which can be queried by the sinks later on
/////
var DEBUG = true;

var express = require('express')
  , app = express()
  , http = require('http')
  , server = http.createServer(app)
  , io = require('socket.io').listen(server);


var  port = parseInt(process.argv[2], 10) || CONNECTION_CONF.port
     , nsp = process.argv[3] || CONNECTION_CONF.nsp
     , usingdb = parseBoolean(process.argv[4]) || false
     ;


if(usingdb){
  console.log("----- messages will be stored in database ---------");     
  var databaseUrl = "mongodb://" + MONGO_DATABASE_CONST.username + ":" + MONGO_DATABASE_CONST.password + 
                    "@" + MONGO_DATABASE_CONST.host+ "/" + MONGO_DATABASE_CONST.database; 
  if(DEBUG)
    console.log("database url:" + databaseUrl);                  
  var collections = ["messages", "inputs", "definitions"]; 
  var db = require("mongojs").connect(databaseUrl, collections);
}



// routing
app.get(nsp, function (req, res) {
  res.sendfile(__dirname + '/index.html');
});

// there are two types of clients
// sources: make requests
var sources = {};
// serve requests from sources
var sinks = {};
// list of virtual conenction between sources and sinks
// note that one source can connect to multiple sinks, but not the otherway around
var connections = {};
//authenticated list
var authcatedClients = {};




//serving all the connections
io.of(nsp).on(CLIENT_CONST.connect, function (socket) {
  log('[connect] New connection from id:' +  socket.id);

  
    /////////////// authenticate ///////////////////
  /**
  * when the socket requests to connect and authenticate
  * details contains the information
  * such as username, password, type
  * for now ignore username and password, accepts all the connection
  * 
  * headers:
  * username: ignore
  * password: ignore
  * clienttype: source or sink
  * sourceid: source id  - for sink connections
  */
  socket.on(CLIENT_CONST.auth, function(details){
    //add details into the socket
    //retrieve later via this
    socket[CONNECTION_CONST.details] = details;
    //console.log("details=" + JSON.stringify(details));
    ///////////////////////////////////////
    ///// AUTHENTICATE HERE ////////////
    ///////////////////////////////////////
    authcatedClients[socket.id] = true;
    var _uname = getUsernameFromSource(details);
    log("New connection: uname=", _uname);
    //which type of client it is
    if(details[CONNECTION_CONST.clienttype] === CONNECTION_CONST.source){
      //add to sources
      sources[socket.id] = socket;      
      log("[auth] New source added: "+ socket.id);
      if(usingdb){
        var _definitionToStore = {'socketid':socket.id, 'definition':details, 'timestamp': Date.now()};
        db.definitions.find({socketid:socket.id}, function(err, _rows){
          if(!_rows || _rows.length==0){
            db.definitions.save(_definitionToStore, function(err, saved) {});
          }
          else{
            db.definitions.update({socketid:socket.id}, {'definition':details, 'timestamp': Date.now()},  {multi:true}, function() {});  
          }
        });


      }

      //return the result
      var _authreply = {  
                          messagetype :SERVER_CONST.auth_return, 
                          id : socket.id, 
                          authresult : 'true', 
                          comment: '',
                        };
      console.log("[auth] notify source about the authentication:", _authreply);
      socket.send(_authreply);
      //notify all the sinks that a new source has been connected
      var _msgToSink = {
            messagetype: SERVER_CONST.source_connect,
            sourcelist: prepareSourcesListWithUsername(sources, _uname),
      }
      console.log("[auth] notify sinks that new source has connected:", _msgToSink);
      for(var _sinkid in sinks){
        console.log("sink uname:",sinks[_sinkid][CONNECTION_CONST.username], " _uname", _uname);
        if(sinks[_sinkid].details[CONNECTION_CONST.username] === _uname){
          sinks[_sinkid].send(_msgToSink);
          console.log('[auth] sending: ', _msgToSink, " to:", _sinkid);
        }
      }

    }
    // if not specified, or otherwise add to sink
    else{
        //add to sink list
        sinks[socket.id] = socket;
        log("[auth] new sink added: "+ socket.id);
        //return the result: 
        //TODO: add timestamp  
        var _authreply = { 
                messagetype: SERVER_CONST.auth_return,
                authresult: 'true', 
                id : socket.id, 
                sourcelist: prepareSourcesListWithUsername(sources, _uname), // list of sources - removed some information
                timestamp: Date.now(),
                comment: ''
        };
        console.log("[auth] notify the sink about the authenticaiton and list of sources:", _authreply);  
        socket.send(_authreply);

    }
  });



  
    /////////////// select source ///////////////////
  /**
  * this one is called when sink selects a source to connect to
  * later can query data, get all messages so far from this source, and 
  * send to the sink, the sink can "replay" or ignore them
  * add an option of querying messages so far, go to mongodb and get messages
  * this method accepts live or dead sourceid
  * if the source is not alive, query the database for the recorded messages from that source
  */
  socket.on(CLIENT_CONST.selectsource, function(_sourceid, latestMessageTStamp){
    timeStampToFindMessages = latestMessageTStamp;
    if(latestMessageTStamp == null){
      console.log("[selectsource] from ", socket.id,  " selectsource:", _sourceid, " timestamp is NULL");
      timeStampToFindMessages = new Date(1986, 7, 17).getTime(); //my birthday
    }
    else  
      console.log("[selectsource] from ", socket.id,  " selectsource:", _sourceid, " timestamp:", latestMessageTStamp);
    var _allowedOpts = MESSAGE_CONST.read;
    if(usingdb){
      db.messages.find({socketid:_sourceid, timestamp:{$gt:timeStampToFindMessages}}, function(err, msgEntries){
        if( err || !msgEntries || msgEntries.length == 0){
          console.log("No messages entry found");
          if(!isSource(_sourceid, sources)){
            log("[selectsource] "+ _sourceid+ " is an invalid source id");
            return;
          }
        }
        sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
        sinks[socket.id].send({ messagetype: SERVER_CONST.connection_established, allowedoperations: _allowedOpts});
        //for each message entry
        msgEntries.forEach(function(msgEntry) {
          var _recordedMsgToSend = msgEntry.message;
          _recordedMsgToSend['timestamp']=msgEntry.timestamp;
          sinks[socket.id].send(_recordedMsgToSend);
        });
        if(connections.hasOwnProperty(_sourceid))
          connections[_sourceid].push(socket.id);
        else
          connections[_sourceid] = [socket.id];
        //TODO: what if there are messages coming since the last time query the database???  
        if(socket.id === connections[_sourceid][0]){
          _allowedOpts = _allowedOpts.concat(MESSAGE_CONST.write);
          sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
          sinks[socket.id].send({messagetype:SERVER_CONST.permission_changed, allowedoperations: _allowedOpts});
          console.log("====socketid:", socket.id, " permission:", sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations]);
        }
          
        //notify the source that a new sink has been connected to it
        //also send the list of sinks that are currently connectd
        var _msgToSource = {
          messagetype: SERVER_CONST.sink_connect,
          sinklist: connections[_sourceid],      
        };
        sources[_sourceid].send(_msgToSource);
      });
    }
    else{
      if(!isSource(_sourceid, sources)){
        log("[selectsource] "+ _sourceid+ " is an invalid source id");
        return;
      }
      if(connections.hasOwnProperty(_sourceid))
          connections[_sourceid].push(socket.id);
      else
          connections[_sourceid] = [socket.id];
      if(socket.id === connections[_sourceid][0]){
        _allowedOpts = _allowedOpts.concat(MESSAGE_CONST.write);
        sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
        sinks[socket.id].send({ messagetype: SERVER_CONST.connection_established, allowedoperations: _allowedOpts });
        console.log("====socketid:", socket.id, " permission:", sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations]);
      }
      //notify the source that a new sink has been connected to it
      //also send the list of sinks that are currently connectd
      var _msgToSource = {
        messagetype: SERVER_CONST.sink_connect,
        sinklist: connections[_sourceid],      
      };
      sources[_sourceid].send(_msgToSource);  
    }

  });


  /////////////// message ///////////////////
  socket.on(CLIENT_CONST.message, function(message){
    //log("[message] message from ", socket.id , " message:", JSON.stringify(message));
    //store message here
    var _receivedTimeStamp = Date.now();
    message['timestamp']=_receivedTimeStamp;
    var _messageToStore = {'socketid':socket.id, 'message':message, 'timestamp': _receivedTimeStamp};
    //log("[message] message to store:", JSON.stringify(_messageToStore));
    // message from source
    if(isSource(socket.id, sources)){
      //distribute it to the sinks
      if(connections.hasOwnProperty(socket.id) && connections[socket.id].length > 0)
        log("[message] send message to all sinks");
      else
        log("[message] this source is not connected to any sink.");
      for(var _index in connections[socket.id]){
        sinks[connections[socket.id][_index]].send(message);
      }
      if(usingdb){
        db.messages.save(_messageToStore, function(err, saved) {
          if( err || !saved ) log("!!!!!!!!!!!!!!!!!Data not saved");
        });
      }
    }
    else{  //message from sink
      log("[message] message from ", socket.id , " message:", JSON.stringify(message));
      
      //only writable client can do that
      if(isSinkWritable(socket.id)){
        var _sourceIdOfTheSink = findSource(socket.id, connections);
        if(_sourceIdOfTheSink){
          // send to source
          console.log("[message] send message to source:", _sourceIdOfTheSink);
          sources[_sourceIdOfTheSink].send(message);  
          for(var _sink in connections[_sourceIdOfTheSink]){
            if(connections[_sourceIdOfTheSink][_sink] !== socket.id ){
              console.log("[message] send message to (other) sink:", connections[_sourceIdOfTheSink][_sink]);
              sinks[connections[_sourceIdOfTheSink][_sink]].send(message);
            }//end if
          }//end for
          if(usingdb){
            db.inputs.save(_messageToStore, function(err, saved) {
              if( err || !saved ) log("!!!!!!!!!!!!!!!!!Data not saved");
            });
          }
        }//end if
        else
          console.log("[mesage]Cannot find source:" ,socket.id,  " in connections:" , connections);
      }
      else{
        console.log("[message]The sink is not allowed to send command, allowed operations:",
                 socket[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations]);
      } 
    
    }//end message from sink


  }); //end message



  /////////////// disconnect ///////////////////
  socket.on(CLIENT_CONST.disconnect, function(){
    //it is a source
    if(sources.hasOwnProperty(socket.id)){
      var _uname = getUsernameFromSource(sources[socket.id].details);
      //remove the source from sources
      delete sources[socket.id];
      delete authcatedClients[socket.id];
      if(connections.hasOwnProperty(socket.id))  delete connections[socket.id];
      var _msgToSink = {
        messagetype: SERVER_CONST.source_disconnect,
        sourcelist: prepareSourcesListWithUsername(sources, _uname),
      };
      for(var _sinkid in sinks){
          sinks[_sinkid].send(_msgToSink);
      }      
      log("[disconnect] source: "+ socket.id+ " disconnected, removed from sources list");  
    }
    else if(sinks.hasOwnProperty(socket.id)){
      log("[disconnect] sink: "+ socket.id+ " disconnected, removed from sinks list");    
      var _sourceConnectionId = findSource(socket.id, connections);
      //only concern where this sink belongs to a connection
      if(_sourceConnectionId){
        log("[disconnect] this sink is in a connection, source:", _sourceConnectionId);
        var indexPosition = removeElementFromArray(socket.id, connections[_sourceConnectionId]);  
        log("[disconnect] indexPosition:", indexPosition);
          
        //the first position
        if(indexPosition === 0 && connections[_sourceConnectionId].length > 0){
          //notify the first sink that it now have the write permission
          var _newFirstSink = sinks[connections[_sourceConnectionId][0]];
          var _allowedOpts = _newFirstSink[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations];
           // add w into it 
          _allowedOpts = _allowedOpts.concat(MESSAGE_CONST.write);
          _newFirstSink[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
          _newFirstSink.send({messagetype:SERVER_CONST.permission_changed, allowedoperations: _allowedOpts});
          log("[disconnect] sink removed is the first sink, assign new writing sink")
        }
        else if(connections[_sourceConnectionId].length == 0){
          // this source runs out of sink
          sources[_sourceConnectionId].send({messagetype: SERVER_CONST.sink_disconnect});
          log("[disconnect] this source is out of sink")
        }
      }
      else
        log("[disconnect] this sink is not in any connection");
      //remove from sink
      delete sinks[socket.id];
    }
  });

});




  
  
//////////////////////////});




server.listen(port, function() {
  log('[listen] Socket.IO server listening on port:'+ port + ' nsp:' + nsp);
});



//////////////////////////////////////////////////////
// misc methods
//////////////////////////////////////////////////////
/**
* parse string to boolean
*/
function parseBoolean(string) {
  switch (String(string).toLowerCase()) {
    case "true":
    case "1":
    case "yes":
    case "y":
      return true;
    case "false":
    case "0":
    case "no":
    case "n":
      return false;
    default:
      //you could throw an error, but 'undefined' seems a more logical reply
      return undefined;
  }
}


/**
* source list contains more information than needed to send to sinks
* remove some unnnecessary information
*/
function prepareSourcesList(_sourceList)
{
  var _sources = {};
  for (var _sid in _sourceList) {
    var _details = _sourceList[_sid].details;
    var _presentingDetails = {};
    for (var key in _details) {
      if(key != CONNECTION_CONST.password && key != CONNECTION_CONST.clienttype)
        _presentingDetails[key] = _details[key];           
    }//end inner for
    _sources[_sid] = _presentingDetails;
  }//end outer for
  //log("[prepareSourcesList]", _sources);
  return _sources;
}
/**
* prepare source list belong to this username
*/
function prepareSourcesListWithUsername(_sourceList, _username)
{
  var _sources = {};
  for (var _sid in _sourceList) {
    var _details = _sourceList[_sid].details;
    var _presentingDetails = {};
    var _belongToThisUser = false;
    //check username: note that there are two types of connections
    //from library and from socket library directly
    //from direct socket: {"username":"asource","appname":"edu.monash.io.socketio.test.TestSourceClient","comment":"a test for source client"}
    //from library: in configuration of each channel
    if(CONNECTION_CONST.username in _details){
      for (var key in _details) {
        if(key != CONNECTION_CONST.password && key != CONNECTION_CONST.clienttype)
          _presentingDetails[key] = _details[key];           
      }//end inner for
      if(_details[CONNECTION_CONST.username]===_username){
        _belongToThisUser = true;
      }
    }
    else{
      for(var key in _details){
        if(key !== 'clienttype' && 'type' in _details[key] && _details[key]['type'] === 'channel'){
          var _configuration = _details[key]['configuration'];
          if(_configuration[CONNECTION_CONST.username] === _username)
            _belongToThisUser = true;
        }
        _presentingDetails[key] = _details[key];
        //TODO: remove password
      }
    }

    if(_belongToThisUser)
      _sources[_sid] = _presentingDetails;      

  }//end outer for
  //log("[prepareSourcesList]", _sources);
  return _sources;
}

/**
* get username from source information
*/
function getUsernameFromSource(_sourceDetails){
  if(CONNECTION_CONST.username in _sourceDetails){
      return _sourceDetails[CONNECTION_CONST.username]
  }
  else{
      for(var key in _sourceDetails){
        if(key !== 'clienttype' && 'type' in _sourceDetails[key] && _sourceDetails[key]['type'] === 'channel')
          return _sourceDetails[key]['configuration'][CONNECTION_CONST.username];
      }
  }
}


/*
* checks whether this socket id is source
*/
function isSource(_socketid, _sourceList){
  return _sourceList.hasOwnProperty(_socketid);
}

/**
* find the source that this sink belongs
* empty if non
*/
function findSource(_sinkid, _connectionList){
  for(var _sourceid in _connectionList){
    for(var _sid in _connectionList[_sourceid]){
      if (_connectionList[_sourceid][_sid] === _sinkid) return _sourceid;
    }
  }
  return "";
}

/**
* remove an element from an array
* return the index whether the value was
*/
function removeElementFromArray(elementVal, array){
  for(var i=0; i< array.length&& array[i]!==elementVal ; i++) {}
  if( i < array.length ) array.splice(i, 1);
  return i;
}

/**
* is socket writable
*/
function isSocketWritable(_socket){
  return (_socket[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations].indexOf(MESSAGE_CONST.write) !== -1);
}


/**
* is sink writable
*/
function isSinkWritable(_sinkid){
  return (sinks[_sinkid][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations].indexOf(MESSAGE_CONST.write) !== -1);
}


/**
* prepare message to send
*/
function prepareMessage(_messageType, _messageContents){
  return {messagetype: _messageType, message: _messageContents};
}


/**
* log
*/
function log(){
  if(!DEBUG)
    return;
  var logString = "";
  for (var i = 0; i < arguments.length; i++) {
    logString += arguments[i] + " ";
  }
  console.log(logString);
}