package edu.monash.io.socketio.client;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import java.io.*;

//logging
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.Iterator;

public abstract class Connection{
	// constants
	final static int 							DETAULT_TIMEOUT 	= 3000			;
    final static int 							DETAULT_PORT 		= 8080			;
    final static String 						DETAULT_PROCTOCOL 	= "http"		;
    final static String 						DETAULT_HOST 		= "localhost"	;
    final static String 						DETAULT_NSP 		= "/"			;

    //public variables
    public static final Log _log 				= LogFactory.getLog(IOActor.class.getName());
    public static final boolean _debugging	 	= _log.isDebugEnabled();

    //private variables
    private 	int 							timeout 			= DEFAULT_TIMEOUT	;
    private 	int 							port 				= DEFAULT_PORT		;
    private 	String 							protocol 			= DEFAULT_PROTOCOL	;
    private 	String 							host 				= DEFAULT_HOST		;
    private 	String 							nsp					= DEFAULT_NSP		;

	// the socket for connection
	private 	Socket 							socket; 

	//the authentication information: username, pass, ....
	private 	JsonObject 						authInfo;
	
	//is authenticated or not
	private 	boolean 						isAuthcated	= false;

	//is conenction established
	private 	boolean							isConnectionEstablished = false;

	//list of ConnectionListeners
	private 	LinkedList<ConnectionListener> 	listeners 	= new LinkedList<ConnectionListener>();


	/********************************************/
	/**
	* consutrctor
	*/
	public Connection(){
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
	public String 			getPort			() 									{ return port;	 				}	 					
	//auth info
	public void 			setAuthInfo		(JsonObject _authInfo) 				{ authInfo = _authInfo;			}
	public JsonObject 		getAuthInfo		() 									{return _authInfo;				}
	//socket
	public void 			setSocket		(Socket _socket)					{ socket = _socket;				}
	public Socket 			getSocket		()									{ return socket;				}

	//is authcated
	public boolean 			authcated 		()									{ return isAuthcated;			}
	public void 			setAuthcated	(boolean _authcated)				{ isAuthcated = _authcated;		}

	//connection established
	public boolean			connectionEstablished()								{return isConnectionEstablished;}
	public boolean			setConEstablished	(boolean _b)					{isConnectionEstablished = _b;	}

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
		return this.protocol + "://" + this.localhost + ":" + this.port + this.nsp;
	}

	/**
	*
	*/
	public static String log(String msg){
	  	if(Connection._debugging)
	  		Connection._log(msg);
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
			  	LinkedList<ConnectionListener> listeners = this.getListeners();
  			  	Iterator<ConnectionListener> iterator = listeners.iterator();
		  		while(iterator.hasNext()){
		  			ConnectionListener _listener = iterator.next();
		  			_listener.onConnectionEstablished();
		  		}
			    //send the credentials
				this.getSocket().emit(ConnectionConsts.CLIENT_C_AUTH, this.getAuthInfo()); 	

			  }
			}).on(ConnectionConsts.CLIENT_C_MESSAGE, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			  	JsonObject obj = (JsonObject)args[0];
			  	log("[SinkConnection] message:" + obj.toString);
			  	notifyListeners(obj);
			  }
			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			  @Override
			  public void call(Object... args) {
			  	log("[SinkConnection] Socket disconnected");
			  	LinkedList<ConnectionListener> listeners = this.getListeners();
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
			return new ConnectionFailException(e.getMessage);
		}
	}

	/**
	* notifyListeners
	*/
	protected abstract void notifyListeners(JsonObject message);

	


}
