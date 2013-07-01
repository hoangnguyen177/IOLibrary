package edu.monash.io.messagebased.socketio.source;


//java
import java.util.*;
// import java.util.concurrent.ConcurrentMap;
// import java.util.concurrent.ConcurrentHashMap;

//gson
import com.google.gson.*;

// IO library
import edu.monash.io.iolibrary.iointerface.BlockingIOInterface;
import edu.monash.io.iolibrary.ConfigurationConst;
import edu.monash.io.iolibrary.ConfigurationConst.DataType;
import edu.monash.io.iolibrary.ConfigurationHelper;
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.messagebased.MessageConsts;

//connection
import edu.monash.io.socketio.connection.*;
import edu.monash.io.socketio.exceptions.*;

import static edu.monash.io.socketio.ConnectionConsts.CLIENT_C_MESSAGE ;
import static edu.monash.io.socketio.ConnectionConsts.MESSAGE_C_MESSAGE_CODE ;

public class BlockingIO implements BlockingIOInterface{
	/*initialise the interface*/
	public void initialise(JsonObject _definition) throws IOFailException, InvalidDefinitionException{
		definition = _definition;
		source = new SourceConnection(); 
	    JsonObject connectionConfig = ConfigurationHelper.getConfigFromDefinition(definition);
		if(connectionConfig.has(ConfigurationConst.CONNECTION_HOST))
			source.setHost(connectionConfig.get(ConfigurationConst.CONNECTION_HOST));
		if(connectionConfig.has(ConfigurationConst.CONNECTION_PROTOCOL))
			source.setHost(connectionConfig.get(ConfigurationConst.CONNECTION_PROTOCOL));
		if(connectionConfig.has(ConfigurationConst.CONNECTION_NSP))
			source.setHost(connectionConfig.get(ConfigurationConst.CONNECTION_NSP));				
		if(connectionConfig.has(ConfigurationConst.CONNECTION_PORT))
			source.setHost(connectionConfig.get(Integer.parseInt(ConfigurationConst.CONNECTION_HOST)));
		if(connectionConfig.has(ConfigurationConst.CONNECTION_TIMEOUT))
			source.setHost(connectionConfig.get(Integer.parseInt(ConfigurationConst.CONNECTION_TIMEOUT)));
		source.setAuthInfo(config.getSourceInfo(_componentId));
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
			public void onMessage(JsonObject aMessage) throws InvalidMessageException{
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
	public void put(String path, Object value, boolean append) throws IOFailException{
		//now create the message
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.OBJECT);
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			source.send(message);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage);
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage);
		}
		
	}

	

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , Base64.encodeToString(value, false));
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.BYTEARRAY);
		jObject.addProperty(MessageConsts.APPEND , append);

		//add path, value and append
		try{
			source.send(message);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage);
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage);
		}		
	}


	/*put a file to specified path: append to the previous file name*/
	//fileType: text/binary
	public void putTextFile(String path, String filename, boolean append) throws IOFailException{
		//read the file in first
		try{
			byte[] contents = FileHelper.readTextFile(filename);
			JsonObject jObject = new JsonObject();
			jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
			jObject.addProperty(MessageConsts.PATH , path);
			jObject.addProperty(MessageConsts.DATA , Base64.encodeToString(contents, false));
			jObject.addProperty(MessageConsts.DATA_TYPE , DataType.TEXT.toString());
			jObject.addProperty(MessageConsts.FILE_NAME, filename);
			jObject.addProperty(MessageConsts.APPEND , append);
			//add path, value and append
			source.send(message);
		}
		catch(FileNotFoundException e){
			throw new IOFailException(e.getMessage());
		}
		catch(IOException e){
			throw new IOFailException(e.getMessage());
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage);
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage);
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
			source.send(message);
		}
		catch(FileNotFoundException e){
			throw new IOFailException(e.getMessage());
		}
		catch(IOException e){
			throw new IOFailException(e.getMessage());
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage);
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage);
		}

	}

	/*retrieve data held at specified path*/
	public String getString(String path){
		return get(path).getAsString();
	}

	/*retrieve double value held at specified path*/
	public Double getDouble(String path){
		return get(path).getAsDouble();
	}

	/*retrieve int value at specified path*/
	public int getInt(String path){
		return get(path).getAsInt();		
	}

	/*boolean*/
	public boolean getBoolean(String path){
		return get(path).getAsBoolean();
	}	
	/*get an object*/
	public Object getObject(String path){
		return get(path).getAsJsonObject();		
	}

	/**************************************/
	private synchronized JsonElement get(String path){
		boolean _wait = true;
		if(messages.containsKey(path)){
			try{
				if(DataType.STRING == DataType.fromString(messages.get(path).get(DATA_TYPE).getASString()))
					_wait= false;
			}
			catch(InvalidDataTypeException e){}
		}
		while(_wait){
			wait();
			if(messages.containsKey(path)){
			try{
				if(DataType.STRING == DataType.fromString(messages.get(path).get(DATA_TYPE).getASString()))
					_wait= false;
			}
			catch(InvalidDataTypeException e){}
			}
		}
		return messages.get(path).get(DATA);
	}
	//what to do with the messages
	private synchronized void processMessage(JsonObject aMessage){
		String _path = aMessage.get(MessageConsts.PATH).getAsString();
		if(_path!=null&& !path.isEmpty()){
			messages.putIfAbsent(_path, aMessage);
			notify();			
		}
	}

	//private variables
	private JsonObject definition = null;
	private SourceConnection source = null;
	private Map<String, JsonObject> messages = new HashMap<String, JsonObject>();




}