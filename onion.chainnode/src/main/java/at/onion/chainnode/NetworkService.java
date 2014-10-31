package at.onion.chainnode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainMessage;
import static at.onion.chainnode.ForwardingMode.*;

public class NetworkService {

	/**
	 * forward any message that comes through inputstream to outputstream read
	 * byte[] will be decrypted. the payload of the decrypted message will be
	 * sent to outputstream
	 * 
	 * @param in
	 * @param out
	 */
	public void forwardMessage(InputStream in, OutputStream out, ForwardingMode mode) throws IOException,
			ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		ObjectInputStream ois;
		ObjectOutputStream oos;
		byte[] input, payload;
		NodeChainMessage msg;
		switch (mode.getForwardingMode()) {
		case FORWARDING_NODE_TO_NODE:
			ois = new ObjectInputStream(in);
			oos = new ObjectOutputStream(out);
			while (true) {
				input = (byte[]) ois.readObject();

				if (mode.getRouteMode() == ROUTE_TO_SERVER) {
					msg = CryptoUtils.decryptMessage(input);
					payload = msg.getPayload();
				} else {
					payload = CryptoUtils.encrypt(mode.getClientPublicKey(), input);
				}
				oos.writeObject(payload);
			}
		case FORWARDING_NODE_TO_TARGET:
			while (true) {
				ois = new ObjectInputStream(in);
				input = (byte[]) ois.readObject();
				msg = CryptoUtils.decryptMessage(input);

				payload = msg.getPayload();

				out.write(payload);
			}
		case FORWARDING_TARGET_TO_NODE:
			// TODO: timeout?
			oos = new ObjectOutputStream(out);
			String line;
			System.out.println("read from target");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				payload = CryptoUtils.encrypt(mode.getClientPublicKey(), line.getBytes());
				System.out.println("server reply: " + line);
				oos.writeObject(payload);
			}

			System.out.println("finished reading from target");
			break;
		}
	}

	/**
	 * read and decrypt a NodeChainMessage from the inputstream
	 * 
	 * @param in
	 * @return read NodeChainMessage
	 */
	public NodeChainMessage readMessage(InputStream in) throws IOException, ClassNotFoundException,
			InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		ObjectInputStream ois = new ObjectInputStream(in);

		byte[] input = (byte[]) ois.readObject();
		NodeChainMessage msg = CryptoUtils.decryptMessage(input);

		return msg;
	}

	/**
	 * send encrypted NodeChainMessage
	 * 
	 * @param out
	 * @param msg
	 * @throws ClassNotFoundException
	 */
	public void sendMessage(OutputStream out, NodeChainMessage msg) throws IOException, ClassNotFoundException {
		try {
			System.out.println("send message: " + new String(msg.getPayload()));
		} catch (Exception e) {
		}
		if (!msg.getHeader().isTarget()) {
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(msg.getPayload());
		} else {
			byte[] payload = msg.getPayload();
			out.write(payload);
		}
	}
}