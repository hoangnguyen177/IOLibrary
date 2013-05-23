package edu.monash.io.socketio;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
//import all the exceptions
import edu.monash.io.socketio.exceptions.*;

/**
 * SourceConnection
 * @author hoangnguyen
 */
public interface SourceConnection extends Connection{

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
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SourceListener){
	  				SourceListener _sourceListener = (SourceListener)_listener;
	  				_sourceListener.onSinkDisconnect();
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_SINK_CONNECT)){
	  		JsonArray _sinkArray = obj.getAsJsonArray(ConnectionConsts.MESSAGE_C_SINK_LIST);
	  		log("[SourceConnection] sink connect, list of sources:" + _sinkArray.toString());
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SinkListener){
	  				SinkListener _sinkListener = (SinkListener)_listener;
	  				_sinkListener.onSourceConnect(_sinkArray);
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


}
