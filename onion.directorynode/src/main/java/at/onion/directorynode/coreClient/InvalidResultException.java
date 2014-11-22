package at.onion.directorynode.coreClient;

public class InvalidResultException extends Exception {
	
	public InvalidResultException(){
		super();
	}
	
	public InvalidResultException(String s){
		super(s);
	}
	
	public InvalidResultException(Throwable cause){
		super(cause);
	}
	
	public InvalidResultException(String msg, Throwable cause){
		super(msg, cause);
	}

}
