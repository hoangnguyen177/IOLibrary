package edu.monash.io.iolibrary;


import com.google.gson.JsonObject;
//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.ioobjects.IOObjectsManager;

public abstract class IO{
	/**
	* constructor
	*/
	public IO(IOObjectsManager _objects){
		objects = _objects;
	}


	/**
	*
	*/
	public IOObjectsManager getIOObjects(){
		return objects;
	}
	
	/*put value to specified path*/
	public abstract void put(String path, JsonObject value, boolean append) 
					throws InvalidPathException, NotSupportException, IOFailException;

	/*overwrite value to specified path*/
	public abstract void put(String path, JsonObject value) 
					throws InvalidPathException, NotSupportException, IOFailException;

	/*put an array of byte to specified path*/
	public abstract void putData(String path, byte[] value, boolean append) 
					throws InvalidPathException, NotSupportException, IOFailException;

	/*put an array of byte to specified path*/
	public abstract void putData(String path, byte[] value) 
					throws InvalidPathException, NotSupportException, IOFailException;

	/*put a file to specified path*/
	public abstract void putTextFile(String path, String filename, boolean append) 
					throws InvalidPathException, NotSupportException, IOFailException;

	public abstract void putBinaryFile(String path, String filename, boolean append) 
					throws InvalidPathException, NotSupportException, IOFailException;



	/*****************************/
	//private variables
	private IOObjectsManager objects = null;
	
}
