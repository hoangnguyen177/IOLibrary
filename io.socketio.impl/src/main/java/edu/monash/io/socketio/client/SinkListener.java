package edu.monash.io.socketio;
import com.google.gson.JsonObject;
/**
 * ConnectionListenerInterface
 * @author hoangnguyen
 * Interface for all ConnectionListeners
 */
public interface SinkListener{
	/**
	 * source disconencted 
	 */
	public void onSouceDisconnect();
	/**
	 * source connect
	 */
	public void onSourceConnect(JsonObject sourceList);
	/**
	 * permission changed
	 */
	public void onPermissionChanged(String newPermission);
	/**
	 * link established
	*/
	public void onLinkEstablished(String allowedOperations);

}
