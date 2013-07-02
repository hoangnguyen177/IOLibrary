package edu.monash.io.iolibrary.ioobjects;


//Json
import com.google.gson.JsonObject;


//Library


import static edu.monash.io.iolibrary.ConfigurationConsts.TYPE;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT_LABEL;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT_DESCRIPTION;
import static edu.monash.io.iolibrary.ConfigurationConsts.ABOUT_HINT;
import static edu.monash.io.iolibrary.ConfigurationConsts.NAME;


/**
* this class is the parent class of IOComponent (contains actual IO component) and IOContainer
*/

public class IOObject{
	public IOObject(){
		type= "";
		name = "";
		id= "";
		about = null;
	}

	/*about*/
	public JsonObject getInfo()							{	return about;	}
	public void setInfo(JsonObject _about)				{	about = _about;	}

	public void setInfo(String label, String description, String hint){
		about = new JsonObject();
		about.addProperty(ABOUT_LABEL , label);
		about.addProperty(ABOUT_DESCRIPTION , description);
		about.addProperty(ABOUT_HINT , hint);		
	}



	/*type*/
	protected void 	setType(String _type)				{	type = _type;	}
	public String 	getType()							{	return type;	}


	/*name*/
	public void setName(String _name) 					{	name = _name;	}
	public String getName()								{	return name;	}


	/*id*/
	public void setId(String _id)						{ 	id = _id;		}
	public String getId()								{ 	return id;		}

	/*get the json object represent this object*/
	public JsonObject getObject(){
		JsonObject _anObject = new JsonObject();
		_anObject.add(ABOUT, this.about);
		_anObject.addProperty(NAME, this.getName());
		_anObject.addProperty(TYPE, this.getType());
		//do not need to add id
		return _anObject;
	}
	/******************************/
	//private variables
	//every io object has a type
	private String type;
	private String name; //can be left null
	private String id;   //must not be left null
	private JsonObject about;
}