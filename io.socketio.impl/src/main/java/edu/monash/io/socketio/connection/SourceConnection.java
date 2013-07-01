package edu.monash.io.socketio.connection;

//gson
import com.google.gson.*;
//import all the exceptions
import edu.monash.io.socketio.exceptions.*;
//java util
import java.util.*;

/**
 * SourceConnection
 * @author hoangnguyen
 */
public class SourceConnection extends Connection{

	//set auth info
	public void setAuthInfo	(JsonObject _authInfo){ 
		authInfo = _authInfo;			
		authInfo.addProperty(ConnectionConsts.CONNECTION_C_CLIENT_TYPE, ConnectionConsts.CONNECTION_C_SOURCE);
	}	

	/**
	* notify listeners
	*/		
	protected void notifyListeners(JsonObject message) throws InvalidMessageException{
		String msgCode = message.get(ConnectionConsts.MESSAGE_C_MESSAGE_CODE).getAsString();
	  	if(msgCode == null || msgCode.trim().isEmpty())
	  		throw new InvalidMessageException("[SourceConnection::notifyListeners] No message code. Invalid message");
	  	LinkedList<ConnectionListener> listeners = this.getListeners();
  		if(msgCode.equals(ConnectionConsts.SERVER_C_AUTH_RETURN)){
	  		log("[SourceConnection] Authentication return");
	  		//get the result
	  		String _authResult = message.get(ConnectionConsts.MESSAGE_C_AUTH_RESULT).getAsString();
	  		if(_authResult == null || _authResult.trim().isEmpty())
	  			throw new InvalidMessageException("[SourceConnection::notifyListeners] authresult is null or empty, invalid");
	  		if(Boolean.parseBoolean(_authResult))
		  		this.setAuthcated(true);
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			_listener.onAuthResponse(message);
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_SINK_DISCONNECT)){
	  		log("[SourceConnection] sink disconnect, no more sink");
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		numberOfSinks = 0;
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SourceListener){
	  				SourceListener _sourceListener = (SourceListener)_listener;
	  				_sourceListener.onSinkDisconnect();
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_SINK_CONNECT)){
	  		JsonArray _sinkArray = message.getAsJsonArray(ConnectionConsts.MESSAGE_C_SINK_LIST);
	  		numberOfSinks = _sinkArray.size();
	  		log("[SourceConnection] sink connect, list of sinks:" + _sinkArray.toString());
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SourceListener){
	  				SourceListener _sourceListener = (SourceListener)_listener;
	  				_sourceListener.onSinkConnect(_sinkArray);
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.CLIENT_C_MESSAGE)){
	  		log("[SourceConnection] Message from source");
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			_listener.onMessage(message);
	  		}
	  	}
	}




	/**
	* number of connected sinks
	*/
	public int getNumberOfSinks(){
		return numberOfSinks;
	}



	////////////////////////////
	private int numberOfSinks = 0;
}
