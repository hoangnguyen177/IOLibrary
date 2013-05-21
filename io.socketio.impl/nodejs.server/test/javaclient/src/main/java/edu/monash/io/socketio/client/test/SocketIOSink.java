package edu.monash.io.socketio.client.test;

//java client
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import java.io.*;

import java.awt.Image;
import java.awt.Graphics2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;


public class SocketIOSink{
	final static int TIMEOUT = 3000;
    final static int PORT = 8080;

	private Socket socket; 
	public boolean isAuthcated = false;
	public SocketIOSink(){
		try{
		String contentsSoFar = "";
		IO.Options opts = new IO.Options();
		opts.forceNew = true;
		//opts.path = "http://localhost:8080/socket.io/socket.io.js";
		socket = IO.socket("http://localhost:8080/", opts);	
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		    //ask name
		    try {
		    	String userName = null;
		    	System.out.print("username: ");
		    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				userName = br.readLine();
		    	System.out.print("pass: ");
		    	String pass = br.readLine();
		    	JsonObject jObject = new JsonObject();
    			jObject.addProperty(TestConsts.CLIENT_USERNAME , userName);
    			jObject.addProperty(TestConsts.CLIENT_PASSWORD , pass);
    			jObject.addProperty(TestConsts.CLIENT_CLIENT_TYPE , TestConsts.CLIENT_SINK);
				socket.emit(TestConsts.CLIENT_E_AUTH, jObject);

		    } catch (IOException ioe) {
		        System.out.println("IO error trying to read your name!");
		        System.exit(1);
		    }		    
		  }

		}).on(TestConsts.CLIENT_E_MESSAGE, new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
     		  System.out.println("message:" + args[0].toString());
		  	JsonObject obj = (JsonObject)args[0];
		  	String msgCode = obj.get("messagetype").getAsString() ;
	  	if(msgCode.equals(TestConsts.SERVER_E_AUTH_RETURN)){
		  		System.out.println("Authentication return");
		  		try {
			    	String source = null;
			    	System.out.print("source: ");
			    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					source = br.readLine();
					socket.emit(TestConsts.CLIENT_E_SELECT, source);
					isAuthcated = true;

			    } catch (IOException ioe) {
			        System.out.println("IO error trying to read your name!");
			        System.exit(1);
			    }
		  	}
		  	else if(msgCode.equals(TestConsts.SERVER_E_SOURCE_DISCONNECT)){
		  		System.out.println("source disconnect, no more source");
		  	}
		  	else if(msgCode.equals(TestConsts.SERVER_E_PERMISSION_CHANGED)){
		  		System.out.println("permission changed:" + obj.get("allowedoperations").toString());
		  	}
		  	else if(msgCode.equals(TestConsts.SERVER_E_SOURCE_CONNECT)){
		  		System.out.println("source connect, list of sources:" + obj.get("sourcelist").toString());
		  	}

		  }
		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		  	System.out.println("-------======= Disconnect ========-------------");
		  }
		});
		
	}
	catch(Exception e){
		System.out.println("[Exception]" + e.getMessage());
	}
	
	}
	
	public void connect(){
		socket.connect();
		System.out.println("-------======= Connecting ========-------------");
	}

	public void send(String msg){
		JsonObject jObject = new JsonObject();
		jObject.addProperty("messagetype", TestConsts.CLIENT_E_MESSAGE);
		jObject.addProperty(TestConsts.CLIENT_E_MESSAGE , msg);
		socket.emit(TestConsts.CLIENT_E_MESSAGE, jObject);
	}

	public void sendImg(String msg){
		JsonObject jObject = new JsonObject();
		jObject.addProperty("messagetype", TestConsts.CLIENT_E_MESSAGE);
		jObject.addProperty(TestConsts.CLIENT_E_MESSAGE , msg);
		jObject.addProperty("datatype", "image");
		socket.emit(TestConsts.CLIENT_E_MESSAGE, jObject);
	}

	//just main
	public static void main(String[] args) throws Exception{
		SocketIOSink sChat = new SocketIOSink();
		sChat.connect();
		while(!sChat.isAuthcated){
			try{
				Thread.sleep(600);
			}
			catch(Exception e){}
		}
			
		try {
	    	String msg = "";
			while(!msg.equals("q")){
				System.out.print("Enter smth to send to source: ");
	    		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				msg = br.readLine();
				sChat.send(msg);	
				System.out.println("Enter an image path:");
				msg = br.readLine();
				System.out.println("Enter an image type:");
				String imageType = br.readLine();				
				BufferedImage image = ImageIO.read(new File(msg));
			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    ImageIO.write(image, imageType, baos);
			    String imageContent = Base64.encodeToString(baos.toByteArray(), false);
			    sChat.sendImg(imageContent);
			}	
	    } catch (IOException ioe) {
	        System.out.println("IO error trying to read your name!");
	        System.exit(1);
	    }
		
	}//end main

	public static String uri() {
        return "http://localhost:" + PORT + nsp();
    }

    public static String nsp() {
        return "/";
    }

}
