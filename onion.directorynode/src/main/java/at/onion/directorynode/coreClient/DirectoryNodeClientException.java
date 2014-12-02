package at.onion.directorynode.coreClient;

public class DirectoryNodeClientException extends Exception{

	public DirectoryNodeClientException(){
		super();
	}
	
	public DirectoryNodeClientException(String msg){
		super(msg);
	}
	
	public DirectoryNodeClientException(Throwable cause){
		super(cause);
	}
	
	public DirectoryNodeClientException(String msg, Throwable cause){
		super(msg, cause);
	}
}
