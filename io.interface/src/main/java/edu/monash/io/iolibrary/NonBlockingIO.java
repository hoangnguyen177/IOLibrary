package edu.monash.io.iolibrary;


//IO library
import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
/**
* @author Hoang Nguyen
* Non Blocking class: expose io functaionlities of the library
*/
public class NonBlockingIO extends IO{
	
	/*put value to specified path*/
	public void put(String path, Object value, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().put(path, value, append, false);
	}

	/*overwrite value to specified path*/
	public void put(String path, Object value) throws InvalidPathException, NotSupportException, IOFailException{
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
	public void putFile(String path, String filename, boolean append) throws InvalidPathException, NotSupportException, IOFailException{
		this.getIOObjects().putFile(path, value, append, false);
	}

	/*put a file to specified path*/
	public void putFile(String path, String filename) throws InvalidPathException, NotSupportException, IOFailException{
		this.putFile(path, filename, false);
	}


	/*add handler so that it can be called back, note if wrong handler, string is return*/
	public void addDataHandler(String path, DataHandler handler) throws InvalidPathException, NotSupportException{
		this.getIOObjects().addDataHandler(path, handler);
	}



}

