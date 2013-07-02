package edu.monash.io.iolibrary;

import com.google.gson.JsonObject;
//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.ioobjects.IOObjectsManager;

public class BlockingIO extends IO{
	public BlockingIO(IOObjectsManager _objects){
		super(_objects);
	}

	/*put value to specified path*/
	public void put(String path, JsonObject value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().put(path, value, append, true);
	}

	/*overwrite value to specified path*/
	public void put(String path, JsonObject value) throws InvalidPathException, NotSupportException, IOFailException{
		this.put(path, value, false);	
	}

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putData(path, value, append, true);
	}

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value) throws InvalidPathException, NotSupportException, IOFailException{
		this.putData(path, value, false);
	}

	/*put a file to specified path*/
	public void putTextFile(String path, String filename, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putTextFile(path, filename, append, true);
	}

	public void putBinaryFile(String path, String filename, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putBinaryFile(path, filename, append, true);
	}

	/*put a file to specified path*/
	public void putTextFile(String path, String filename) throws InvalidPathException, NotSupportException, IOFailException{
		this.putTextFile(path, filename, false);
	}

	public void putBinaryFile(String path, String filename) throws InvalidPathException, NotSupportException, IOFailException{
		this.putBinaryFile(path, filename, false);
	}	

	/*retrieve data held at specified path*/
	public String getString(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getString(path);
	}

	/*retrieve double value held at specified path*/
	public Double getDouble(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getDouble(path);
	}

	/*retrieve int value at specified path*/
	public int getInt(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getInt(path);
	}

	/*boolean*/
	public boolean getBoolean(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getBoolean(path);
	}
	
	/*get an object*/
	public JsonObject getObject(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getObject(path);
	}


}