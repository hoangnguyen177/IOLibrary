package edu.monash.io;

/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
* All these calls are blocking
*/


public interface IOInterface{

	/*put value to specified path*/
	public void put(String path, Object value, boolean append);

	/*overwrite value to specified path*/
	public void put(String path, Object value);

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value, boolean append);

	/*put an array of byte to specified path*/
	public void putData(String path, byte[] value);

	/*put a file to specified path*/
	public void putFile(String path, String filename, boolean append);

	/*put a file to specified path*/
	public void putFile(String path, String filename);

	/*get the definition of the operation*/
	public String getDefinition();

}