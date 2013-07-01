package edu.monash.io.socketio.connection;

//gson
import com.google.gson.*;
/**
 * SourceListener
 * @author hoangnguyen
 * Interface for all SourceListeners
 */
public interface SourceListener extends ConnectionListener{
	/**
	 * source disconencted 
	 */
	public void onSinkDisconnect();
	/**
	 * source connect
	 */
	public void onSinkConnect(JsonArray sinkList);
}
