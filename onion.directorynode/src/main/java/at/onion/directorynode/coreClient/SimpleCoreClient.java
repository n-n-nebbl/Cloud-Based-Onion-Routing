package at.onion.directorynode.coreClient;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.commons.directoryNode.Request;
import at.onion.commons.directoryNode.RequestType;
import at.onion.commons.directoryNode.Response;

public class SimpleCoreClient implements CoreClient {
	
 	private String ip = "127.0.0.1";
 	private int port = 8001;
	private Socket socket;

	@Override
	public NodeChainInfo getNodeChain() 
			throws UnknownHostException, IOException, InvalidResultException {
		Request request = new Request();
		request.setRequestType(RequestType.GET_NODECHAIN);
	 	Response reponse = sendRequestAndBlockTillResponse(request);	
	 	//TODO: Error handling for status !ok
		return reponse.getNodeChain();
	}

	@Override
	public void addNode(NodeInfo node) 
			throws UnknownHostException, IOException, InvalidResultException {
		Request request = new Request();
		request.setRequestType(RequestType.ADD_NODE);
		request.setNewNode(node);
		Response reponse = sendRequestAndBlockTillResponse(request);
	}
	
	
	private Response sendRequestAndBlockTillResponse(Request request) 
			throws UnknownHostException, IOException, InvalidResultException{
		setUpSocket();
	 	sendRequest(request);
	 	Response response = blockingReadResponse();
	 	shutdownSocket();
	 	return response;
	}
	
	private void sendRequest(Request request) 
			throws IOException{
		OutputStream outputStream = socket.getOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(request);
		outputStream.flush();		
	}
	
	private Response blockingReadResponse() 
			throws IOException, InvalidResultException{
		InputStream inputStream = socket.getInputStream();
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		
		Response reponse = null;
		try {
			reponse = (Response)objectInputStream.readObject();
		}catch(ClassNotFoundException ex){
			throw new InvalidResultException("Result does not contain a valid object", ex);
		}	
		return reponse;
	}

	private void setUpSocket() 
			throws UnknownHostException, IOException{
	 	socket = new Socket(ip,port);
	}
	
	private void shutdownSocket(){
		if(socket == null) return;
		if(!socket.isClosed()){ 
			try {
				socket.close();
			} catch (IOException e) {}
		}
		socket = null;
	}
}
