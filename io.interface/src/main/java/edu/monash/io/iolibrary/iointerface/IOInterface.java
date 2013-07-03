package edu.monash.io.iolibrary.iointerface;

/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
* All these calls are blocking
*/



//gson
import com.google.gson.JsonObject;


//IO Library
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;

public interface IOInterface{

	/*initialise the interface*/
	//config contains the information for creating an IO (connection)
	//and definition contains the declartion for the whole channel + container
	public void initialise(JsonObject definition) throws InvalidDefinitionException, IOFailException;

	//put string
	public void putString(String path, String value, boolean append) throws IOFailException;

	//put int
	public void putInt(String path, int value, boolean append) throws IOFailException;

	//double
	public void putDouble(String path, double value, boolean append) throws IOFailException;

	//boolean
	public void putBoolean(String path, boolean value, boolean append) throws IOFailException;

	/*put value to specified path*/
	public void put(String path, JsonObject value, boolean append) throws IOFailException;

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append) throws IOFailException;

	/*put a file to specified path*/
	public void putTextFile(String path, String filename, boolean append) throws IOFailException;

	/*put binary file*/
	public void putBinaryFile(String path, String filename, boolean append) throws IOFailException;

	/*get the definition of the operation*/
	public String getDefinition();

}