package edu.monash.io.socketio;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
/**
 * SourceListener
 * @author hoangnguyen
 * Interface for all SourceListeners
 */
public interface SourceListener{
	/**
	 * source disconencted 
	 */
	public void onSinkDisconnect();
	/**
	 * source connect
	 */
	public void onSinkConnect(JsonArray sinkList);
}
