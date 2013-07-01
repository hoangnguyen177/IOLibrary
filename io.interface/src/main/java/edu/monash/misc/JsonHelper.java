package edu.monash.misc;

// gson
import com.google.gson.*;


public class JsonHelper{

	/**
	* get the value from object based on the path
	*/
	public static JsonElement getObject(JSonObject object, String path) throws InvalidPathException,
																				DefinitionNotDefinedException{
		if(object == null)
			throws DefinitionNotDefinedException("Cannot set value to null object");
		String[] _parts = path.split("\.");
		JsonObject _currentObject = object;
		for(int i=0; i< _parts.length; i++){
			JsonElement _currentElement = _currentObject.get(_parts[i]);
			//only the last element is allowed not to be JsonObject
			//not sure if the first one is necessary
			if(_currentElement== null || _currentElement.isJsonNull())
				throw InvalidPathException("Invalid path:" + path + " JsonObject:" + object.toString());
			if(i < _parts.length - 1)
				_currentObject = _currentElement.getAsJsonObject();
		}
		return _tmp;
	}

	/**
	* set value in the path to value
	* note that this method does not check whether the type of value is same as old one
	*/
	public static void setObject(JsonObject object, String path, Object value) throws InvalidPathException, 
																				DefinitionNotDefinedException{
		if(object == null)
			throws DefinitionNotDefinedException("Cannot set value to null object");
		String[] _parts = path.split("\.");
		JsonObject _currentObject = object;
		//last key
		String _lastKey = _parts[parts.length-1];
		//get the last JsonObject
		for(int i=0; i< _parts.length - 1; i++){
			_currentObject = _currentObject.get(_parts[i]);
			if(_currentObject== null || _currentObject.isJsonNull())
				throw InvalidPathException("Invalid path:" + path + " JsonObject:" + object.toString());
		}
		JsonPrimitive _value = new JsonPrimitive(value);
		_currentObject.add(_lastKey, _value);
	}

	

}