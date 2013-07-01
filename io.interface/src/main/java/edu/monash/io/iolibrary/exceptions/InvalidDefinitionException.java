package edu.monash.io.iolibrary.exceptions;

/**
 * InvalidDefinitionException
 * @author hoangnguyen
 * THrows this one when the definition is invalid
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