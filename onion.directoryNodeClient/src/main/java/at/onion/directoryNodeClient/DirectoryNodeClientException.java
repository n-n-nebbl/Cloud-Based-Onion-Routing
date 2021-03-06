package at.onion.directoryNodeClient;

public class DirectoryNodeClientException extends Exception {

	private static final long	serialVersionUID	= 8290573429273468192L;

	public DirectoryNodeClientException() {
		super();
	}

	public DirectoryNodeClientException(String msg) {
		super(msg);
	}

	public DirectoryNodeClientException(Throwable cause) {
		super(cause);
	}

	public DirectoryNodeClientException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
