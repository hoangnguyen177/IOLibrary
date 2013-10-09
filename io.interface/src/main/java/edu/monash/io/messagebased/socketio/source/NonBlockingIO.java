package edu.monash.io.messagebased.socketio.source;


//java
import java.util.*;


//gson
import com.google.gson.*;

// IO library
import edu.monash.io.iolibrary.iointerface.NonBlockingIOInterface;
import edu.monash.io.iolibrary.ConfigurationConsts;
import edu.monash.io.iolibrary.ConfigurationConsts.DataType;
import edu.monash.io.iolibrary.ConfigurationHelper;
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.messagebased.MessageConsts;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.DataHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

//connection
import edu.monash.io.socketio.connection.*;
import edu.monash.io.socketio.exceptions.*;

import static edu.monash.io.socketio.connection.ConnectionConsts.CLIENT_C_MESSAGE ;
import static edu.monash.io.socketio.connection.ConnectionConsts.MESSAGE_C_MESSAGE_CODE ;



public class NonBlockingIO implements NonBlockingIOInterface{
	/*initialise the interface*/
	public void initialise(JsonObject _definition) throws IOFailException, InvalidDefinitionException{
		definition = _definition;
		source = ConnectionFactory.getInstance().getConnection(definition);
		source.addListener(new SourceListener(){
			public void onConnectionEstablished(){
				System.out.println("NonBlockingIO:: socket established");
			}
			public void onDisconnect(){
				System.out.println("NonBlockingIO:: disconnect");
			}
			public void onAuthResponse(JsonObject authResponse){
				System.out.println("NonBlockingIO:: auth response:" + authResponse.toString());
			}
			public void onMessage(JsonObject aMessage){
				System.out.println("NonBlockingIO:: new message: " + aMessage.toString());
				try{
					processMessage(aMessage);
				}
				catch(InvalidDataTypeException e){
					//ignored the message
				}
			}	
			public void onSinkDisconnect(){
				System.out.println("NonBlockingIO:: sink disconnect, no more sink");	
			}
			public void onSinkConnect(JsonArray sinkList){
				System.out.println("NonBlockingIO:: new sink connect, sinklist:" + sinkList.toString());	
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

	/*add handler so that it can be called back, note if wrong handler, string is return*/
	//basically you can add handler anywhere, but it does not mean you can have data returned
	public void addDataHandler(String path, DataHandler handler){
		if(!handlers.containsKey(path))
			handlers.put(path, handler);
	}

	/**
	* count the number of data handlers
	*/
	public boolean hasDataHandler(String path){
		return handlers.containsKey(path);
	}


	/*put value to specified path*/
	public void put(String path, JsonObject value, boolean append) throws IOFailException{
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



	/***************************************/
	private void processMessage(JsonObject aMessage) throws InvalidDataTypeException{
		if(!aMessage.has(MessageConsts.PATH))
			return;
		String _path = aMessage.get(MessageConsts.PATH).getAsString();
		DataHandler _handler = handlers.get(_path);
		if(_handler!=null){
			DataType _type = DataType.fromString(aMessage.get(MessageConsts.DATA_TYPE).getAsString());
			String _data = aMessage.get(MessageConsts.DATA).getAsString();
			try{
				if(_type == DataType.INT){
					int val = Integer.parseInt(_data);
					_handler.onInt(val);
				}
				else if(_type == DataType.DOUBLE){
					double val = Double.parseDouble(_data);
					_handler.onDouble(val);
				}
				else if(_type == DataType.BOOL){
					boolean val = Boolean.parseBoolean(_data);
					_handler.onBoolean(val);
				}
				else if(_type == DataType.OBJECT){
					JsonParser parser = new JsonParser();
					JsonObject _object = parser.parse(_data).getAsJsonObject();
					_handler.onObject(_object);
				}
				else if(_type == DataType.BYTEARRAY){
					//ignored
				}
				else if(_type == DataType.TEXT){
					//ignored
				}
				else if(_type == DataType.BINARY){
					//ignored
				}
				else{
					_handler.onString(_data);
				}
			}
			catch(NumberFormatException e){}
			catch(JsonSyntaxException e){}
		}

	}

	//private variables
	private JsonObject definition = null;
	private Map<String, DataHandler> handlers = new HashMap<String, DataHandler>();
	private SourceConnection source = null;

}