package edu.monash.io.socketio.exceptions;
/**
 * InvalidSourceException
 * @author hoangnguyen
 */
public class InvalidSourceException extends Exception{
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor
	 */
	public InvalidSourceException(){
		super();
	}
	
	public InvalidSourceException(String message){
		super(message);
	}
	
}