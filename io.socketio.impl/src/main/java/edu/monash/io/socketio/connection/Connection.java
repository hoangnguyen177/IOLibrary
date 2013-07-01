package edu.monash.io.socketio.connection;

// java api of socketio
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
//gson
import com.google.gson.*;
//java
import java.io.*;
//logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//java utils
import java.util.*;
//import all the exceptions
import edu.monash.io.socketio.exceptions.*;

public abstract class Connection{

	//log
	private Log _log 												= null;

    //private variables
    private 	int 							timeout 					;
    private 	int 							port 						;
    private 	String 							protocol 					;
    private 	String 							host 						;
    private 	String 							nsp							;

	// the socket for connection
	private 	Socket 							socket; 

	//the authentication information: username, pass, ....
	protected 	JsonObject 						authInfo 				= null;
	
	//is authenticated or not
	private 	boolean 						isAuthcated				= false;

	//is conenction established
	private 	boolean							isConnectionEstablished = false;

	//list of ConnectionListeners
	private 	LinkedList<ConnectionListener> 	listeners 				= new LinkedList<ConnectionListener>();


	/********************************************/
	/**
	* consutrctor
	*/
	public Connection(){
		host 		= "localhost";
		protocol 	= "http";
		timeout 	= 3000;
		port 		= 8080;
		nsp 		= "/";
	}

	public Connection(String _host, String _protocol, int _timeout, int _port, String _nsp){
		host 		= _host;
		protocol 	= _protocol;
		timeout 	= _timeout;
		port 		= _port;
		nsp 		= _nsp;
	}

	/* gets and sets*/
	//host
	public void 			setHost			(String _host) 						{ host = _host; 				}
	public String 			getHost			() 									{ return host;	 				}	 					
	//host
	public void 			setNsp			(String _nsp) 						{ nsp  =  _nsp; 				}
	public String 			getNsp			() 									{ return nsp;	 				}	 					
	//protocol
	public void 			setProtocol		(String _proc) 						{ protocol = _proc;				}
	public String 			getProtocol		() 									{ return protocol; 				}	 					
	//port
	public void 			setPort			(int _port) 						{ port = _port; 				}
	public int 	 			getPort			() 									{ return port;	 				}	 					
	//auth info
	public void 			setAuthInfo		(JsonObject _authInfo) 				{ authInfo = _authInfo;			}
	public JsonObject 		getAuthInfo		() 									{return authInfo;				}
	//socket
	public void 			setSocket		(Socket _socket)					{ socket = _socket;				}
	public Socket 			getSocket		()									{ return socket;				}

	//is authcated
	public boolean 			authcated 		()									{ return isAuthcated;			}
	public void 			setAuthcated	(boolean _authcated)				{ isAuthcated = _authcated;		}

	//connection established
	public boolean			connectionEstablished()								{return isConnectionEstablished;}
	public void				setConEstablished	(boolean _b)					{isConnectionEstablished = _b;	}

	// add listener
	public void 			addListener		(ConnectionListener _listener)		{ listeners.add(_listener);   	}
	// remove listeners
	public void 			removeListener	(ConnectionListener _listener)		{ listeners.remove(_listener);	}
	//clear listeners
	public void 			clearListeners	()									{ listeners.clear();			}
	//get listeners
	public LinkedList<ConnectionListener> getListeners()						{return listeners;				}
	
	/**
	* connect
	*/
	public void connect() throws ConnectionFailException{
		if(!isConnectionEstablished)
			this.establishConnection();
		socket.connect();
	}

	/**
	* disconnect
	*/
	public void disconnect() throws ConnectionFailException{
		if(socket == null)
			throw new ConnectionFailException("Socket is null");
		socket.disconnect();
	}

	/**
	* send message 
	*/
	public void send(JsonObject message) throws ConnectionFailException, UnauthcatedClientException{
		if(socket == null)
			throw new ConnectionFailException("Socket is null");
		if(!this.authcated())
			throw new UnauthcatedClientException("Not authcated yet");
		socket.emit(ConnectionConsts.CLIENT_C_MESSAGE, message);
	}


	/**
	* connection path
	*/
	public String getConnectionPath(){
		return this.protocol + "://" + this.host + ":" + this.port + this.nsp;
	}

	/**
	* abstract method log
	*/
	public void log(String msg){
		if(_log == null)
			_log = LogFactory.getLog(this.getClass().getName());
		if(_log.isDebugEnabled()){
			_log.info(msg);
		}
	}

	/**
	* establish the connection
	*/
	private void establishConnection() throws ConnectionFailException{
		try{
			IO.Options opts = new IO.Options();
			opts.forceNew = true;
			//opts.path = "http://localhost:8080/socket.io/socket.io.js";
			socket = IO.socket(this.getConnectionPath(), opts);	
			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			  	Iterator<ConnectionListener> iterator = listeners.iterator();
		  		while(iterator.hasNext()){
		  			ConnectionListener _listener = iterator.next();
		  			_listener.onConnectionEstablished();
		  		}
			    //send the credentials
				socket.emit(ConnectionConsts.CLIENT_C_AUTH, authInfo); 	
			  }
			}).on(ConnectionConsts.CLIENT_C_MESSAGE, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			  	JsonObject msg = (JsonObject)args[0];
			  	log("[Connection::establishConnection] message:" + msg.toString());
			  	try{
				  	notifyListeners(msg);
			  	}
			  	catch(InvalidMessageException e){
					log("[Connection::establishedConnection] exception: " + e.getMessage());
				}
			  }//end call
			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			  	log("[Connection::establishConnection] Socket disconnected");
			  	Iterator<ConnectionListener> iterator = listeners.iterator();
		  		while(iterator.hasNext()){
		  			ConnectionListener _listener = iterator.next();
		  			_listener.onDisconnect();
		  		}
			  }
			});
			//socket is established
			this.setConEstablished(true);
		}
		catch(Exception e){
			throw new ConnectionFailException("[Connection:establishConnection] connection failed");
		}
	}

	/**
	* notifyListeners
	*/
	protected abstract void notifyListeners(JsonObject message) throws InvalidMessageException;

	


}
