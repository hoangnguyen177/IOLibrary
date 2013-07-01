package edu.monash.io.messagebased.socketio.source;


//IO library
import edu.monash.io.iolibrary.iointerface.ImpDeclaration;

public class SocketIOImplementation implements ImpDeclaration{

	/*version of the implementation*/
	public String version(){
		return "1.0.0";
	}

	/*support blocking io*/
	public boolean blockingSupported(){
		return true;
	}

	/*non blocking io*/
	public boolean nonBlockingSupported(){
		return true;
	}


}