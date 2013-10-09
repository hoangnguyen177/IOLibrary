package edu.monash.io.iolibrary.iointerface;


/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
*/


import edu.monash.io.iolibrary.exceptions.InvalidPathException;
import edu.monash.io.iolibrary.DataHandler;

/**
* interface NonBlockingIOInterface
* 
*/
public interface NonBlockingIOInterface extends IOInterface{
	
	/*add handler so that it can be called back, note if wrong handler, string is return*/
	public void addDataHandler(String path, DataHandler handler);
	/*count the number of data handlers*/
	public boolean hasDataHandler(String path);
}

