package edu.monash.io.iolibrary;

/**
* class DataHandler
* Will need to modify this one for call back
*/
import com.google.gson.JsonObject;

public class DataHandler{
	// //path to listen to 
	// private String path;

	// //gets and sets
	// public String 	getPath		()				{return path;}
	// public void 	setPath		(String _p)		{path = _p;}
	// boolean
	public void 	onBoolean	(boolean bVal)	{}
	// this one is called when int value received
	public void 	onInt		(int intVal)	{}
	// called when receiving a double
	public void 	onDouble	(double dblVal)	{}
	// called when receiving a string
	public void 	onString	(String str)	{}
	//called when receiving an object, which is non of previous types
	public void 	onObject	(JsonObject obj)	{}

}
