package edu.monash.io.socketio.exceptions;
/**
 * ConnectionFailException
 * @author hoangnguyen
 */
public class ConnectionFailException extends Exception{
	private static final long serialVersionUID = 1L;
	
	/**
	 * constructor
	 */
	public ConnectionFailException(){
		super();
	}
	
	public ConnectionFailException(String message){
		super(message);
	}
	
}