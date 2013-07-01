package edu.monash.io.iolibrary;


//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;

public class BlockingIO extends IO{
	
	/*put value to specified path*/
	public void put(String path, Object value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().put(path, value, append, true);
	}

	/*overwrite value to specified path*/
	public void put(String path, Object value) throws InvalidPathException, NotSupportException, IOFailException{
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
	public void putFile(String path, String filename, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putFile(path, value, append, true);
	}

	/*put a file to specified path*/
	public void putFile(String path, String filename) throws InvalidPathException, NotSupportException, IOFailException{
		this.putFile(path, filename, false);
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
	public Object getObject(String path) throws InvalidPathException, NotSupportException, IOFailException{
		return this.getIOObjects().getObject(path);
	}


}