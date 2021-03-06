package edu.monash.io.iolibrary.ioobjects;

//java 
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
//gson
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
//IO Library
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidIOTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.iointerface.IOFactory;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.iointerface.BlockingIOInterface;
import edu.monash.io.iolibrary.iointerface.NonBlockingIOInterface;
import edu.monash.io.iolibrary.ConfigurationConsts.IoType;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.NAME;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE_CHANNEL;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONFIG;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_HOST;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_PROTOCOL;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_PORT;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_USENAME;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_PASS;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_TIMEOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONNECTION_NSP;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONTAINERID;
import static edu.monash.io.iolibrary.ConfigurationConsts.LAYOUT_VERTICAL;
import static edu.monash.io.iolibrary.ConfigurationConsts.LAYOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.WAIT_FOR_SINK;


public class IOChannel extends IOObject{
	/*cosntructor*/
	public IOChannel(){
		super();
		variables = new HashMap<String, IOVariable>();
		this.setType(TYPE_CHANNEL);
		layout = LAYOUT_VERTICAL;
	}


	/*variables*/
	public IOVariable getVariable(String _varName){	
		return variables.get(_varName);	
	}
	
	public void removeVariable(String _varName){
		variables.remove(_varName);
	}
	
	public void addVariable(String _varName, IOVariable _var){
		if(!variables.containsKey(_varName))
			variables.put(_varName, _var);
	}

	public Map<String, IOVariable> getVariables(){
		return variables;
	}

	/*layout*/
	public void setLayout(String _layout)				{	layout = _layout;	}
	public String getLayout()							{	return layout;		}

	/*container id*/
	public void setContainerId(String _conId)			{	containerId = _conId;	}
	public String getContainerId()						{	return containerId;		}

	/*configuration*/
	public JsonObject getConfig()						{	return configuration;	}
	public void setConfiguration(JsonObject _config)	{	configuration = _config;}

	/*waitfor sink*/
	public void setWaitForSink(boolean _wait)			{ waitforsink = _wait; }
	public boolean getWaitForSink()						{ return waitforsink;  }
	// //not needed
	// public void setConfiguration(){
	// 	about = new JsonObject();
	// 	about.addProperty();
	// }

	/*blocking io*/
	public BlockingIOInterface getBlockingIO(boolean shared, String _subPath) throws NotSupportException, InvalidPathException{
		String _path = this.getContainerId();
		if(!shared)
			_path += "." + this.getId(); 
		if(_subPath.trim().isEmpty() || (!variables.containsKey(_subPath) && !"*".equals(_subPath.trim())) )
			throw new InvalidPathException("There is no variable:" + _subPath); 
		BlockingIOInterface _blockingInterface= IOFactory.getInstance().getBlockingIOInterface(_path);
		return _blockingInterface;
	}

	
	/*non blocking io*/
	public NonBlockingIOInterface getNonBlockingIO(boolean shared, String _subPath) throws NotSupportException, InvalidPathException{
		String _path = this.getContainerId();
		if(!shared)
			_path += "." + this.getId(); 
		if(_subPath.trim().isEmpty() || (!variables.containsKey(_subPath) && !"*".equals(_subPath.trim())) )
			throw new InvalidPathException("There is no variable:" + _subPath); 
//		return IOFactory.getInstance().getNonBlockingIOInterface(_path);
		NonBlockingIOInterface _nonblockingInterface= IOFactory.getInstance().getNonBlockingIOInterface(_path);
		return _nonblockingInterface;
	}

	

	/*get objects*/
	public JsonObject getObject(){
		JsonObject _obj = super.getObject();
		_obj.addProperty(LAYOUT, layout);
		_obj.addProperty(CONTAINERID, containerId);
		_obj.addProperty(WAIT_FOR_SINK, this.getWaitForSink());
		_obj.add(CONFIG, configuration);
		//add variables into the object as well
		Set<String> _keySet = variables.keySet();
		Iterator<String> _it = _keySet.iterator();
		while(_it.hasNext()){
			String _key = _it.next();
			_obj.add(_key, variables.get(_key).getObject());
		}
		return _obj;
	}

	///////////////////////////////
	/*private variables/methods*/
	/**
	* establish the IO connection based on the configuration provided
	* containerDefinition is provided to create a proper definition, including: container + channel
	* if the channel is shared in a container, the path will be container id
	*/
	public void registerIO(JsonObject containerDefinition, boolean shared) throws InvalidDefinitionException, IOFailException{
		//create connection
		if(configuration == null)
			throw new InvalidDefinitionException("Configuration cannot be null");
		//create definition based on containerDefinition
		JsonObject _definition = new JsonObject();
		_definition.add(this.getContainerId(), containerDefinition);
		_definition.add(this.getId(), this.getObject());
		//create the connection
		String _path = this.getContainerId();
		if(!shared)
			_path += "." + this.getId(); 
		IOFactory.getInstance().registerIO(_path, _definition);
	}

	/**
	* this method receives an JsonObject and parses it to IOChannel
	*/
	// channelid:{															NON-KEY		
	// 	//name of the operations, unique in each channel
	// 	type: channel, 
	// 	about:{															
	// 			label: label here,											
	// 			description: description here,									
	// 			hint: hint here,												
	// 	},
	// 	configuration:{
	// 		type: socketio,
	// 		host:"localhost",
	// 		port: 8080,
	// 		username: blah,
	// 		pass: blah,
	// 	}, 
	// 	containerid: containerID,
	// 	layout: vertical, //default
	// 	input1:{															NON-KEY
	// 	....}, 
	// }
	public static IOChannel parseIOChannel(JsonObject channelContents)throws InvalidDefinitionException, 
																		InvalidDataTypeException, InvalidIOTypeException
	{
		if(channelContents==null)
			throw new InvalidDefinitionException("[parseIOChannel@IOChannel] channel contents cannot be null here");
		IOChannel _channel = new IOChannel();
		Set<Map.Entry<String,JsonElement>> _entries = channelContents.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _iterator = _entries.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> entry = _iterator.next();
			String _key = entry.getKey();
			JsonElement _value = entry.getValue();
			if(TYPE.equals(_key)){
				//ignore, already set in the constructor
			}
			else if(ABOUT.equals(_key))	
				_channel.setInfo(_value.getAsJsonObject());
			else if(NAME.equals(_key))
				_channel.setName(_value.getAsString());
			else if(LAYOUT.equals(_key))
				_channel.setLayout(_value.getAsString());
			else if(CONTAINERID.equals(_key))
				_channel.setContainerId(_value.getAsString());
			else if(CONFIG.equals(_key))
				_channel.setConfiguration(_value.getAsJsonObject());
			else if(WAIT_FOR_SINK.equals(_key))
				_channel.setWaitForSink(_value.getAsBoolean());
			else{	//for variable
				JsonElement _type = _value.getAsJsonObject().get(TYPE);
				if(_type!=null){
					try{
 						IoType.fromString(_type.getAsString());
 						//no exception, add it to the list of variables
 						_channel.addVariable(_key, IOVariable.parseIOVariable(_value.getAsJsonObject()));	
					}
					catch(InvalidIOTypeException e) {}
				}
			} 
		}//end while
		return _channel;
	}

	
	//list of variable belong to this channel
	private Map<String, IOVariable> variables;
	//layout between variables: default = vertical
	private String layout;
	//each channel belongs to a container
	private String containerId;
	//wait for sink
	private boolean waitforsink = true; /*default is true*/
	//each IO channel has a configuration
	private JsonObject configuration = null;
	private BlockingIOInterface blockingIO = null;
	private NonBlockingIOInterface nonBlockingIO = null;
	// private boolean blockingSupported = false;
	// private boolean nonBlockingSupported = false;
}
