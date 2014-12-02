package at.onion.directorynode.coreClient;

public class NotEnoughNodesException extends DirectoryNodeClientException{
	
	public NotEnoughNodesException(){
		super();
	}
	
	public NotEnoughNodesException(String msg){
		super(msg);
	}
	
	public NotEnoughNodesException(Throwable cause){
		super(cause);
	}
	
	public NotEnoughNodesException(String msg, Throwable cause){
		super(cause);
	}
}
