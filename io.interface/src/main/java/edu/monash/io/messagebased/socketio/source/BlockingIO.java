package edu.monash.io.messagebased.socketio.source;


//java
import java.util.*;
// import java.util.concurrent.ConcurrentMap;
// import java.util.concurrent.ConcurrentHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
//gson
import com.google.gson.*;

// IO library
import edu.monash.io.iolibrary.iointerface.BlockingIOInterface;
import edu.monash.io.iolibrary.ConfigurationConsts;
import edu.monash.io.iolibrary.ConfigurationConsts.DataType;
import edu.monash.io.iolibrary.ConfigurationHelper;
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.messagebased.MessageConsts;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
//connection
import edu.monash.io.socketio.connection.*;
import edu.monash.io.socketio.exceptions.*;

import static edu.monash.io.socketio.connection.ConnectionConsts.CLIENT_C_MESSAGE ;
import static edu.monash.io.socketio.connection.ConnectionConsts.MESSAGE_C_MESSAGE_CODE ;

public class BlockingIO implements BlockingIOInterface{
	/*initialise the interface*/
	public void initialise(JsonObject _definition) throws IOFailException, InvalidDefinitionException{
		definition = _definition;
		source = new SourceConnection(); 
	    JsonObject connectionConfig = ConfigurationHelper.getConfigFromDefinition(definition);
		if(connectionConfig.has(ConfigurationConsts.CONNECTION_HOST))
			source.setHost(connectionConfig.get(ConfigurationConsts.CONNECTION_HOST).getAsString());
		if(connectionConfig.has(ConfigurationConsts.CONNECTION_PROTOCOL))
			source.setProtocol(connectionConfig.get(ConfigurationConsts.CONNECTION_PROTOCOL).getAsString());
		if(connectionConfig.has(ConfigurationConsts.CONNECTION_NSP))
			source.setNsp(connectionConfig.get(ConfigurationConsts.CONNECTION_NSP).getAsString());				
		if(connectionConfig.has(ConfigurationConsts.CONNECTION_PORT))
			source.setPort(connectionConfig.get(ConfigurationConsts.CONNECTION_HOST).getAsInt());
		if(connectionConfig.has(ConfigurationConsts.CONNECTION_TIMEOUT))
			source.setTimeout(connectionConfig.get(ConfigurationConsts.CONNECTION_TIMEOUT).getAsInt());
		source.setAuthInfo(definition);
		source.addListener(new SourceListener(){
			public void onConnectionEstablished(){
				System.out.println("TestSourceClient:: socket established");
			}
			public void onDisconnect(){
				System.out.println("TestSourceClient:: disconnect");
			}
			public void onAuthResponse(JsonObject authResponse){
				System.out.println("TestSourceClient:: auth response:" + authResponse.toString());
			}
			public void onMessage(JsonObject aMessage){
				System.out.println("TestSourceClient:: new message: " + aMessage.toString());
				processMessage(aMessage);
			}	
			public void onSinkDisconnect(){
				System.out.println("TestSourceClient:: sink disconnect, no more sink");	
			}
			public void onSinkConnect(JsonArray sinkList){
				System.out.println("TestSourceClient:: new sink connect, sinklist:" + sinkList.toString());	
			}
		});
		try{
			//create the socket
            //connect
			source.connect();
			//wait till this source is authenticated
			while(!source.authcated()){
				try{
					Thread.sleep(600);
				}
				catch(Exception e){}
			}//end small while	

        }
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
        catch(JsonSyntaxException e){
            throw new InvalidDefinitionException(e.getMessage());
        }
	}

	/*get the definition of the operation*/
	public String getDefinition(){
		if(definition!=null)
			return definition.toString();
		else
			return "";
	}

	/*put value to specified path*/
	public void put(String path, JsonObject value, boolean append) throws IOFailException{
		//now create the message
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.add(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.OBJECT.toString());
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}
		
	}

	

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , Base64.encodeToString(value, false));
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.BYTEARRAY.toString());
		jObject.addProperty(MessageConsts.APPEND , append);

		//add path, value and append
		try{
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}		
	}


	/*put a file to specified path: append to the previous file name*/
	//fileType: text/binary
	public void putTextFile(String path, String filename, boolean append) throws IOFailException{
		//read the file in first
		try{
			String contents = FileHelper.readTextFile(filename);
			JsonObject jObject = new JsonObject();
			jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
			jObject.addProperty(MessageConsts.PATH , path);
			jObject.addProperty(MessageConsts.DATA , contents);
			jObject.addProperty(MessageConsts.DATA_TYPE , DataType.TEXT.toString());
			jObject.addProperty(MessageConsts.FILE_NAME, filename);
			jObject.addProperty(MessageConsts.APPEND , append);
			//add path, value and append
			source.send(jObject);
		}
		catch(FileNotFoundException e){
			throw new IOFailException(e.getMessage());
		}
		catch(IOException e){
			throw new IOFailException(e.getMessage());
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}

	}

	public void putBinaryFile(String path, String filename, boolean append) throws IOFailException{
		//read the file in first
		try{
			byte[] contents = FileHelper.readBinaryFile(filename);
			JsonObject jObject = new JsonObject();
			jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
			jObject.addProperty(MessageConsts.PATH , path);
			jObject.addProperty(MessageConsts.DATA , Base64.encodeToString(contents, false));
			jObject.addProperty(MessageConsts.DATA_TYPE , DataType.BINARY.toString());
			jObject.addProperty(MessageConsts.FILE_NAME, filename);
			jObject.addProperty(MessageConsts.APPEND , append);
			//add path, value and append
			source.send(jObject);
		}
		catch(FileNotFoundException e){
			throw new IOFailException(e.getMessage());
		}
		catch(IOException e){
			throw new IOFailException(e.getMessage());
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}

	}

	/*retrieve data held at specified path*/
	public String getString(String path){
		return get(path, DataType.STRING).getAsString();
	}

	/*retrieve double value held at specified path*/
	public Double getDouble(String path){
		return get(path, DataType.DOUBLE).getAsDouble();
	}

	/*retrieve int value at specified path*/
	public int getInt(String path){
		return get(path, DataType.INT).getAsInt();		
	}

	/*boolean*/
	public boolean getBoolean(String path){
		return get(path, DataType.BOOL).getAsBoolean();
	}	
	/*get an object*/
	public JsonObject getObject(String path){
		return get(path, DataType.OBJECT).getAsJsonObject();		
	}

	/**************************************/
	private synchronized JsonElement get(String path, DataType _dataType){
		boolean _wait = true;
		if(messages.containsKey(path)){
			try{
				DataType _messageType = DataType.fromString(messages.get(path).get(MessageConsts.DATA_TYPE).getAsString());
				if(_dataType == _messageType)
					_wait= false;
			}
			catch(InvalidDataTypeException e){}
		}
		while(_wait){
			try{
				wait();
			}
			catch(InterruptedException e){}
			if(messages.containsKey(path)){
				try{
					DataType _messageType = DataType.fromString(messages.get(path).get(MessageConsts.DATA_TYPE).getAsString());
					if(_dataType == _messageType)
						_wait= false;
				}
				catch(InvalidDataTypeException e){}
			}
		}
		return messages.get(path).get(MessageConsts.DATA);
	}
	//what to do with the messages
	private synchronized void processMessage(JsonObject aMessage){
		String _path = aMessage.get(MessageConsts.PATH).getAsString();
		if(_path!=null&& !_path.isEmpty()){
			messages.put(_path, aMessage);
			notify();			
		}
	}

	//private variables
	private JsonObject definition = null;
	private SourceConnection source = null;
	private Map<String, JsonObject> messages = new HashMap<String, JsonObject>();




}