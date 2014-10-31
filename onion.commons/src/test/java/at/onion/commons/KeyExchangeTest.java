package at.onion.commons;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;

import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import static org.junit.Assert.*;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class KeyExchangeTest {

	
	public void testKeyExchange() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, ClassNotFoundException, NoSuchPaddingException {
		BASE64Encoder b64 = new BASE64Encoder();
		
		ServerUtils sutil = new ServerUtils();
		KeyExchangeConnectionHandler server = new KeyExchangeConnectionHandler();
		sutil.startAcceptingConnections(9999, server);
		
		KeyExchangeConnectionHandler client = new KeyExchangeConnectionHandler();
		client.connect("localhost", 9999);
		
		System.out.println("publicKey : " + b64.encode(client.getOtherPublicKey().getEncoded()));
		
		server.getSocket().close();
		client.getSocket().close();
		sutil.stopAcceptionConnections(9999);
		
		assertTrue(Objects.equals(client.getOtherPublicKey(), CryptoUtils.getKeyPair().getPublic()));
	}
}
