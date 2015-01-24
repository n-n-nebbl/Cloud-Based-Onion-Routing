package at.onion.directorynodeCore.nodeInstanceService;

import at.onion.directorynodeCore.domain.Node;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SimpleNodeInstanceService implements NodeInstanceService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${aws.keyPairName}")
    private String keyPairName;

    @Value("${aws.endpoint}")
    private String endpoint;

    @Value("${aws.nodeImageId}")
    private String nodeImageId;

    @Value("${aws.instanceType}")
    private String instanceType;

    @Value("${aws.securityGroupId}")
    private String securityGroupId;

    private AmazonEC2Client amazonEC2Client;


    @Override
	public void startNewNodeInstance()
            throws ClaudConnectionException {
        try {
            startNewNodeInstanceWithCloudSpecificExceptions();
        } catch (AmazonServiceException e) {
            throw new ClaudConnectionException(e);
        } catch (AmazonClientException e) {
            throw new ClaudConnectionException(e);
        }
	}

	@Override
	public void shutdownNodeInstaceOwnerForNode(Node node)
            throws ClaudConnectionException{
        try {
            shutdownNodeInstaceOwnerForNodeWithCloudSpecificExceptions(node);
        } catch (AmazonServiceException e) {
            throw new ClaudConnectionException(e);
        } catch (AmazonClientException e) {
            throw new ClaudConnectionException(e);
        }
	}

    private void startNewNodeInstanceWithCloudSpecificExceptions() {
        logger.debug("Start new instance");
        createClientIfNeeded();

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId(nodeImageId)
                .withInstanceType(instanceType)
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(keyPairName);
        runInstancesRequest.setUserData(getUserDataScript());
        amazonEC2Client.runInstances(runInstancesRequest);
    }

    private void shutdownNodeInstaceOwnerForNodeWithCloudSpecificExceptions(Node node) {
        createClientIfNeeded();
        String ipString = node.getIpAddress().getHostAddress();
        logger.debug("Shutdown host with ip:" + ipString);
        String id = getIdForPublicIp(ipString);
        if(id != null) terminateInstanceWithId(id);
    }

    private String getIdForPublicIp(String ipString) {
        createClientIfNeeded();
        Filter filter = getFilterForKeyValueString("ip-address", ipString);
        DescribeInstancesRequest request = new DescribeInstancesRequest()
                .withFilters(filter);
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
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest()
                .withInstanceIds(id);
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
