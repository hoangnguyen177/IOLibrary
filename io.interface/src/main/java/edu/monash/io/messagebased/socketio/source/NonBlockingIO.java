package edu.monash.io.messagebased.socketio.source;


//java
import java.util.*;


//gson
import com.google.gson.*;

// IO library
import edu.monash.io.iolibrary.iointerface.NonBlockingIOInterface;
import edu.monash.io.iolibrary.ConfigurationConst;
import edu.monash.io.iolibrary.ConfigurationConst.DataType;
import edu.monash.io.iolibrary.ConfigurationHelper;
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.messagebased.MessageConsts;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;


//connection
import edu.monash.io.socketio.connection.*;
import edu.monash.io.socketio.exceptions.*;

import static edu.monash.io.socketio.ConnectionConsts.CLIENT_C_MESSAGE ;
import static edu.monash.io.socketio.ConnectionConsts.MESSAGE_C_MESSAGE_CODE ;



public class NonBlockingIO implements NonBlockingIOInterface{
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

	/*add handler so that it can be called back, note if wrong handler, string is return*/
	//basically you can add handler anywhere, but it does not mean you can have data returned
	public void addDataHandler(String path, DataHandler handler){
		if(!handlers.contains(path))
			handler.put(path, handler);
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



	/***************************************/
	private void processMessage(JsonObject aMessage) throws InvalidDataTypeException{
		String _path = aMessage.get(MessageConsts.PATH).getAsString();
		DataHandler _handler = handlers.get(_path);
		if(_handler!=null){
			DataType _type = DataType.fromString(aMessage.get(MessageConsts.DATA_TYPE).getAsString());
			String _data = aMessage.get(MessageConsts.DATA);
			try{
				if(_type == DataType.INT){
					int val = Integer.parseInt(_data);
					_handler.onInt(val);
				}
				else if(_type == DataType.DOUBLE){
					double val = Double.parseDouble(_data);
					_handler.onInt(val);
				}
				else if(_type == DataType.BOOL){
					boolean val = Boolean.parseBoolean(_data);
					_handler.onInt(val);
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

		}

	}

	//private variables
	private JsonObject definition = null;
	private Map<String, DataHandler> handlers = new HashMap<String, DataHandler>();
	private SourceConnection source = null;

}