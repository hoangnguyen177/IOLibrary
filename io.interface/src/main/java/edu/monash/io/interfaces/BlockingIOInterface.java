package io.interfaces;

/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
* All these calls are blocking
*/


public interface BlockingIOInterface extends IOInterface{
	/*retrieve data held at specified path*/
	public String getString(String path);

	/*retrieve double value held at specified path*/
	public Double getDouble(String path);

	/*retrieve int value at specified path*/
	public int getInt(String path);
}