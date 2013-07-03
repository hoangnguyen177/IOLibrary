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
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.IN_DATA_TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.OUT_DATA_TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.DATA;
import static edu.monash.io.iolibrary.ConfigurationConsts.GUI_ELEMENT;
import static edu.monash.io.iolibrary.ConfigurationConsts.NAME;



public class IOVariable extends IOObject{

	public IOVariable(){
		inputDataType = DataType.STRING;
		outputDataType = DataType.STRING;
		guiElement = "";
		data = null;
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
	public void setData(JsonElement _data)				{ data = _data;			}
	public JsonElement getData()							{ return data;			}

	/**
	* to JsonObject
	*/
	public JsonObject getObject(){
		JsonObject _obj = super.getObject();
		_obj.addProperty(IN_DATA_TYPE, inputDataType.toString());
		_obj.addProperty(OUT_DATA_TYPE, outputDataType.toString());
		_obj.add(DATA, data);
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
			else if(DATA.equals(_key))
				_variable.setData(_value);	
		}
		return _variable;
	}


	/***************************************/
	//private variables
	//data type
	private DataType inputDataType;
	private DataType outputDataType; 
	//gui element
	private String guiElement;
	//data
	// not sure whether this is the right decision
	private JsonElement data;
}