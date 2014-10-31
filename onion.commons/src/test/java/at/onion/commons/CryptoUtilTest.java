package at.onion.commons;

import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Objects;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.Test;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class CryptoUtilTest {

	@Test
	public void keyPairGeneration() throws NoSuchAlgorithmException, NoSuchProviderException {
		BASE64Encoder b64 = new BASE64Encoder();
		KeyPair keyPair = CryptoUtils.generateDefaultRSAKeyPair();
		
		Key pubKey = keyPair.getPublic();
        Key privKey = keyPair.getPrivate();

        System.out.println("publicKey : " + b64.encode(pubKey.getEncoded()));
        System.out.println("privateKey : " + b64.encode(privKey.getEncoded()));
	}
	
	@Test
	public void keyWrapping() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException {
		KeyPair keyPair = CryptoUtils.generateDefaultRSAKeyPair();
		SecretKey sKey = CryptoUtils.generateSecretAESKey(256);
		
		byte[] wrappedKey = CryptoUtils.wrapSecretKey(keyPair.getPublic(), sKey);
		SecretKey result = CryptoUtils.unwrapSecretKey(keyPair.getPrivate(), wrappedKey);
		
		assertTrue(Objects.equals(sKey, result));
	}
}
