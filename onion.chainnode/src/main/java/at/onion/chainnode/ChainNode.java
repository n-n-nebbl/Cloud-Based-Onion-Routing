package at.onion.chainnode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainNode {

	private static final int DEFAULT_PORT = 9000;
			
	private static Logger	logger	= LoggerFactory.getLogger(ChainNode.class);

	public static void main(String[] args) {
		ConnectionService connServ = new ConnectionService();
		
		int port = DEFAULT_PORT;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			logger.info("Could not find/parse port, using default port {}", DEFAULT_PORT);
		}
		
		try {
			connServ.startListening(port);
		} catch (Exception e) {
			logger.error("Error occured in ChainNode", e);
			connServ.stopListening(port);
		}
	}

}
