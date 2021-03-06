package edu.monash.io.socketio.connection;
//gson
import com.google.gson.*;
//import all the exceptions
import edu.monash.io.socketio.exceptions.*;
//java util
import java.util.*;

import java.sql.Timestamp;
/**
 * SinkConnection
 * @author hoangnguyen
 */
public class SinkConnection extends Connection{
	
    /*list of sources to choose from*/
	private Set<Map.Entry<String,JsonElement>> sourceList = null;
	/*true if done selection*/
	private boolean doneSelection = false;
	/*permission*/
	private String permission = "";
	/*connected source*/
	private String connectedSourceId = "";


	/*gets and sets*/
	public boolean 	doneSelection() {return doneSelection;}

	public String 	getPermission() {return permission;}

	public String 	getConnectedSource() {return connectedSourceId;}

	/**
	* select a source from the source list
	*/
	public void selectSource(String sourceId, Timestamp timeStamp) throws UnauthcatedClientException, InvalidSourceException{
		if(!authcated())
			throw new UnauthcatedClientException("[SinkConnection::selectSource] not authcated yet");
		if(!checkSourceInSourceList(sourceId))
			throw new InvalidSourceException("[SinkConnection::selectSource] source id is not in the source list");
		 //emit the signal
		//this.getSocket().emit(sourceId);
		this.getSocket().emit(ConnectionConsts.CLIENT_C_SELECT, sourceId, timeStamp);
		connectedSourceId = sourceId;
		doneSelection = true;
	}
	
	/**
	* find source in the sourceslist
	*/
	public boolean checkSourceInSourceList(String sourceId){
		//check whether sourceId is in source list
		if(sourceList== null)
			return false;
		Iterator<Map.Entry<String, JsonElement>> _iterator = sourceList.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> _aSource = _iterator.next();
			if(_aSource.getKey().equals(sourceId))
				return true;
		}
		return false;
	}

	/**
	* checks whether the virtual conenction is still there
	*/
	public boolean isConnected(){
		if(connectedSourceId!=null && !connectedSourceId.trim().isEmpty())
			return checkSourceInSourceList(connectedSourceId);
		else
			return false;
	}

	/**
	* get the sourcelisst
	*/
	public Set<Map.Entry<String,JsonElement>> getSourceList(){
		return sourceList;
	}

	/**
	* notify listeners
	*/		
	protected void notifyListeners(JsonObject message) throws InvalidMessageException{
		String msgCode = message.get(ConnectionConsts.MESSAGE_C_MESSAGE_CODE).getAsString();
	  	if(msgCode == null || msgCode.trim().isEmpty())
	  		throw new InvalidMessageException("[SinkConnection::notifyListeners] No message code. Invalid message");
	  	LinkedList<ConnectionListener> listeners = this.getListeners();
  		if(msgCode.equals(ConnectionConsts.SERVER_C_AUTH_RETURN)){
	  		log("[SinkConnection] Authentication return");
	  		//get the result
	  		String _authResult = message.get(ConnectionConsts.MESSAGE_C_AUTH_RESULT).getAsString();
	  		if(_authResult == null || _authResult.trim().isEmpty())
	  			throw new InvalidMessageException("[SinkConnection::notifyListeners] authresult is null or empty, invalid");
	  		if(Boolean.parseBoolean(_authResult))
		  		this.setAuthcated(true);
		  	JsonObject _sourceList = message.getAsJsonObject(ConnectionConsts.MESSAGE_C_SOURCE_LIST);
	  		if(_sourceList != null )
	  			sourceList = _sourceList.entrySet();
	  		else return;
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			_listener.onAuthResponse(message);
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_SOURCE_DISCONNECT)){
	  		JsonObject _sourceList = message.getAsJsonObject(ConnectionConsts.MESSAGE_C_SOURCE_LIST);
	  		if(_sourceList != null )
	  			sourceList = _sourceList.entrySet();
	  		else
	  			return;
	  		log("[SinkConnection] source disconnect, source list:" + sourceList);
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SinkListener){
	  				SinkListener _sinkListener = (SinkListener)_listener;
	  				_sinkListener.onSourceDisconnect();
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_PERMISSION_CHANGED)){
	  		String _newPermission = message.get(ConnectionConsts.MESSAGE_C_ALLOWED_OPS).getAsString();
	  		log("[SinkConnection] permission changed:" + _newPermission);
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		permission = _newPermission;
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SinkListener){
	  				SinkListener _sinkListener = (SinkListener)_listener;
	  				_sinkListener.onPermissionChanged(_newPermission);
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_SOURCE_CONNECT)){
	  		JsonObject _sourceList = message.getAsJsonObject(ConnectionConsts.MESSAGE_C_SOURCE_LIST);
	  		if(_sourceList != null )
	  			sourceList = _sourceList.entrySet();
	  		else return;
	  		log("[SinkConnection] source connect, list of sources:" + _sourceList.toString());
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SinkListener){
	  				SinkListener _sinkListener = (SinkListener)_listener;
	  				_sinkListener.onSourceConnect(_sourceList);
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.SERVER_C_CONNECTION_ESTABLISHED)){
	  		String _permission = message.get(ConnectionConsts.MESSAGE_C_ALLOWED_OPS).getAsString();
	  		permission = _permission;
	  		doneSelection = true;
	  		log("[SinkConnection] Connection established permission:" + _permission);
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			if(_listener instanceof SinkListener){
	  				SinkListener _sinkListener = (SinkListener)_listener;
	  				_sinkListener.onLinkEstablished(_permission);
	  			}
	  		}
	  	}
	  	else if(msgCode.equals(ConnectionConsts.CLIENT_C_MESSAGE)){
	  		log("[SinkConnection] Message from source");
	  		Iterator<ConnectionListener> iterator = listeners.iterator();
	  		while(iterator.hasNext()){
	  			ConnectionListener _listener = iterator.next();
	  			_listener.onMessage(message);
	  		}
	  	}
	}

}
