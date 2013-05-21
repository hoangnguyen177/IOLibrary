
/////////////////////////////////////////////////////
//Constants
/////////////////////////////////////////////////////
var CONNECTION_CONST = {
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
var DEBUG = true;

var express = require('express')
  , app = express()
  , http = require('http')
  , server = http.createServer(app)
  , io = require('socket.io').listen(server);


var  port = parseInt(process.argv[2], 10) || CONNECTION_CONST.port
     , nsp = process.argv[3] || CONNECTION_CONST.nsp;

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
  log('[connect] New connection has been established. Id=', socket.id);

  
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

    ///////////////////////////////////////
    ///// AUTHENTICATE HERE ////////////
    ///////////////////////////////////////
    authcatedClients[socket.id] = true;
    //which type of client it is
    if(details[CONNECTION_CONST.clienttype] === CONNECTION_CONST.source){
      //add to sources
      sources[socket.id] = socket;      
      log("[auth] New source added: ", socket.id);
      //return the result
      var _authreply = {  
                          messagetype :SERVER_CONST.auth_return, 
                          id : socket.id, 
                          authresult : 'true', 
                          comment: '',
                        };
      log("[auth] message to source:", _authreply);
      socket.send(_authreply);
      //notify all the sinks that a new source has been connected
      var _msgToSink = {
            messagetype: SERVER_CONST.source_connect,
            sourcelist: prepareSourcesList(sources),
      }
      log("[auth] message to sink:", _msgToSink);
      for(var _sinkid in sinks){
        sinks[_sinkid].send(_msgToSink);
      }

    }
    // if not specified, or otherwise add to sink
    else{
        //add to sink list
        sinks[socket.id] = socket;
        log("[auth] New sink added: ", socket.id);
        //return the result: 
        var _authreply = { 
                messagetype: SERVER_CONST.auth_return,
                authresult: 'true', 
                id : socket.id, 
                sourcelist: prepareSourcesList(sources), // list of sources - removed some information
                comment: ''
        };
        log("[auth] message to sink:", _authreply);  
        socket.send(_authreply);

    }
  });



  
    /////////////// select source ///////////////////
  /**
  * this one is called when sink selects a source to connect to
  * later can query data, get all messages so far from this source, and 
  * send to the sink, the sink can "replay" or ignore them
  */
  socket.on(CLIENT_CONST.selectsource, function(_sourceid){
    log("[message] from ", socket.id, " selectsource:", _sourceid);
    if(isSource(_sourceid, sources))  {
      if(connections.hasOwnProperty(_sourceid))
        connections[_sourceid].push(socket.id);
      else
        connections[_sourceid] = [socket.id];
      //notify the source that a new sink has been connected to it
      //also send the list of sinks that are currently connectd
      var _msgToSource = {
        messagetype: SERVER_CONST.sink_connect,
        sinklist: connections[_sourceid],      
      };
      sources[_sourceid].send(_msgToSource);
      //tell the sink that it has been connected to the wanted source. with allowed oeprations
      var _allowedOpts = MESSAGE_CONST.read;
      if(socket.id === connections[_sourceid][0]) //first one{}
          _allowedOpts = _allowedOpts.concat(MESSAGE_CONST.write);
      sinks[socket.id].send({ messagetype: SERVER_CONST.connection_established, allowedoperations: _allowedOpts});
      sinks[socket.id][CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
      // when receive message, also checks whether its the first one connected 
      log("[selectsource] Sink:", socket.id, " connects to source:", _sourceid);
    }
    else
      log("[selectsource] ", _sourceid, " is an invalid source id");
  });


  /////////////// message ///////////////////
  socket.on(CLIENT_CONST.message, function(message){
    log("[message] from ", socket.id, " message:", message);
    // message from source
    if(isSource(socket.id, sources)){
      //distribute it to the sinks
      log("[message] send message to sinks");
      for(var _index in connections[socket.id]){
        sinks[connections[socket.id][_index]].send(message);
      }
    }
    else{  //message from sink
      //only writable client can do that
      if(isSocketWritable(socket)){
        var _sourceIdOfTheSink = findSource(socket.id, connections);
        if(_sourceIdOfTheSink){
          // send to source
          log("[message] send message to source:", _sourceIdOfTheSink);
          sources[_sourceIdOfTheSink].send(message);  
          for(var _sink in connections[_sourceIdOfTheSink]){
            if(connections[_sourceIdOfTheSink][_sink] !== socket.id ){
              log("[message] send message to (other) sink:", connections[_sourceIdOfTheSink][_sink]);
              sinks[connections[_sourceIdOfTheSink][_sink]].send(message);
            }//end if
          }//end for
        }//end if
        else
          log("[mesage]Cannot find source:", socket.id, " in connections:", connections);
      }
      else{
        log("[message]The sink is not allowed to send command, allowed operations:",
                 socket[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations]);
      } 
    
    }//end message from sink


  }); //end message



  /////////////// disconnect ///////////////////
  socket.on(CLIENT_CONST.disconnect, function(){
    //it is a source
    if(sources.hasOwnProperty(socket.id)){
      //remove the source from sources
      delete sources[socket.id];
      delete authcatedClients[socket.id];
      if(connections.hasOwnProperty(socket.id))  delete connections[socket.id];
      var _msgToSink = {
        messagetype: SERVER_CONST.source_disconnect,
        sourcelist: prepareSourcesList(sources),
      };
      for(var _sinkid in sinks){
          sinks[_sinkid].send(_msgToSink);
      }      
      log("Source: ", socket.id, " disconnected, removed from lists");  
    }
    else if(sinks.hasOwnProperty(socket.id)){
      var _sourceConnectionId = findSource(socket.id, connections);
      //only concern where this sink belongs to a connection
      if(_sourceConnectionId){
        var indexPosition = removeElementFromArray(socket.id, connections[_sourceConnectionId]);    
        //the first position
        if(indexPosition === 0 && connections[_sourceConnectionId].length > 0){
          //notify the first sink that it now have the write permission
          var _newFirstSink = sinks[connections[_sourceConnectionId][0]];
          var _allowedOpts = _newFirstSink[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations];
           // add w into it 
          _allowedOpts = _allowedOpts.concat(MESSAGE_CONST.write);
          _newFirstSink.send({messagetype:SERVER_CONST.permission_changed, allowed_operations: _allowedOpts});
          _newFirstSink[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations] = _allowedOpts;
        }
        else if(connections[_sourceConnectionId].length == 0){
          // this source runs out of sink
          sources[_sourceConnectionId].send({messagetype: SERVER_CONST.sinkdisconnect});
        }
      }
    }
  });

});




  
  
//////////////////////////});




server.listen(port, function() {
  log('Socket.IO server listening on port', port);
});



//////////////////////////////////////////////////////
// misc methods
//////////////////////////////////////////////////////
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
      if(key != CONNECTION_CONST.username && key != CONNECTION_CONST.password && key != CONNECTION_CONST.clienttype)
        _presentingDetails[key] = _details[key];           
    }//end inner for
    _sources[_sid] = _presentingDetails;
  }//end outer for
  //log("[prepareSourcesList]", _sources);
  return _sources;
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
  var count =0;
  for(var _element in array){
    if(_element === elementVal)
      array.splice(count, 1);
    else
      count++;
  }
  return count;
}

/**
* is socket writable
*/
function isSocketWritable(_socket){
  return (_socket[CONNECTION_CONST.details][MESSAGE_CONST.allowed_operations].indexOf(MESSAGE_CONST.write) !== -1);
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