package at.onion.directoryNodeClient;

import java.net.InetAddress;
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
import at.onion.commons.directoryNode.ResponseStatus;

public class SimpleCoreClient implements CoreClient {
	
 	private int port;
 	private InetAddress inetAddress;
	private Socket socket;
	
	public static SimpleCoreClient getInstanceForInetAddressAndPort(InetAddress inetAddres, int port){
		return new SimpleCoreClient(inetAddres, port);
	}
	
	public SimpleCoreClient(InetAddress inetAddress, int port){
		this.inetAddress = inetAddress;
		this.port = port;
	}

	@Override
	public NodeChainInfo getNodeChain() 
			throws IOException, InvalidResultException, InternalServerErrorException, NotEnoughNodesException {
		Request request = new Request();
		request.setRequestType(RequestType.GET_NODECHAIN);
	 	Response response = sendRequestAndBlockTillResponse(request);	
	 	return extractNodeChainFromResponseAndHandleErrors(response);
	}

	@Override
	public String addNode(NodeInfo node) 
			throws IOException, InvalidResultException, InternalServerErrorException {
		Request request = new Request();
		request.setRequestType(RequestType.ADD_NODE);		
		request.setNewNode(node);
		Response response = sendRequestAndBlockTillResponse(request);
		return getIdFromAddNodeResponseAndHandleErrors(response);
	}
	
	@Override
	public void closeConnection(){
		shutdownSocket();
	}
	
	private NodeChainInfo extractNodeChainFromResponseAndHandleErrors(Response response) 
			throws NotEnoughNodesException, InternalServerErrorException{
		ResponseStatus status = response.getResponseStatus();
	 	if(status != ResponseStatus.OK){
	 		handleNodeChainResponseErrorStatus(status);
	 	}
	 	if(response.getNodeChain() == null){
	 		throw new InternalServerErrorException("Server returned empty node chain");
	 	}
	 	return response.getNodeChain();
	}
	
	private void handleNodeChainResponseErrorStatus(ResponseStatus status) 
			throws NotEnoughNodesException, InternalServerErrorException{
	 	if(status == ResponseStatus.ERR_NOT_ENOUGH_NODES){
	 		throw new NotEnoughNodesException();
	 	}else{
	 		throw new InternalServerErrorException();
	 	}		
	}
	
	private String getIdFromAddNodeResponseAndHandleErrors(Response response) 
			throws InternalServerErrorException{
		if(response.getResponseStatus() != ResponseStatus.OK){
			throw new InternalServerErrorException();
		}
		return response.getId();
	}
	
	private Response sendRequestAndBlockTillResponse(Request request) 
			throws UnknownHostException, IOException, InvalidResultException{
		setUpSocket();
	 	sendRequest(request);
	 	Response response = blockingReadResponse();
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
			throws IOException{
	 	socket = new Socket(inetAddress,port);
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
