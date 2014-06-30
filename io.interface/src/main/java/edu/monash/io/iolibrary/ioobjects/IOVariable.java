package edu.monash.io.iolibrary.ioobjects;
//java
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

//gson
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
//IO Library
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.exceptions.InvalidDataTypeException;
import edu.monash.io.iolibrary.exceptions.InvalidIOTypeException;
import edu.monash.io.iolibrary.ConfigurationConsts.DataType;
import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import edu.monash.io.iolibrary.ConfigurationConsts.IoType;
import edu.monash.io.iolibrary.ConfigurationConsts.UpdateMode;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.IN_DATA_TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.OUT_DATA_TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.IN_DATA;
import static edu.monash.io.iolibrary.ConfigurationConsts.OUT_DATA;
import static edu.monash.io.iolibrary.ConfigurationConsts.GUI_ELEMENT;
import static edu.monash.io.iolibrary.ConfigurationConsts.NAME;
import static edu.monash.io.iolibrary.ConfigurationConsts.DATA_UPDATE_MODE;




public class IOVariable extends IOObject{

	public IOVariable(){
	}

	/*input data type*/
	public void setInputDataType(DataType _inT)			{ inputDataType = _inT;	}
	public DataType getInputDataType()					{ return inputDataType; }
	
	/*output data type*/
	public void setOutputDataType(DataType _outT)		{ outputDataType = _outT;}
	public DataType getOutputDataType()					{ return outputDataType; }

	/*gui element*/
	public void setGuiElement(String _gElement)			{ guiElement = _gElement;}
	public String getGuiElement()						{ return guiElement;	 }

	/*data*/
	public void setInData(JsonElement _data)			{ in_data = _data;		 }
	public JsonElement getInData()						{ return in_data;		 }

	public void setOutData(JsonElement _data)			{ out_data = _data;		 }
	public JsonElement getOutData()						{ return out_data;		 }

	public void addExtra(String key,JsonElement _data)	{ extras.add(key, _data);}
	public JsonObject getExtras()						{ return extras;		 }

	public void setDataUpdateMode(String _updateMode)	throws InvalidDataTypeException
	{ 
		updatemode = UpdateMode.fromString(_updateMode);
	}

	public UpdateMode getDataUpdateMode()				{ return updatemode; 	}
	/**
	* to JsonObject
	*/
	public JsonObject getObject(){
		JsonObject _obj = super.getObject();
		IoType _type = null;
		try{
			_type = IoType.fromString(this.getType());
		}
		catch(InvalidIOTypeException e){}
		if(_type == IoType.INPUT){
			_obj.addProperty(IN_DATA_TYPE, this.getInputDataType().toString());
			_obj.addProperty(OUT_DATA_TYPE, this.getOutputDataType().toString());
			_obj.add(IN_DATA, getInData());
			_obj.add(OUT_DATA, getOutData());
		}
		else if(_type == IoType.OUTPUT){
			_obj.addProperty(OUT_DATA_TYPE, this.getOutputDataType().toString());
			_obj.add(OUT_DATA, getOutData());
		}
		_obj.addProperty(GUI_ELEMENT, this.getGuiElement());
		_obj.addProperty(DATA_UPDATE_MODE, this.getDataUpdateMode().toString());
		Set<Map.Entry<String,JsonElement>> _entries = extras.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _iterator = _entries.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> entry = _iterator.next();
			String _key = entry.getKey();
			JsonElement _value = entry.getValue();
			_obj.add(_key, _value);
		}
		return _obj;	
	}


	/**
	* receives a JsonObject and parse it to IOVariable
	*/
	// input1:{															
	// 		type: input,													
	// 		about:{															
	// 			label: label here,											
	// 			description: description here,									
	// 			hint: hint here,												
	// 		},
	// 		in_datatype: int/string/object,
	// 		out_datatype: int/string/object,
	// 		guielement: choice,
	// 		data:[about: {label, l1, description: d1}]
	// 	}, 
	public static IOVariable parseIOVariable(JsonObject variableContents) throws InvalidDefinitionException, 
																		InvalidDataTypeException, InvalidIOTypeException
	{
		if(variableContents==null)
			throw new InvalidDefinitionException("[parseIOVariable@IOVariable] declaration is null");
		IOVariable _variable = new IOVariable();
		Set<Map.Entry<String,JsonElement>> _entries = variableContents.entrySet();
		Iterator<Map.Entry<String, JsonElement>> _iterator = _entries.iterator();
		while(_iterator.hasNext()){
			Map.Entry<String, JsonElement> entry = _iterator.next();
			String _key = entry.getKey();
			JsonElement _value = entry.getValue();
			//now check the key: type, about, int_datatype, out_datatype and data
			if(TYPE.equals(_key))
				_variable.setType(IoType.fromString(_value.getAsString()).toString());
			else if(ABOUT.equals(_key))
				_variable.setInfo(_value.getAsJsonObject());
			else if(NAME.equals(_key))
				_variable.setName(_value.getAsString());
			else if(IN_DATA_TYPE.equals(_key))
				_variable.setInputDataType(DataType.fromString(_value.getAsString()));
			else if(OUT_DATA_TYPE.equals(_key))
				_variable.setOutputDataType(DataType.fromString(_value.getAsString()));
			else if(IN_DATA.equals(_key))
				_variable.setInData(_value);	
			else if(OUT_DATA.equals(_key))
				_variable.setOutData(_value);
			else if(GUI_ELEMENT.equals(_key))
				_variable.setGuiElement(_value.getAsString());
			else if(DATA_UPDATE_MODE.equals(_key))
				_variable.setDataUpdateMode(_value.getAsString());
			else
				_variable.addExtra(_key, _value);
		}
		return _variable;
	}


	/***************************************/
	//private variables
	//data type
	private DataType inputDataType = null;
	private DataType outputDataType = null; 
	//gui element
	private String guiElement = null;
	//data
	// not sure whether this is the right decision
	private JsonElement in_data = null;
	private JsonElement out_data = null;
	//how to udpate data
	private UpdateMode updatemode = UpdateMode.APPEND;
	//aprt from predefined tags, users can define their own
	private JsonObject extras = new JsonObject();
}