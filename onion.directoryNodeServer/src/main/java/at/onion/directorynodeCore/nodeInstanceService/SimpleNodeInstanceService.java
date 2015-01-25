package at.onion.directorynodeCore.nodeInstanceService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import at.onion.directorynodeCore.domain.Node;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class SimpleNodeInstanceService implements NodeInstanceService {

	Logger					logger				= LoggerFactory.getLogger(this.getClass());

	@Value("${aws.keyPairName}")
	private String			keyPairName;

	@Value("${aws.endpoint}")
	private String			endpoint;

	@Value("${aws.nodeImageId}")
	private String			nodeImageId;

	@Value("${aws.instanceType}")
	private String			instanceType;

	@Value("${aws.securityGroupId}")
	private String			securityGroupId;

	@Value("${aws.serviceName.chainNode}")
	private String			serviceName;

	private AmazonEC2Client	amazonEC2Client;

	private int				runRequestCounter	= 0;

	@Override
	public void startNewNodeInstance() throws CloudConnectionException {
		try {
			startNewNodeInstanceWithCloudSpecificExceptions();
		} catch (AmazonServiceException e) {
			throw new CloudConnectionException(e);
		} catch (AmazonClientException e) {
			throw new CloudConnectionException(e);
		}
	}

	@Override
	public void shutdownNodeInstaceOwnerForNode(Node node) throws CloudConnectionException {
		try {
			shutdownNodeInstaceOwnerForNodeWithCloudSpecificExceptions(node);
		} catch (AmazonServiceException e) {
			throw new CloudConnectionException(e);
		} catch (AmazonClientException e) {
			throw new CloudConnectionException(e);
		}
	}

	private void startNewNodeInstanceWithCloudSpecificExceptions() {
		createClientIfNeeded();
		logger.debug("Start new instance");
		startNewNodeInstanceWithCloudSpecificExceptions(amazonEC2Client, serviceName, nodeImageId, instanceType,
				keyPairName, runRequestCounter, getUserDataScript(), null);
		runRequestCounter++;
		logger.debug("New instance started");
	}

	public static void startNewNodeInstanceWithCloudSpecificExceptions(AmazonEC2Client amazonEC2Client, String tagName,
			String nodeImageId, String instanceType, String keyPairName, Integer counter, String userDataScript,
			AWSCredentials credentials) {

		String instanceTagName = tagName + (counter != null ? "-" + counter : "");

		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId(nodeImageId).withInstanceType(instanceType).withMinCount(1).withMaxCount(1)
				.withKeyName(keyPairName);
		if (credentials != null) {
			runInstancesRequest.setRequestCredentials(credentials);
		}
		runInstancesRequest.setUserData(userDataScript);

		RunInstancesResult runInstances = amazonEC2Client.runInstances(runInstancesRequest);

		List<Instance> instances = runInstances.getReservation().getInstances();
		int idx = 1;
		boolean one = (instances.size() == 1);

		for (Instance instance : instances) {
			CreateTagsRequest createTagsRequest = new CreateTagsRequest();
			createTagsRequest.withResources(instance.getInstanceId()).withTags(
					new Tag("Name", instanceTagName + (one ? "" : "." + idx)));
			amazonEC2Client.createTags(createTagsRequest);

			idx++;
		}
	}

	private void shutdownNodeInstaceOwnerForNodeWithCloudSpecificExceptions(Node node) {
		createClientIfNeeded();
		String ipString = node.getIpAddress().getHostAddress();
		logger.debug("Shutdown host with ip:" + ipString);
		String id = getIdForPublicIp(ipString);
		if (id != null) terminateInstanceWithId(id);
	}

	private String getIdForPublicIp(String ipString) {
		createClientIfNeeded();
		Filter filter = getFilterForKeyValueString("ip-address", ipString);
		DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(filter);
		DescribeInstancesResult result = amazonEC2Client.describeInstances(request);

		List<Reservation> reservations = result.getReservations();
		if (reservations.size() > 0) {
			List<Instance> instances = reservations.get(0).getInstances();
			if (instances.size() > 0) {
				return instances.get(0).getInstanceId();
			}
		}
		return null;
	}

	private void terminateInstanceWithId(String id) {
		createClientIfNeeded();
		TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(id);
		amazonEC2Client.terminateInstances(terminateInstancesRequest);
	}

	private Filter getFilterForKeyValueString(String key, String value) {
		List<String> valueList = new ArrayList<String>();
		valueList.add(value);
		return new Filter(key, valueList);
	}

	private void createClientIfNeeded() {
		if (amazonEC2Client == null) {
			amazonEC2Client = new AmazonEC2Client();
			amazonEC2Client.setEndpoint(endpoint);
		}
	}

	private String getUserDataScript() {
		Script script = new Script();
		script.addLine("#!/bin/sh");
		script.addLine("cd /home/ec2-user/");
		script.addLine("java -jar '-DdirNode.hostname=directoryNode.mooo.com' onion.chainnode-2.0.0.BUILD-SNAPSHOT.jar");
		script.addLine("touch success.txt");
		return script.getEncodedScriptAsString();
	}

}
