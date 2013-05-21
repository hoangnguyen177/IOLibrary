package edu.monash.io.socketio.client.test;

//java client
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketIOSource{
	final static int TIMEOUT = 3000;
    final static int PORT = 8080;

	private Socket socket; 
	public boolean isAuthcated = false;
	public SocketIOSource(){
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
    			jObject.addProperty(TestConsts.CLIENT_CLIENT_TYPE , TestConsts.CLIENT_SOURCE);
				System.out.println("Sending Authentication");
				socket.emit(TestConsts.CLIENT_E_AUTH, jObject);
				System.out.println("Authentication");

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
		  		isAuthcated = true;
		  	}
		  	else if(msgCode.equals(TestConsts.SERVER_E_SINK_DISCONNECT)){
		  		System.out.println("sink disconnect, no more sink");
		  	}
		  	else if(msgCode.equals(TestConsts.SERVER_E_SINK_CONNECT)){
		  		System.out.println("sink connect, lit of sinks:" + obj.get("sinklist").toString());
		  	}
		  	else if(msgCode.equals(TestConsts.CLIENT_E_MESSAGE)){
		  		if(obj.get("datatype")!= null && obj.get("datatype").getAsString().equals("image")){
		  			byte[] imageReceived = Base64.decode(obj.get("message").getAsString());
    	            //now write it
        	        try{
        	        	String _fileN = SocketIOSource.getCurrentTimeStamp();
                        File newFile = new File(_fileN);
	                	FileOutputStream fos = new FileOutputStream(newFile);
	                	fos.write(imageReceived);
	                	fos.close();
	                	System.out.println("Image written");
	                }
	                catch(Exception e){
	  	              e.printStackTrace();
			  		}
			  	}
			  	else{
			  		System.out.println("message:" + args[0].toString());
			  	}	
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
	//just main
	public static void main(String[] args) throws Exception{
		SocketIOSource sChat = new SocketIOSource();
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

    public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}


}
