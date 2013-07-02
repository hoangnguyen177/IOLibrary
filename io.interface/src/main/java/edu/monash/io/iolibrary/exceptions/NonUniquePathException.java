package edu.monash.io.iolibrary.exceptions;

/**
 * NonUniquePathException
 * @author hoangnguyen
 * Throw this message when the path given is not unique
 */

public class NonUniquePathException extends Exception{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public NonUniquePathException(){
		super();
	}
	
	public NonUniquePathException(String message){
		super(message);
	}

}