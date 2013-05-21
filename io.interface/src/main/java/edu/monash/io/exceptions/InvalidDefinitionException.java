package io.exceptions;

/**
 * InvalidMessageException
 * @author hoangnguyen
 * Throw this message when name is already registered by the same username
 */

public class InvalidDefinitionException extends Exceptions{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public InvalidDefinitionException(){
		super();
	}
	
	public InvalidDefinitionException(String message){
		super(message);
	}

}