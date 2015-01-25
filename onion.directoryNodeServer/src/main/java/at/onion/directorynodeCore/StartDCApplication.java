package at.onion.directorynodeCore;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import at.onion.directorynodeCore.nodeInstanceService.SimpleNodeInstanceService;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;

public class StartDCApplication {
	private static PropertiesCredentials	credentials;
	private static String					TAG_NAME				= "g3-t3-directorynode";
	private static String					INSTANCE_TYPE			= "t2.micro";
	private static String					KEY_NAME				= "aic14-group3-topic3";
	private static String					IMAGE_ID				= "ami-07762037";
	private static String					ENDPOINT				= "ec2.us-west-2.amazonaws.com";
	private static String					STARTFILE_DIRECTORYNODE	= "start_directorynodeserver.sh";

	public static void main(String[] args) {

		try {
			AmazonEC2Client ec2 = new AmazonEC2Client();
			ec2.setEndpoint(ENDPOINT);
			credentials = new PropertiesCredentials(new File("awscredentials.properties"));
			SimpleNodeInstanceService.startNewNodeInstanceWithCloudSpecificExceptions(ec2, TAG_NAME, IMAGE_ID,
					INSTANCE_TYPE, KEY_NAME, null, getUserDataScript(STARTFILE_DIRECTORYNODE), credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String encodeLines(List<String> lines) {
		String str = new String(Base64.encodeBase64(join(lines, "\n").getBytes()));
		return str;
	}

	private static String join(Collection<String> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	private static String getUserDataScript(String file) throws IOException {
		List<String> lines = Files.readAllLines(new File(file).toPath(), Charset.forName("UTF-8"));
		String str = encodeLines(lines);
		return str;
	}
}
