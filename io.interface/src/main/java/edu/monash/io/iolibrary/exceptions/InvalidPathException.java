package edu.monash.io.iolibrary.exceptions;

/**
 * InvalidPathException
 * @author hoangnguyen
 * throw this one when the path is invalid
 */

public class InvalidPathException extends Exception{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public InvalidPathException(){
		super();
	}
	
	public InvalidPathException(String message){
		super(message);
	}

}