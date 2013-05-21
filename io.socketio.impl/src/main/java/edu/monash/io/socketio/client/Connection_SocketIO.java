package edu.monash.io.socketio.client;


// socket io java interface
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

//gson
import com.google.gson.JsonObject;

//java
import java.io.*;


public class Connection_SocketIO{
	// constants	
	final static int 		DEFAULT_TIMEOUT 	= 3000			;
    final static int 		DEFAULT_PORT 		= 8080			;
    final static String 	DEFAULT_PROTOCOL 	= "http"		;
    final static String 	DEFAULT_HOST 		= "localhost"	;
    final static String 	DEFAULT_NSP 		= "/"			;

    /*constructor*/
	public Connection_SocketIO(){
		host 		= DEFAULT_HOST;
		port 		= DEFAULT_PORT;
		nsp 		= DEFAULT_NSP;
		protocol 	= DEFAULT_PROTOCOL;
		
		
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
		    System.out.print("Enter your name: ");
		    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String userName = null;
			try {
		    	userName = br.readLine();
		    	System.out.print("Enter your age: ");
		    	String age = br.readLine();
		    	JsonObject jObject = new JsonObject();
    			jObject.addProperty("username" , userName);
    			jObject.addProperty("age" , age);
				socket.emit("adduser", jObject);

		    } catch (IOException ioe) {
		        System.out.println("IO error trying to read your name!");
		        System.exit(1);
		    }		    
		  }

		}).on("updatechat", new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		  	//JsonObject obj = (JsonObject)args[0];
		  	//print chat
		  	//System.out.println("New message:");
		  	//System.out.println(obj.toString());
		  	System.out.println(String.format(
                        args.length > 1 ? "updatechat [1] Message: %s, %s" : "updatechat [2] message: %s", args));
		  }
		}).on("updateusers", new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		  	//JsonObject obj = (JsonObject)args[0];
		  	//print users 
		  	//System.out.println("Users updates:");
		  	//System.out.println(obj.toString());
		  	System.out.println(String.format(
                        args.length > 1 ? "updateuser[1] message %s, %s" : "updateuser [2] message: %s", args));
		  }
		}).on("message", new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		  	//JsonObject obj = (JsonObject)args[0];
		  	//print users 
		  	//System.out.println("Users updates:");
		  	//System.out.println(obj.toString());
		  	System.out.println(String.format(
                        args.length > 1 ? "----------message %s, %s" : "[2] message: %s", args));
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
	
	/**
	* connect
	*/
	public void connect(){
		socket.connect();
	}

	/**
	* disconnect
	*/
	public void disconnect(){
		socket.disconnect();
	}
	
	/**
	* sets
	*/
	public void setHost		(String h)		{ host = h;		}
	public void setPort		(int p)			{ port = p;		}
	public void setNsp		(String _nsp)	{ nsp = _nsp;	}
	public void setProtocl	(String p)		{ protocol = p;	}
	/**
	* gets
	*/
	public String getHost	()				{ return host;		}
	public int getPort		()				{ return port;		}
	public String getNsp	()				{ return nsp;		}
	public String getProtocl()				{ return protocol;	}
	
	/**
	*get uri
	*/
	public String uri		()				{return protocol + "://" + host + ":" + port + nsp;}

    ///////////// private varibbles
    /*socket*/
    private Socket socket; 
    /*host*/
    private String host;
    /*port*/
    private int port;
    /*nsp*/
    private String nsp;
    /*protocl*/
    private String protocol;


}
