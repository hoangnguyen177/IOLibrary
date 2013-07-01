package edu.monash.io.socketio.exceptions;
/**
 * InvalidMessageException
 * @author hoangnguyen
 * Throw this message when there is something wrong with the format of the message received/created
 */
public class UnauthcatedClientException extends Exception{
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor
	 */
	public UnauthcatedClientException(){
		super();
	}
	
	public UnauthcatedClientException(String message){
		super(message);
	}
	
}