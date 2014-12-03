package at.onion.directorynodeCore.chainGernatorService;

public class NotEnoughNodesException extends Exception {
	
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
		super(msg, cause);
	}
}
