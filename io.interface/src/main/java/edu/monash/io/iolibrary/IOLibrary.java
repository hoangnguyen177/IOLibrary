package edu.monash.io.iolibrary;

//java
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

//library
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.ioobjects.IOObjectsManager;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;


/**
* @author Hoang Nguyen
* This library contains Container and Component
* The component is 
*/

public class IOLibrary{
	private IOLibrary() {
       
    }
    private static class IOLibrarySingletonHolder {
        static IOLibrary instance = new IOLibrary();
    }

    public static IOLibrary getInstance() {
        return IOLibrarySingletonHolder.instance;
    }




	/**
	* get the blocking io
	*/
	public BlockingIO getBlockingIO(String _definition) throws NotSupportException, InvalidDefinitionException, IOFailException{
		if(!maps.containsKey(_definition))
			maps.put(_definition, new IOObjectsManager(_definition));
		if(!maps.get(_definition).supportBlocking())
			throw new NotSupportException("Blocking IO is not supported");
		return new BlockingIO(maps.get(_definition));
	}

	/**
	* get the non blocking io
	*/
	public NonBlockingIO getNonBlockingIO(String _definition)throws NotSupportException, InvalidDefinitionException, IOFailException{
		if(!maps.containsKey(_definition))
			maps.put(_definition, new IOObjectsManager(_definition));
		if(!maps.get(_definition).supportNonBlocking())
			throw new NotSupportException("Non Blocking IO is not supported");
		return new NonBlockingIO(maps.get(_definition));	
	}


	/*********************************************/
	//convenient function

	/**
	* get BlockingIO straight from text file
	*/
	public BlockingIO getBlockingIOFromFile(String fileName) throws FileNotFoundException, IOException, IOFailException,
																	NotSupportException, InvalidDefinitionException
	{
		String _definition = FileHelper.readTextFile(fileName);
		return getBlockingIO(_definition);
	}

	/**
	* get NonBlockingIO straight from text file
	*/
	public NonBlockingIO getNonBlockingIOFromFile(String fileName) throws FileNotFoundException, IOException, IOFailException,
																		NotSupportException, InvalidDefinitionException
	{
		String _definition = FileHelper.readTextFile(fileName);
		return getNonBlockingIO(_definition);
	}

	/*********************************************/
	private Map<String, IOObjectsManager> maps = new HashMap<String, IOObjectsManager>();

}