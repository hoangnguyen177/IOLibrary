package edu.monash.io.iolibrary.exceptions;

/**
 * InvalidIOTypeException
 * @author hoangnguyen
 * throw this one when the IO type is invalid
 */

public class InvalidIOTypeException extends Exceptions{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public InvalidIOTypeException(){
		super();
	}
	
	public InvalidIOTypeException(String message){
		super(message);
	}

}