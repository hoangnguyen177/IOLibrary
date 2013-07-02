package edu.monash.misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.FileNotFoundException;
import java.io.IOException;



public class FileHelper{

	private static final String INPUT_FILE_NAME = "";
	private static final String OUTPUT_FILE_NAME = "";
	public static final boolean isDebugging = true;

	/** Run the example. */
	public static void main(String... aArgs) {
		// FileHelper test = new FileHelper();
		// //read in the bytes
		// byte[] fileContents = test.read(INPUT_FILE_NAME);
		// //test.readAlternateImpl(INPUT_FILE_NAME);
		// //write it back out to a different file name
		// test.write(fileContents, OUTPUT_FILE_NAME);
	}

	/** Read the given binary file, and return its contents as a byte array.*/ 
	public static byte[] readBinaryFile(String aInputFileName) throws FileNotFoundException, IOException{
	    log("Reading in binary file named : " + aInputFileName);
	    File file = new File(aInputFileName);
	    log("File size: " + file.length());
	    byte[] result = new byte[(int)file.length()];
	    InputStream input = null;
        int totalBytesRead = 0;
        input = new BufferedInputStream(new FileInputStream(file));
        while(totalBytesRead < result.length){
          int bytesRemaining = result.length - totalBytesRead;
          //input.read() returns -1, 0, or more :
          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
          if (bytesRead > 0){
            totalBytesRead = totalBytesRead + bytesRead;
          }
        }
        log("Num bytes read: " + totalBytesRead);
        input.close();
	    return result;
	}
  
	/**
	* Write a byte array to the given file. 
	* Writing binary data is significantly simpler than reading it. 
	*/
	public static void writeBinaryFile(byte[] aInput, String aOutputFileName) throws FileNotFoundException, IOException{
	    log("Writing binary file...");
	    OutputStream output = null;
        output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
        output.write(aInput);
    	output.close();
	}
	  
	/** Read the given binary file, and return its contents as a byte array.*/ 
	public static byte[] readAlternateImpl(String aInputFileName)throws FileNotFoundException, IOException{
	    log("Reading in binary file named : " + aInputFileName);
	    File file = new File(aInputFileName);
	    log("File size: " + file.length());
	    byte[] result = null;
	    InputStream input =  new BufferedInputStream(new FileInputStream(file));
	    result = readAndClose(input);
	    return result;
	}
  
	/**
	*Read an input stream, and return it as a byte array.  
	*Sometimes the source of bytes is an input stream instead of a file. 
	*This implementation closes aInput after it's read.
	*/
	public static byte[] readAndClose(InputStream aInput) throws IOException{
		//carries the data from input to output :    
		byte[] bucket = new byte[32*1024]; 
		ByteArrayOutputStream result = null; 
	    //Use buffering? No. Buffering avoids costly access to disk or network;
	    //buffering to an in-memory stream makes no sense.
	    result = new ByteArrayOutputStream(bucket.length);
	    int bytesRead = 0;
	    while(bytesRead != -1){
	      //aInput.read() returns -1, 0, or more :
	      bytesRead = aInput.read(bucket);
	      if(bytesRead > 0){
	        result.write(bucket, 0, bytesRead);
	      }
	  	}
	    aInput.close();
	    //result.close(); this is a no-operation for ByteArrayOutputStream
		return result.toByteArray();
	}

	/**
	* log
	*/
	private static void log(Object aThing){
		if(isDebugging)
			System.out.println(String.valueOf(aThing));
	}	


	/**
	* read a text file
	*/
	public static String readTextFile(String fileName) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        br.close();
	    return sb.toString();
	}

	/**
	* write to a text file
	*/
	public static void writeTextFile(String text, String fileName) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(text);
		out.close();
	}

}