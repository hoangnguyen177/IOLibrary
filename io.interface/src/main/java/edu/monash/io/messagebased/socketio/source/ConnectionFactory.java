package edu.monash.io.messagebased.socketio.source;

//java
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

//gson
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
//IO library
import edu.monash.misc.JsonHelper;
import edu.monash.io.socketio.connection.SourceConnection;
import edu.monash.io.socketio.connection.SourceListener;
import edu.monash.io.socketio.exceptions.ConnectionFailException;

import edu.monash.io.iolibrary.ConfigurationHelper;
import edu.monash.io.iolibrary.ConfigurationConsts;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;

public class ConnectionFactory{
	//singleton stuff
	private ConnectionFactory() {
       
    }
    private static class ConnectionFactorySingletonHolder {
        static ConnectionFactory instance = new ConnectionFactory();
    }

    public static ConnectionFactory getInstance() {
        return ConnectionFactorySingletonHolder.instance;
    }

    /**
    * get the connection from the 
    */
    public synchronized SourceConnection getConnection(JsonObject _definition)throws IOFailException,InvalidDefinitionException
    {
    	SourceConnection source;
    	if(connections.containsKey(_definition)){
    		source = connections.get(_definition);
    	}
    	else{//create one
	    	JsonObject definition = JsonHelper.cloneJsonObject(_definition);
    		source = new SourceConnection(); 
		    JsonObject connectionConfig = ConfigurationHelper.getConfigFromDefinition(definition);
			if(connectionConfig.has(ConfigurationConsts.CONNECTION_HOST))
				source.setHost(connectionConfig.get(ConfigurationConsts.CONNECTION_HOST).getAsString());
			if(connectionConfig.has(ConfigurationConsts.CONNECTION_PROTOCOL))
				source.setProtocol(connectionConfig.get(ConfigurationConsts.CONNECTION_PROTOCOL).getAsString());
			if(connectionConfig.has(ConfigurationConsts.CONNECTION_NSP))
				source.setNsp(connectionConfig.get(ConfigurationConsts.CONNECTION_NSP).getAsString());				
			if(connectionConfig.has(ConfigurationConsts.CONNECTION_PORT))
				source.setPort(connectionConfig.get(ConfigurationConsts.CONNECTION_PORT).getAsInt());
			if(connectionConfig.has(ConfigurationConsts.CONNECTION_TIMEOUT))
				source.setTimeout(connectionConfig.get(ConfigurationConsts.CONNECTION_TIMEOUT).getAsInt());
			source.setAuthInfo(definition);
            // source.addListener(new SourceListener(){
            //     public void onConnectionEstablished(){
            //         System.out.println("getConnection:: socket established");
            //     }
            //     public void onDisconnect(){
            //         System.out.println("getConnection:: disconnect");
            //     }
            //     public void onAuthResponse(JsonObject authResponse){
            //         System.out.println("getConnection:: auth response:" + authResponse.toString());
            //     }
            //     public void onMessage(JsonObject aMessage){
            //         System.out.println("getConnection:: new message: " + aMessage.toString());
            //     }   
            //     public void onSinkDisconnect(){
            //         System.out.println("getConnection:: sink disconnect, no more sink");    
            //     }
            //     public void onSinkConnect(JsonArray sinkList){
            //         System.out.println("getConnection:: new sink connect, sinklist:" + sinkList.toString());    
            //     }
            // });

            try{
                //create the socket
                //connect
                source.connect();
                //wait till this source is authenticated
                while(!source.authcated()){
                    try{
                        Thread.sleep(600);
                    }
                    catch(Exception e){}
                }//end small while  

            }
            catch(ConnectionFailException e){
                throw new IOFailException(e.getMessage());
            }
            catch(JsonSyntaxException e){
                throw new InvalidDefinitionException(e.getMessage());
            }
            //put the origional one, not the modified
			connections.put(_definition, source);
    	}	
    	return source;
    }

    /**
    * remove the connection
    */
    public SourceConnection removeConnection(JsonObject _definition){
		return connections.remove(_definition);
    }

    /**
    * clear
    */
    public void clear(){
    	connections.clear();
    }

    /*************************************************************/
    //private variables
    private Map<JsonObject, SourceConnection> connections = new HashMap<JsonObject, SourceConnection>();



}