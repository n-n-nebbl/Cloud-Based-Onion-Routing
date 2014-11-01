package at.onion.proxy.proxyconnection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

import at.onion.proxy.TCPConnection;

public class FilterTestProxyConnection extends TestProxyConnection {

	public FilterTestProxyConnection(String host, int port, TCPConnection socksConnection) throws UnknownHostException,
			IOException {
		super(host, port, socksConnection);
	}

	// If you want to do this correctly you have to buffer :)
	// Also gzip and stuff like that
	// Also HTTP length and stuff like that :)
	// http://stackoverflow.com/questions/10739448/java-library-for-byte-array-manipulations
	@Override
	public void sendToClient(byte[] message) {

		// Open http://www.online-tutorials.net/home/impressum.html :)
		message = searchAndReplace(message, "simon".getBytes(), "_GOD_".getBytes());
		message = searchAndReplace(message, "Simon".getBytes(), "_GOD_".getBytes());

		super.sendToClient(message);
	}

	public static int searchFor(byte[] array, byte[] subArray) {
		if (subArray.length > array.length) return -1;
		int p = (new String(array)).indexOf(new String(subArray));
		for (int i = 1; i < subArray.length; i++) {
			if (array[p + i] != subArray[i]) return -1;
		}
		return p;
	}

	public static byte[] searchAndReplace(byte[] array, byte[] search, byte[] replace) {
		if (search.length != replace.length) return array;
		int p = searchFor(array, search);
		if (p == -1) return array;

		byte[] result = Arrays.copyOf(array, array.length);
		for (int i = 0; i < replace.length; i++) {
			result[p] = replace[i];
			p++;
		}
		return result;
	}
}
