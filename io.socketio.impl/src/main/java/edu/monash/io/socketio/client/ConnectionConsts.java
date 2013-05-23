package edu.monash.io.socketio.client;


public class ConnectionConsts {
  //CLIENT_CONSTS	
	//events fro mclient
  //connect
  public static final String CLIENT_C_CONNECT= "connection";
  //authenticate
  public static final String CLIENT_C_AUTH = "authenticate";
  //select
  public static final String CLIENT_C_SELECT = "selectsource";
  //disconect
  public static final String CLIENT_C_DISCONNECT = "disconnect";
  // message from source
  public static final String CLIENT_C_MESSAGE =  "message";


//events header from server

  //return of the authentication
  public static final String SERVER_C_AUTH_RETURN = "authreturn";
  //source connect
  public static final String SERVER_C_SOURCE_CONNECT = "sourceconnect";
  //sink connect
  public static final String SERVER_C_SINK_CONNECT = "sinkconnect";
  // source disconnect
  public static final String SERVER_C_SOURCE_DISCONNECT = "sourcedisconnect";
  // sink disconnect
  public static final String SERVER_C_SINK_DISCONNECT  = "sinkdisconnect";
  //connection established
  public static final String SERVER_C_CONNECTION_ESTABLISHED  = "connectionestablished";
  // permission changed
  public static final String SERVER_C_PERMISSION_CHANGED  = "permissionchanged";

	//message
  // details of the socket
  public static final String CONNECTION_C_DETAILS = "details";
  // username
  public static final String CONNECTION_C_USERNAME  = "username";
  //password
  public static final String CONNECTION_C_PASSWORD  = "password";
  //type of client
  public static final String CONNECTION_C_CLIENT_TYPE  = "clienttype";
  //source
  public static final String CONNECTION_C_SOURCE  = "source";
  //source id
  public static final String CONNECTION_C_SOURCE_ID  = "sourceid";
  //sink
  public static final String CONNECTION_C_SINK = "sink";
  //message type
  public static final String MESSAGE_C_MESSAGE_CODE = "messagetype";
  //result of the authentication= true or false
  public static final String MESSAGE_C_AUTH_RESULT  = "authresult"; 
  //id of the socket
  public static final String MESSAGE_C_ID  = "id";
  //comment
  public static final String MESSAGE_C_COMMENT  = "comment";
  //list of sources
  public static final String MESSAGE_C_SOURCE_LIST  = "sourcelist";
  // list of sinks
  public static final String MESSAGE_C_SINK_LIST = "sinklist";
  // allowed operations
  public static final String MESSAGE_C_ALLOWED_OPS  = "allowedoperations";
  //read
  public static final String MESSAGE_C_READ  = "r";
  public static final String MESSAGE_C_WRITE  = "w";
  public static final String MESSAGE_C_EXECUTE  = "x"; //maybe
}
