package edu.monash.io.socketio.connection;
//gson
import com.google.gson.JsonObject;
/**
 * SinkListener
 * @author hoangnguyen
 */
public interface SinkListener extends ConnectionListener{
	/**
	 * source disconencted 
	 */
	public void onSourceDisconnect();
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
