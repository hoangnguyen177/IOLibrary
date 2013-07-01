package edu.monash.io.iolibrary.exceptions;

/**
 * NotSupportException
 * @author hoangnguyen
 * Throws this exception when the type if IO requested is not supported
 */

public class NotSupportException extends Exceptions{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public NotSupportException(){
		super();
	}
	
	public NotSupportException(String message){
		super(message);
	}

}