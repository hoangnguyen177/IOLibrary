package edu.monash.io.iolibrary.tests;

import edu.monash.io.iolibrary.IOLibrary;
import edu.monash.io.iolibrary.BlockingIO;
import edu.monash.io.iolibrary.NonBlockingIO;
import edu.monash.io.iolibrary.exceptions.NotSupportException;
import edu.monash.io.iolibrary.exceptions.InvalidDefinitionException;
import edu.monash.io.iolibrary.iointerface.exceptions.IOFailException;
import java.io.*;
public class TestIOLibrary{
	public static void main(String[] args){
		String file = "/tmp/definition";
		if(args.length >=1)
			file = args[0];
		try{
			BlockingIO iolibrary = IOLibrary.getInstance().getBlockingIOFromFile(file);
			/*iolibrary.putString("TestIOLibrary.ioChannel1.output1", "hello, its Hoang");
	    	String msg = "";
			while(!msg.equals("q")){
				System.out.print("Enter path to send: ");
	    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String path = br.readLine();
				System.out.print("Enter message to send, q to quit: ");
	    		msg = br.readLine();
				iolibrary.putString(path, msg);
			}*/	
		}
		catch(NotSupportException e){
			e.printStackTrace();
		}
		catch(InvalidDefinitionException e){
			e.printStackTrace();
		}
		catch(IOFailException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
}
