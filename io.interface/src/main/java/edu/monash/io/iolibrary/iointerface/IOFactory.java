package edu.monash.io.iolibrary.iointerface;

//spring framework
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
//java 
import java.util.*;
import java.io.*;

//gson
import com.google.gson.*;

//Library
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
* Interface for IO Factory
*/

public class IOFactory{

	private IOFactory() {
        String swingconfig = System.getProperty("springconfig");
        if (swingconfig == null) {
            swingconfig = "messagebased/socketio/socketio-spring-config.xml";
        }
        appcontext = new ClassPathXmlApplicationContext(swingconfig);
        blockings =  new HashMap<String, BlockingIOInterface>();
        nonBlockings = new HashMap<String, NonBlockingIOInterface>();
    }

    private ApplicationContext appcontext;

    private static class IOFactorySingletonHolder {
        static IOFactory instance = new IOFactory();
    }

    public static IOFactory getInstance() {
        return IOFactorySingletonHolder.instance;
    }


    /**
    * register io interface with factory
    * with given path and definition
    */
    public void registerIO(String path, JsonObject definition) throws InvalidDefinitionException,IOFailException{
        //blocking map
        if(!blockings.containsKey(path)){
            BlockingIOInterface _blocking = (BlockingIOInterface)appcontext.getBean("blockingio");
            _blocking.initialise(definition);
            //then put it into the blockings map
            blockings.put(path, _blocking);
        }
        //non blocking map
        if(!nonBlockings.containsKey(path)){
            NonBlockingIOInterface _nonblocking = (NonBlockingIOInterface)appcontext.getBean("nonblockingio");
            _nonblocking.initialise(definition);
            nonBlockings.put(path, _nonblocking);
        }
    }

    /**
    * remove path from the list
    */
    public void deregisterIO(String path){
        blockings.remove(path);
        nonBlockings.remove(path);
    }

    /**
    * get the bloking io interface at that path
    * if not supported bloking interface, throw the exception
    */
    public BlockingIOInterface getBlockingIOInterface(String path) throws NotSupportException{
        if(!supportBlockingIO())
            throw new NotSupportException("This implementation does not support blocking io"); 
        else
            return blockings.get(path);
    }

    /**
    * get the non blocking io
    */
    public NonBlockingIOInterface getNonBlockingIOInterface(String path)throws NotSupportException{
        if(!supportNonBlockingIO())
            throw new NotSupportException("This implementation does not support non-blocking io"); 
        else
            return nonBlockings.get(path);
    }


    /**
    * whether blocking io is supported
    */
    public boolean supportBlockingIO(){
        if(impDetails == null)
            impDetails = (ImpDeclaration)appcontext.getBean("impdeclaration");
        return impDetails.blockingSupported();

    }

    /**
    * whether non blocking io is supported
    */
    public  boolean supportNonBlockingIO(){
        if(impDetails == null)
            impDetails = (ImpDeclaration)appcontext.getBean("impdeclaration");
        return impDetails.nonBlockingSupported();
    }



    /*******************************/
    private Map<String, BlockingIOInterface> blockings;
    private Map<String, NonBlockingIOInterface> nonBlockings;
    private ImpDeclaration impDetails = null;

}







