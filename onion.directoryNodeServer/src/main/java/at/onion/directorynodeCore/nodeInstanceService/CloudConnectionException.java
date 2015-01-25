package at.onion.directorynodeCore.nodeInstanceService;

/**
 * Created by willi on 24.01.15.
 */
public class CloudConnectionException extends Exception {

	private static final long	serialVersionUID	= -6921394578930138268L;

	public CloudConnectionException() {
		super();
	}

	public CloudConnectionException(String msg) {
		super(msg);
	}

	public CloudConnectionException(Throwable cause) {
		super(cause);
	}

	public CloudConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
