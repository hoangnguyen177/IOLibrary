package edu.monash.io.socketio.client.test;

//java client
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import java.io.*;

public class SimpleChat{
	final static int TIMEOUT = 3000;
    final static int PORT = 8080;

	private Socket socket; 
	public SimpleChat(){
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
		    	System.out.print("Enter your pass: ");
		    	String pass = br.readLine();
		    	JsonObject jObject = new JsonObject();
    // 			jObject.addProperty("username" , userName);
    // 			jObject.addProperty("age" , age);
				// JsonObject jObject = new JsonObject();
    			jObject.addProperty(TestConsts.CLIENT_USERNAME , userName);
    			jObject.addProperty(TestConsts.CLIENT_PASSWORD , pass);
    			jObject.addProperty(TestConsts.CLIENT_CLIENT_TYPE , TestConsts.CLIENT_SOURCE);
				socket.emit(TestConsts.CLIENT_E_AUTH, jObject);
    			//socket.emit("adduser", "hoang");

		    } catch (IOException ioe) {
		        System.out.println("IO error trying to read your name!");
		        System.exit(1);
		    }		    
		  }

		}).on("authreturn", new Emitter.Listener() {
		  @Override
		  public void call(Object... args) {
		  	//JsonObject obj = (JsonObject)args[0];
		  	//print chat
		  	//System.out.println("New message:");
		  	//System.out.println(obj.toString());
		  	System.out.println(String.format(
                        args.length > 1 ? "updatechat [1] Message: %s, %s" : "updatechat [2] message: %s", args));
		  }
		}).on("sinkconnect", new Emitter.Listener() {
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
	
	public void connect(){
		socket.connect();
		System.out.println("-------======= Connecting ========-------------");
	}
	//just main
	public static void main(String[] args) throws Exception{
		SimpleChat sChat = new SimpleChat();
		sChat.connect();
		/*IO.Options opts = new IO.Options();
        opts.forceNew = true;
        //opts.path = "http://localhost:8080/socket.io/socket.io.js";
        Socket _socket = IO.socket(uri(), opts);
        _socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("--------------------------------------connect:-----------------------------");
            }
        }).on("message", new Emitter.Listener(){
                @Override
            public void call(Object... objects) {
                //JsonObject obj = (JsonObject)objects[0];
                System.out.println("--------------------------------------message:" );
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("----------------------------disconnect:");
            }
        });
        _socket.connect();*/
	}//end main
	public static String uri() {
        return "http://localhost:" + PORT + nsp();
    }

    public static String nsp() {
        return "/";
    }

}
