package edu.monash.io.iolibrary.exceptions;

/**
 * InvalidDataTypeException
 * @author hoangnguyen
 * Throw this exception when the datatype is found invalid
 */

public class InvalidDataTypeException extends Exceptions{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public InvalidDataTypeException(){
		super();
	}
	
	public InvalidDataTypeException(String message){
		super(message);
	}

}