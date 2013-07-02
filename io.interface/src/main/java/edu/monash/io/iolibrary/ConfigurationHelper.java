package edu.monash.io.iolibrary;


//java
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;

//gson
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

//library
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.misc.JsonHelper;

import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE_CHANNEL;
import static edu.monash.io.iolibrary.ConfigurationConsts.CONFIG;

public class ConfigurationHelper{

	/**
	* check the type of the first level objects - do not go furthur down
	* returns linkedlist of ids with the same type: container, or components
	*/
	public static LinkedList<String> getFirstLevelObjetsIDs(JsonObject definition, String _type) throws InvalidDefinitionException{
		if(definition==null)
			throw new InvalidDefinitionException("Definition is null");
		LinkedList<String> ids = new LinkedList<String>();
		//get all the components IDs, and return
		Set<Map.Entry<String,JsonElement>> _elements = definition.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _elementsIterator = _elements.iterator();
		while(_elementsIterator.hasNext()){
			Map.Entry<String, JsonElement> _element = _elementsIterator.next();
			String _key = _element.getKey(); //this will be added to ids if the type of this object is component
			JsonElement _value = _element.getValue();
			if(_value != null&& !_value.isJsonNull()){
				JsonObject _valueObject = _value.getAsJsonObject();
				String _componentType = _valueObject.get(TYPE).getAsString();
				if(_type.equals(_componentType))
					ids.addLast(_key);
			}
		}
		//done
		return ids;
	}

	/**
	* get the first channel from the definition, in case there are more than one channels in a single defintion file
	*/
	public static JsonObject getFirstChannelFromDefinition(JsonObject definition) throws InvalidDefinitionException{
		LinkedList<String> ids = ConfigurationHelper.getFirstLevelObjetsIDs(definition, TYPE_CHANNEL);
		if(ids.size()==0)
			throw new InvalidDefinitionException("Cannot  find any TYPE_CHANNEL in the definition");
		String _first = ids.getFirst();
		return definition.get(_first).getAsJsonObject();
	}
	/**
	* get the configuration info from the definition object
	*/
	public static JsonObject getConfigFromDefinition(JsonObject definition) throws InvalidDefinitionException{
		JsonObject channel = ConfigurationHelper.getFirstChannelFromDefinition(definition);
		JsonElement _configElement = channel.get(CONFIG);
		if(_configElement == null || _configElement.isJsonNull())
			throw new InvalidDefinitionException("Cannot find config in the definition");
		return _configElement.getAsJsonObject();
	}

}