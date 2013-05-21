package io.exceptions;

/**
 * InvalidMessageException
 * @author hoangnguyen
 * Throw this message when name is already registered by the same username
 */

public class NonUniquePathException extends Exceptions{

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