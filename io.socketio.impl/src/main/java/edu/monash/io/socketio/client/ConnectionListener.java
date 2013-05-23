package edu.monash.io.socketio;
import com.google.gson.JsonObject;
import edu.monash.io.socketio.exceptions.InvalidMessageException;
/**
 * ConnectionListenerInterface
 * @author hoangnguyen
 * Interface for all ConnectionListeners
 */
public interface ConnectionListener{
	/**
	 * onConnectionEstablished
	 * This method is called when a socket connection is established to the server
	 */
	public void onConnectionEstablished();
	/**
	 * onConnectionFailed
	 * this method is called when attempting to make a connection is failed 
	 * not needed
	 */
	//public void onConnectionFailed();
	/**
	 * onDisconnect
	 * this method is called when disconnect 
	 */
	public void onDisconnect();
	/**
	 * on authentication return
	 */
	public void onAuthResponse(JsonObject authResponse);
	/**
 	 * onMessage
	 * this message is called when a new message is available in the connection
	 * @param aMessage
	 * @throws InvalidMessageException
	 */
	public void onMessage(JsonObject aMessage) throws InvalidMessageException;
}
