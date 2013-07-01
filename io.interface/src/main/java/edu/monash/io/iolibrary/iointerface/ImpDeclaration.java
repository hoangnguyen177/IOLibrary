package edu.monash.io.iolibrary.iointerface;

/**
* @Author: Hoang Anh Nguyen
* @Date:  16 May 2013
*/

// this interface declares what is avaiabe
// what is not in the implementation
public interface ImpDeclaration {
	/*version of the implementation*/
	public String version();

	/*support blocking io*/
	public boolean blockingSupported();

	/*non blocking io*/
	public boolean nonBlockingSupported();
}


