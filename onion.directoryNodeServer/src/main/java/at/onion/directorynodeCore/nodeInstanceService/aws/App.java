package at.onion.directorynodeCore.nodeInstanceService.aws;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;

public class App {
	private static PropertiesCredentials credentials;
	private static String DEFAULT_KEY_NAME = "aic14-group3-topic3";
	private static String DEFAULT_IMAGE_ID = "ami-07762037";
	private static String DEFAULT_ENDPOINT = "ec2.us-west-2.amazonaws.com";
	private static final int WAIT_FOR_TRANSITION_INTERVAL = 5000;

	public App() throws FileNotFoundException, IllegalArgumentException, IOException {
		credentials = new PropertiesCredentials(new File("awscredentials.properties"));
	}

	// https://bitbucket.org/utoolity/bamboo-aws-plugin/src/ef5a1a97df2c/src/main/java/net/utoolity/bamboo/plugins/EC2Task.javaa
	public static String startInstance(String instanceId, Logger logger) throws AmazonServiceException, AmazonClientException, InterruptedException {
		AmazonEC2Client ec2 = new AmazonEC2Client(credentials);

		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceId);
		StartInstancesResult startResult = ec2.startInstances(startRequest);
		List<InstanceStateChange> stateChangeList = startResult.getStartingInstances();
		logger.info("Starting instance '" + instanceId);

		// Wait for the instance to be started
		return waitForTransitionCompletion(stateChangeList, "running", ec2, instanceId, logger);
	}

	/**
	 * Wait for a instance to complete transitioning (i.e. status not being in INSTANCE_STATE_IN_PROGRESS_SET or the instance no longer existing).
	 * 
	 * @param stateChangeList
	 * @param instancebuilder
	 * @param instanceId
	 * @param Logger
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static String waitForTransitionCompletion(List<InstanceStateChange> stateChangeList, final String desiredState, AmazonEC2 instancebuilder, String instanceId, Logger logger) throws InterruptedException {
		Boolean transitionCompleted = false;
		InstanceStateChange stateChange = stateChangeList.get(0);
		String previousState = stateChange.getPreviousState().getName();
		String currentState = stateChange.getCurrentState().getName();
		String transitionReason = "";

		while(!transitionCompleted) {
			try {
				Instance instance = describeInstance(instancebuilder, instanceId);
				currentState = instance.getState().getName();
				if(previousState.equals(currentState)) {
					logger.info("... '" + instanceId + "' is still in state " + currentState + " ...");
				}
				else {
					logger.info("... '" + instanceId + "' entered state " + currentState + " ...");
					transitionReason = instance.getStateTransitionReason();
				}
				previousState = currentState;

				if(currentState.equals(desiredState)) {
					transitionCompleted = true;
				}
			}
			catch(AmazonServiceException ase) {
				logger.error("Failed to describe instance '" + instanceId + "'!", ase);
				throw ase;
			}

			// Sleep for WAIT_FOR_TRANSITION_INTERVAL seconds until transition has completed.
			if(!transitionCompleted) {
				Thread.sleep(WAIT_FOR_TRANSITION_INTERVAL);
			}
		}

		logger.info("Transition of instance '" + instanceId + "' completed with state " + currentState + " (" + (StringUtils.isEmpty(transitionReason) ? "Unknown transition reason" : transitionReason) + ").");

		return currentState;
	}

	public static RunInstancesResult runInstance(String serviceName, File userDataScriptFile) throws IOException {
		AmazonEC2Client ec2 = new AmazonEC2Client();
		ec2.setEndpoint(DEFAULT_ENDPOINT);
		if(serviceName != null) {
			ec2.setServiceNameIntern(serviceName);
		}

		RunInstancesRequest request = new RunInstancesRequest();
		request.withImageId(DEFAULT_IMAGE_ID);
		request.withInstanceType("t2.micro");
		request.withMinCount(1);
		request.withMaxCount(1);
		request.withKeyName(DEFAULT_KEY_NAME);
		request.setRequestCredentials(credentials);
		if(userDataScriptFile != null) {
			request.setUserData(getUserDataScript(userDataScriptFile));
		}

		return ec2.runInstances(request);
	}

	/**
	 * @param instancebuilder
	 * @param instanceId
	 * @return Instance
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 */
	public static Instance describeInstance(AmazonEC2 instancebuilder, String instanceId) throws AmazonServiceException, AmazonClientException {
		DescribeInstancesRequest describeRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
		DescribeInstancesResult result = instancebuilder.describeInstances(describeRequest);

		for(Reservation reservation : result.getReservations()) {
			for(Instance instance : reservation.getInstances()) {
				if(instanceId.equals(instance.getInstanceId())) {
					return instance;
				}
			}
		}
		return null;
	}

	private static String join(Collection<String> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = s.iterator();
		while(iter.hasNext()) {
			builder.append(iter.next());
			if(!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	private static String getUserDataScript() {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("#!/bin/sh");
		lines.add("cd /home/ec2-user/");
		lines.add("java -jar '-DdirNode.hostname=directoryNode.mooo.com' onion.chainnode-0.0.1-SNAPSHOT.jar");
		String str = encodeLines(lines);
		return str;
	}

	private static String encodeLines(List<String> lines) {
		String str = new String(Base64.encodeBase64(join(lines, "\n").getBytes()));
		return str;
	}

	private static String getUserDataScript(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		String str = encodeLines(lines);
		return str;
	}
}