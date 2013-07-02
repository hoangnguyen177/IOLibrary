package edu.monash.io.iolibrary.iointerface;
/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
* All these calls are blocking
*/

import com.google.gson.JsonObject;
//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;


public interface BlockingIOInterface extends IOInterface{
	
	/*retrieve data held at specified path*/
	public String getString(String path) throws  IOFailException;

	/*retrieve double value held at specified path*/
	public Double getDouble(String path) throws  IOFailException;

	/*retrieve int value at specified path*/
	public int getInt(String path) throws IOFailException;

	/*boolean*/
	public boolean getBoolean(String path) throws IOFailException;
	
	/*get an object*/
	public JsonObject getObject(String path) throws IOFailException;
}