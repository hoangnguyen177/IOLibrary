package edu.monash.io;

import edu.monash.io.exceptions.NonUniquePathException;
import edu.monash.io.exceptions.InvalidDefinitionException;
/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for IO Factory
*/

public interface IOFactoryInterface{
	
	/*get blocking IO interface from definition*/
	public BlockingIOInterface getBlockingIOFromDefinition(String definition) 
								throws NonUniquePathException, InvalidDefinitionException;
	
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

