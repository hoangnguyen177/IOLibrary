package io.interfaces;


/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for blocking IO interface
* This interface is highly influenced by Rappture API: https://nanohub.org/infrastructure/rappture/wiki/rappture_java_api
*/

/**
* abstract class DataHandler
* Will need to modify this one for call back
*/
public abstract DataHandler{
	public enum DataHandlerType{
		STRING_HANDLER, INT_HANDLER, DOUBLE_HANDLER
	}

	public abstract void onData(Object value);
	
	/*get the handler*/
	public DataHandlerType getHandlerType(){
		return handlerType;
	}
	
	/*set the handler type*/
	public void setHandlerType(DataHandlerType type){
		handlerType = type;
	}

	//////
	private DataHandlerType handlerType;
}


/**
* interface NonBlockingIOInterface
* 
*/
public interface NonBlockingIOInterface extends IOInterface{
	
	/*add handler so that it can be called back, note if wrong handler, string is return*/
	public void addDataHandler(DataHandler handler);
}

