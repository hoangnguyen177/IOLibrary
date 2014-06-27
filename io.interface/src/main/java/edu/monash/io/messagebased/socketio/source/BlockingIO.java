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
		source = ConnectionFactory.getInstance().getConnection(definition);
		source.addListener(new SourceListener(){
			public void onConnectionEstablished(){
				System.out.println("BlockingIO:: socket established");
			}
			public void onDisconnect(){
				System.out.println("BlockingIO:: disconnect");
			}
			public void onAuthResponse(JsonObject authResponse){
				System.out.println("BlockingIO:: auth response:" + authResponse.toString());
			}
			public void onMessage(JsonObject aMessage){
				System.out.println("BlockingIO:: new message: " + aMessage.toString());
				processMessage(aMessage);
			}	
			public void onSinkDisconnect(){
				System.out.println("BlockingIO:: sink disconnect, no more sink");	
			}
			public void onSinkConnect(JsonArray sinkList){
				System.out.println("BlockingIO:: new sink connect, sinklist:" + sinkList.toString());	
			}
		});
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
			if(waitForSink)
				this.waitTillSinkConnect();
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}
		
	}

	//put string
	public void putString(String path, String value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.STRING.toString());
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			if(waitForSink)
				this.waitTillSinkConnect();
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}
	}

	//put int
	public void putInt(String path, int value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.INT.toString());
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			if(waitForSink)
				this.waitTillSinkConnect();
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}
	}

	//double
	public void putDouble(String path, double value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.DOUBLE.toString());
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			if(waitForSink)
				this.waitTillSinkConnect();
			source.send(jObject);
		}
		catch(ConnectionFailException e){
			throw new IOFailException(e.getMessage());
		}
		catch(UnauthcatedClientException e){
			throw new IOFailException(e.getMessage());
		}
	}

	//boolean
	public void putBoolean(String path, boolean value, boolean append) throws IOFailException{
		JsonObject jObject = new JsonObject();
		//tells server that this is a message
		jObject.addProperty(MESSAGE_C_MESSAGE_CODE, CLIENT_C_MESSAGE);
		jObject.addProperty(MessageConsts.PATH , path);
		jObject.addProperty(MessageConsts.DATA , value);
		jObject.addProperty(MessageConsts.DATA_TYPE , DataType.BOOL.toString());
		jObject.addProperty(MessageConsts.APPEND , append);
		//add path, value and append
		try{
			if(waitForSink)
				this.waitTillSinkConnect();
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
			if(waitForSink)
				this.waitTillSinkConnect();
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
			if(waitForSink)
				this.waitTillSinkConnect();
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
			if(waitForSink)
				this.waitTillSinkConnect();
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
	/*set wait for sink*/
	public void setWaitForSink(boolean _wait){
		waitForSink = _wait;
	}

	public boolean getWaitForSink(){
		return waitForSink;
	}


	/**************************************/
	//wait till a sink connect
	private void waitTillSinkConnect(){
		while(source.getNumberOfSinks()<=0){
			try{
				Thread.sleep(100);
			}	
			catch(InterruptedException e){}			
		}
	}


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
		//different message from server
		//ifnore
		if(!aMessage.has(MessageConsts.PATH))
			return;
		String _path = aMessage.get(MessageConsts.PATH).getAsString();
		if(_path!=null&& !_path.isEmpty()){
			messages.put(_path, aMessage);
			notify();			
		}
	}

	//private variables
	private boolean waitForSink   = true;
	private JsonObject definition = null;
	private SourceConnection source = null;
	private Map<String, JsonObject> messages = new HashMap<String, JsonObject>();




}