package at.onion.proxy;

public class TCPConnectionProxyProperty {

	private String	directoryNodeHostName	= "";
	private int		directoryNodePort		= -1;

	public TCPConnectionProxyProperty() {

	}

	public TCPConnectionProxyProperty(String directoryNodeHostName, int directoryNodePort) {
		this.directoryNodeHostName = directoryNodeHostName;
		this.directoryNodePort = directoryNodePort;
	}

	public String getDirectoryNodeHostName() {
		return directoryNodeHostName;
	}

	public void setDirectoryNodeHostName(String directoryNodeHostName) {
		this.directoryNodeHostName = directoryNodeHostName;
	}

	public int getDirectoryNodePort() {
		return directoryNodePort;
	}

	public void setDirectoryNodePort(int directoryNodePort) {
		this.directoryNodePort = directoryNodePort;
	}

}
