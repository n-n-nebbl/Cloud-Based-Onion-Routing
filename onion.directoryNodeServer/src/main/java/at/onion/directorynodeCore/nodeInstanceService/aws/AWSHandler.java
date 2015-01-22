package at.onion.directorynodeCore.nodeInstanceService.aws;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class AWSHandler {
	private static String STARTFILE_DIRECTORYNODE = "start_directorynodeserver.sh";
	private static String STARTFILE_CHAINNODE = "start_chainnode.sh";

	private static Logger logger = LoggerFactory.getLogger(AWSHandler.class);

	public static void main(String[] args) {
		AWSHandler awsHandler = new AWSHandler();
		try {
			awsHandler.getServer();
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RunInstancesResult launchServer() throws IOException {
		return AWSUtil.runInstance("dirnodeserver", new File(STARTFILE_DIRECTORYNODE));
	}

	public String getServer() throws IOException, AmazonServiceException, AmazonClientException, InterruptedException {
		return AWSUtil.startInstance("i-3f882333", logger);
	}

	public RunInstancesResult launchChainNode() throws IOException {
		return AWSUtil.runInstance("chainnode", new File(STARTFILE_CHAINNODE));
	}
}