package edu.monash.io.iolibrary;


//java
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

//gson
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

//IOLibrary
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.BlockingIOInterface;
import edu.monash.io.iolibrary.iointerface.NonBlockingIOInterface;


import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE_CONTAINER;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE_CHANNEL;



public class IOObjectsManager{

	/**
	 * constructors
	 */
	public IOObjectsManager(String _definition) throws InvalidDefinitionException, 
											InvalidDataTypeException, InvalidIOTypeException
	{
		try{
            JsonParser parser = new JsonParser();
            JsonElement defElement = parser.parse(_definition);
            if(defElement==null)
            	throw new InvalidDefinitionException("[@IOObject]Invalid definition");
            definition = defElement.getAsJsonObject();
            //parse the definition to containers and components
            processDefinition();
		}
        catch(JsonSyntaxException e){
            throw new InvalidDefinitionException(e.getMessage());
        }
	}

	/**
	* get definition
	*/
	public JsonObject getDefinition(){
		return definition;
	}

	/**
	* whether support blocking
	*/
	public boolean supportBlocking(){
		return IOFactory.supportBlockingIO();
	}

	/**
	* whether support non blocking
	*/
	public boolean supportNonBlocking(){
		return IOFactory.supportNonBlockingIO();
	}

	/**
	* get the blocking io interface corresponding to the given path
	*/
	public BlockingIOInterface getBlockingIOInterface(String path) throws InvalidPathException, NotSupportException{
		int firstDotPosition = path.indexOf('.');
		if(firstDotPosition <0)
			throw new InvalidPathException(path + " is not an valid path");
		String _containerId = path.substring(0, firstDotPosition-1);
		if(containers.containsKey(_containerId))
			return containers.get(_containerId).getBlockingIOInterface(path.substring(firstDotPosition+1, path.length()-1));
		else
			throw new InvalidPathException("There is no container:" + _containerId);
	}	

	/**
	* get the non blocking io interface corresponding to the given path
	*/
	public NonBlockingIOInterface getNonBlockingIOInterface(String path) throws InvalidPathException, NotSupportException{
		int firstDotPosition = path.indexOf('.');
		if(firstDotPosition <0)
			throw new InvalidPathException(path + " is not an valid path");
		String _containerId = path.substring(0, firstDotPosition-1);
		if(containers.containsKey(_containerId))
			return containers.get(_containerId).getNonBlockingIOInterface(path.substring(firstDotPosition+1, path.length()-1));
		else
			throw new InvalidPathException("There is no container:" + _containerId);
	}

	



	/////////////////////////////////////////////////////////////
	//io variables
	/*put value to specified path*/
	public void put(String path, Object value, boolean append, boolean blocking) throws InvalidPathException, 
																NotSupportException, IOFailException
	{
		IOInterface _ioInterface = null;
		if(blocking)
			_ioInterface= this.getBlockingIOInterface(path);
		else
			_ioInterface= this.getNonBlockingIOInterface(path);
		if(_ioInterface==null)
			throw new IOFailException("cannot get the IO interface. Something goes wrong");
		_ioInterface.put(path, value, append);
	}

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append, boolean blocking) throws InvalidPathException, 
																	NotSupportException, IOFailException
	{
		IOInterface _ioInterface = null;
		if(blocking)
			_ioInterface= this.getBlockingIOInterface(path);
		else
			_ioInterface= this.getNonBlockingIOInterface(path);
		if(_ioInterface==null)
			throw new IOFailException("cannot get the IO interface. Something goes wrong");
		_ioInterface.putData(path, value, append);
	}																

	/*put a file to specified path*/
	public void putFile(String path, String filename, boolean append, boolean blocking) throws InvalidPathException, 
																		NotSupportException, IOFailException
	{
		IOInterface _ioInterface = null;
		if(blocking)
			_ioInterface= this.getBlockingIOInterface(path);
		else
			_ioInterface= this.getNonBlockingIOInterface(path);
		if(_ioInterface==null)
			throw new IOFailException("cannot get the IO interface. Something goes wrong");
		_ioInterface.putFile(path, value, append);
	}

	//// NON BLOCKING
	/*non blocking way to receive data*/
	public void addDataHandler(String path, DataHandler handler) throws InvalidPathException, NotSupportException{
		NonBlockingIOInterface _nonBlocking = this.getNonBlockingIOInterface(path);
		_nonBlocking.addDataHandler(path, handler);
	}

	//BLOCKING
	/*retrieve data held at specified path*/
	public String getString(String path) throws InvalidPathException, NotSupportException, IOFailException{
		BlockingIOInterface _blocking = this.getBlockingIOInterface(path);
		return _blocking.getString(path);		
	}

	/*retrieve double value held at specified path*/
	public Double getDouble(String path) throws InvalidPathException, NotSupportException, IOFailException{
		BlockingIOInterface _blocking = this.getBlockingIOInterface(path);
		return _blocking.getDouble(path);
	}

	/*retrieve int value at specified path*/
	public int getInt(String path) throws InvalidPathException, NotSupportException, IOFailException{
		BlockingIOInterface _blocking = this.getBlockingIOInterface(path);
		return _blocking.getInt(path);
	}

	/*boolean*/
	public boolean getBoolean(String path) throws InvalidPathException, NotSupportException, IOFailException{
		BlockingIOInterface _blocking = this.getBlockingIOInterface(path);
		return _blocking.getBoolean(path);
	}
	
	/*get an object*/
	public Object getObject(String path) throws InvalidPathException, NotSupportException, IOFailException{
		BlockingIOInterface _blocking = this.getBlockingIOInterface(path);
		return _blocking.getObject(path);
	}




	/*********************************************/
	//private methods
	/**
	* from the definition, assume its not null,
	* pass it to components and containers
	*/
	private void processDefinition() throws InvalidDefinitionException, 
											InvalidDataTypeException, InvalidIOTypeException
	{
		//assume that the definition is not null
		Set<Map.Entry<String,JsonElement>> _entries = definition.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _iterator = _entries.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> entry = _iterator.next();
			String _key = entry.getKey();
			JsonObject _value = entry.getValue().getAsJsonObject();
			//get the type
			String _itemType = _value().get(TYPE).getAsString();
			if(TYPE_CONTAINER.equals(_itemType)){
				//a container
				//add to container list
				IOContainer _aContainer = IOContainer.parseContainer(_value);
				_aContainer.setId(_key);
				//check in the free channel list to see whether there is any channels with the smae container id
				if(freeChannels.containsKey(_key))
					_aContainer.addChannelList(freeChannels.get(_key));
				containers.put(_key, _aContainer);	
			}
			else if(TYPE_CHANNEL.equals(_itemType)){
				IOChannel _aChannel = IOChannel.parseChannel(_value);
				_aChannel.setId(_key);
				String _containerId = _aChannel.getContainerId();
				if(!containers.containsKey(_containerId)){
					if(!freeChannels.containsKey(_containerId))
						freeChannels.put(_containerId, new LinkedList<IOChannel>());
					freeChannels.get(_containerId).addLast(_aChannel);
				} 
				else{
					//register channel IO
					_aChannel.registerIO(containers.get(_containerId).getObject(), containers.get(_containerId).sharedIo());
					containers.get(_containerId).addChannel(_aChannel);
				}
			}

		}
	}
	//private variables
	//definition file
	private JsonObject definition = null;
	//list of containers
	private Map<String, IOContainer> containers = new HashMap<String, IOContainer>();
	//channels that not yet belong to any container
	private Map<String, LinkedList<IOChannel>> freeChannels = new HashMap<String, LinkedList<IOChannel>>();
}