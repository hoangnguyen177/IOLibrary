package edu.monash.io.iolibrary;
//gson
import com.google.gson.JsonObject;
//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import edu.monash.io.iolibrary.ioobjects.IOObjectsManager;
/**
* @author Hoang Nguyen
* Non Blocking class: expose io functaionlities of the library
*/
public class NonBlockingIO extends IO{
	public NonBlockingIO(IOObjectsManager _objects){
		super(_objects);
	}
	/*put value to specified path*/
	public void put(String path, JsonObject value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().put(path, value, append, false);
	}

	/*overwrite value to specified path*/
	public void put(String path, JsonObject value) throws InvalidPathException, NotSupportException, IOFailException{
		this.put(path, value, false);	
	}

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putData(path, value, append, false);
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


	/*add handler so that it can be called back, note if wrong handler, string is return*/
	public void addDataHandler(String path, DataHandler handler) throws InvalidPathException, NotSupportException{
		this.getIOObjects().addDataHandler(path, handler);
	}



}

