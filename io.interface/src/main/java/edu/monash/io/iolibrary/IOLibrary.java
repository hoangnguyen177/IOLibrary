package edu.monash.io.iolibrary;

//java
import java.io.FileNotFoundException;
import java.io.IOException;


//library
import edu.monash.misc.FileHelper;
import edu.monash.io.iolibrary.ioobjects.IOObjectsManager;


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
	public BlockingIO getBlockingIO(String _definition) throws NotSupportException, InvalidDefinitionException{
		if(!ioObjects.supportBlocking())
			throw new NotSupportException("Blocking IO is not supported");
		return new BlockingIO(ioObjects);
	}

	/**
	* get the non blocking io
	*/
	public NonBlockingIO getNonBlockingIO(String _definition)throws NotSupportException, InvalidDefinitionException{
		if(!ioObjects.supportNonBlocking())
			throw new NotSupportException("Non Blocking IO is not supported");
		return new NonBlockingIO(ioObjects);
	}


	/*********************************************/
	//convenient function
	
	/**
	* get BlockingIO straight from definition
	*/
	public BlockingIO getBlockingIO(String _definition) throws InvalidDefinitionException, NotSupportException{
		if(maps.has(_definition))
			return maps.get(_definition);
	}

	/**
	* get BlockingIO straight from text file
	*/
	public BlockingIO getBlockingIOFromFile(String fileName) throws FileNotFoundException, IOException{
		String _definition = FileHelper.readTextFile(fileName);
		return getBlockingIO(_definition);
	}

	/**
	* get NonBlockingIO straight from definition
	*/
	public NonBlockingIO getNonBlockingIO(String _definition)throws InvalidDefinitionException, NotSupportException{
		//TODO
		//implement 
	}

	/**
	* get NonBlockingIO straight from text file
	*/
	public NonBlockingIO getNonBlockingIOFromFile(String fileName) throws FileNotFoundException, IOException{
		String _definition = FileHelper.readTextFile(fileName);
		return getNonBlockingIO(_definition);
	}


	/*********************************************/
	private Map<String, IOObjectsManager> maps = new HashMap<String, ioObjects>();

}