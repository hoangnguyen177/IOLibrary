package edu.monash.io.socketio.client.test;


public class TestConsts {
  //CLIENT_CONSTS	
	//events fro mclient
  //connect
  public static final String CLIENT_E_CONNECT= "connection";
  //authenticate
  public static final String CLIENT_E_AUTH = "authenticate";
  //select
  public static final String CLIENT_E_SELECT = "selectsource";
  //disconect
  public static final String CLIENT_E_DISCONNECT = "disconnect";
  // message from source
  public static final String CLIENT_E_MESSAGE =  "message";


//events header from server

  //return of the authentication
  public static final String SERVER_E_AUTH_RETURN = "authreturn";
  //source connect
  public static final String SERVER_E_SOURCE_CONNECT = "sourceconnect";
  //sink connect
  public static final String SERVER_E_SINK_CONNECT = "sinkconnect";
  // source disconnect
  public static final String SERVER_E_SOURCE_DISCONNECT = "sourcedisconnect";
  // sink disconnect
  public static final String SERVER_E_SINK_DISCONNECT  = "sinkdisconnect";
  //connection established
  public static final String SERVER_E_CONNECTION_ESTABLISHED  = "connectionestablished";
  // permission changed
  public static final String SERVER_E_PERMISSION_CHANGED  = "permissionchanged";

	//message
  // details of the socket
  public static final String CLIENT_DETAILS = "details";
  // username
  public static final String CLIENT_USERNAME  = "username";
  //password
  public static final String CLIENT_PASSWORD  = "password";
  //type of client
  public static final String CLIENT_CLIENT_TYPE  = "clienttype";
  //source
  public static final String CLIENT_SOURCE  = "source";
  //source id
  public static final String CLIENT_SOURCE_ID  = "sourceid";
  //sink
  public static final String CLIENT_SINK = "sink";
  //result of the authentication= true or false
  public static final String CLIENT_AUTH_RESULT  = "authresult"; 
  //id of the socket
  public static final String CLIENT_ID  = "id";
  //comment
  public static final String CLIENT_COMMENT  = "comment";
  //list of sources
  public static final String CLIENT_SOURCE_LIST  = "sourcelist";
  // allowed operations
  public static final String CLIENT_ALLOWED_OPS  = "allowedoperations";
  //read
  public static final String CLIENT_READ  = "r";
  public static final String CLIENT_WRITE  = "w";
  public static final String CLIENT_EXECUTE  = "x"; //maybe
}
