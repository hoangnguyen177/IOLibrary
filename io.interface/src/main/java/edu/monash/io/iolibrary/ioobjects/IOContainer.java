package edu.monash.io.iolibrary.ioobjects;

//java
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

//Json
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
//IO library
import edu.monash.io.iolibrary.iointerface.BlockingIOInterface;
import edu.monash.io.iolibrary.iointerface.NonBlockingIOInterface;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidIOTypeException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE_CONTAINER;
import static edu.monash.io.iolibrary.ConfigurationConsts.SHARED_IO;
import static edu.monash.io.iolibrary.ConfigurationConsts.LAYOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.NAME;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT;


public class IOContainer extends IOObject{
	/*cosntructor*/
	public IOContainer(){
		super();
		channels = new HashMap<String, IOChannel>();
		this.setType(TYPE_CONTAINER);
	}


	/*add channel*/
	public void addChannel(String channelId, IOChannel channel){
		if (!channels.containsKey(channelId))
			channels.put(channelId, channel);
	}

	public void addChannel(IOChannel channel){
		this.addChannel(channel.getId(), channel);
	}

	public void addChannelList(LinkedList<IOChannel> list){
		Iterator<IOChannel> _iterator = list.iterator();
		while(_iterator.hasNext())
			this.addChannel(_iterator.next());
	}

	/*remove channel*/
	public void removeChannel(String channelId){
		channels.remove(channelId);
	}

	/*get a channel*/
	public IOChannel getChannel(String channelId){
		return channels.get(channelId);
	}

	/*get the whole map*/
	public Map<String, IOChannel> getChannels(){
		return channels;
	}

	/*layout*/
	public void setLayout(String _layout)		{	layout = _layout;	}
	public String getLayout()					{	return layout;		}

	/*shared io*/
	public void setSharedIO(boolean _shared)	{	sharedIOPath = _shared;	}
	public boolean sharedIo()					{ 	return sharedIOPath;	}


	/*get objects*/
	public JsonObject getObject(){
		JsonObject _obj = super.getObject();
		_obj.addProperty(SHARED_IO, sharedIOPath);
		_obj.addProperty(LAYOUT, layout);
		//note that there is no need to add channels
		return _obj;
	}


	/**
	* get the blocking io interface corresponding to the given path
	*/
	public BlockingIOInterface getBlockingIOInterface(String path) throws InvalidPathException, NotSupportException{
		int firstDotPosition = path.indexOf('.');
		if(firstDotPosition <0)
			throw new InvalidPathException(path + " is not a valid path");
		String _channelid = path.substring(0, firstDotPosition-1);
		if(channels.containsKey(_channelid))
			return channels.get(_channelid).getBlockingIO(this.sharedIo());
		else
			throw new InvalidPathException("There is no channel:" + _channelid);
	}	

	/**
	* get the non blocking io interface corresponding to the given path
	*/
	public NonBlockingIOInterface getNonBlockingIOInterface(String path) throws InvalidPathException, NotSupportException{
		int firstDotPosition = path.indexOf('.');
		if(firstDotPosition <0)
			throw new InvalidPathException(path + " is not a valid path");
		String _channelid = path.substring(0, firstDotPosition-1);
		if(channels.containsKey(_channelid))
			return channels.get(_channelid).getNonBlockingIO(this.sharedIo());
		else
			throw new InvalidPathException("There is no channel:" + _channelid);
	}




	/*parse the container*/
	// containerID:{
	// 	about:{
	// 		title: some title,
	// 		description: some desc,
	// 		hint: some hint,
	// 		author: blah,
	// 		date: blah,
	// 	},
	// 	shared: true, //default is false, share the IO connection
	// 	layout: horizontal,
	// 	type: container,													type, container are Keys
	// },
	public static IOContainer parseContainer(JsonObject containerContents)throws InvalidDefinitionException, 
																		InvalidDataTypeException, InvalidIOTypeException
	{
		if(containerContents==null)
			throw new InvalidDefinitionException("[parseContainer@IOContainer] container contents cannot be null here");
		IOContainer _container = new IOContainer();
		Set<Map.Entry<String,JsonElement>> _entries = containerContents.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _iterator = _entries.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> entry = _iterator.next();
			String _key = entry.getKey();
			JsonElement _value = entry.getValue();
			if(TYPE.equals(_key)){
			}
			else if(ABOUT.equals(_key))
				_container.setInfo(_value.getAsJsonObject());
			else if(LAYOUT.equals(_key))
				_container.setLayout(_value.getAsString());
			else if(SHARED_IO.equals(_key))
				_container.setSharedIO(Boolean.parseBoolean(_value.getAsString()));
			else if(NAME.equals(_key))
				_container.setName(_value.getAsString());
		}
		return _container;
	}

	/*private variables*/
	//list of channels belong to this container
	private Map<String, IOChannel> channels;
	private String layout;
	private boolean sharedIOPath;
	
}