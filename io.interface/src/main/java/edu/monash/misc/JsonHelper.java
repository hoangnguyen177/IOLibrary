package edu.monash.misc;

// gson
import com.google.gson.*;
// library
import edu.monash.io.iolibrary.exceptions.DefinitionNotDefinedException;
import edu.monash.io.iolibrary.exceptions.InvalidPathException;

public class JsonHelper{

	/**
	* get the value from object based on the path
	*/
	public static JsonElement getObject(JsonObject object, String path) throws InvalidPathException,
																				DefinitionNotDefinedException{
		if(object == null)
			throw new DefinitionNotDefinedException("Cannot set value to null object");
		String[] _parts = path.split("\\.");
		JsonObject _currentObject = object;
		for(int i=0; i< _parts.length; i++){
			JsonElement _currentElement = _currentObject.get(_parts[i]);
			//only the last element is allowed not to be JsonObject
			//not sure if the first one is necessary
			if(_currentElement== null || _currentElement.isJsonNull())
				throw new InvalidPathException("Invalid path:" + path + " JsonObject:" + object.toString());
			if(i < _parts.length - 1)
				_currentObject = _currentElement.getAsJsonObject();
		}
		return _currentObject;
	}

	/**
	* set value in the path to value
	* note that this method does not check whether the type of value is same as old one
	*/
	public static void setObject(JsonObject object, String path, JsonElement value) throws InvalidPathException, 
																				DefinitionNotDefinedException{
		if(object == null)
			throw new DefinitionNotDefinedException("Cannot set value to null object");
		String[] _parts = path.split("\\.");
		JsonObject _currentObject = object;
		//last key
		String _lastKey = _parts[_parts.length-1];
		//get the last JsonObject
		for(int i=0; i< _parts.length - 1; i++){
			_currentObject = _currentObject.get(_parts[i]).getAsJsonObject();
			if(_currentObject== null || _currentObject.isJsonNull())
				throw new InvalidPathException("Invalid path:" + path + " JsonObject:" + object.toString());
		}
		_currentObject.add(_lastKey, value);
	}

	/**
	* clone JsonObject
	*/
	public static JsonObject cloneJsonObject(JsonObject object){
		JsonObject returnElement = null;
		JsonParser parser = new JsonParser();
		try{
			returnElement = parser.parse(object.toString()).getAsJsonObject();
		}
		catch(JsonSyntaxException e){
			//ignored, return null
		}
		return returnElement;
	}

	

}