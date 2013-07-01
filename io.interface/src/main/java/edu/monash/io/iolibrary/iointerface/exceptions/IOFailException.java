package edu.monash.io.iolibrary.iointerface.exceptions;

/**
 * IOFailException
 * @author hoangnguyen
 * throw this one when smth is wrong wiht IO
 */

public class IOFailException extends Exceptions{

	private static final long serialVersionUID = 1L;
	/**
	 * constructor
	 */
	public IOFailException(){
		super();
	}
	
	public IOFailException(String message){
		super(message);
	}

}