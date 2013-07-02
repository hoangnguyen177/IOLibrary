package edu.monash.io.iolibrary.exceptions;

/**
 * DefinitionNotDefinedException
 * @author hoangnguyen
 * Throw this exception when the definition is not defined
 */

public class DefinitionNotDefinedException extends Exception{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public DefinitionNotDefinedException(){
		super();
	}
	
	public DefinitionNotDefinedException(String message){
		super(message);
	}

}