package io.implementation.socketio.client;



import io.interfaces.IOFactoryInterface;
import 


public class IOFactory_SocketIO implements IOFactoryInterface{
	/*get blocking IO interface from definition*/
	public BlockingIOInterface getBlockingIOFromDefinition(String definition) 
								throws NonUniquePathException, InvalidDefinitionException{

	}	
	/*get blocking IO interface from file contains the deinition*/
	public BlockingIOInterface getBlockingIOFromFile(String path) 
								throws NonUniquePathException, InvalidDefinitionException;

	/*get non blocking IO interface from definition*/
	public NonBlockingIOInterface getNonBlockingIOFromDefinition(String definition) 
								throws NonUniquePathException, InvalidDefinitionException;

	/*get blocking IO interface from file contains the deinition*/
	public NonBlockingIOInterface getNonBlockingIOFromFile(String path) 
								throws NonUniquePathException, InvalidDefinitionException;

}