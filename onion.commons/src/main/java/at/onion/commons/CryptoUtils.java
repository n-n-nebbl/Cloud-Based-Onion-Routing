package at.onion.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CryptoUtils {

	private static KeyPair		keyPair;
	private static KeyStore		keyStore;
	private static char[]		keyStorePw;
	private static SSLContext	ctx;
	private static final String	rsaCypther	= "RSA/NONE/PKCS1Padding";

	public static KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		if (keyPair == null) {
			synchronized (CryptoUtils.class) {
				if (keyPair == null) {
					keyPair = generateDefaultRSAKeyPair();
				}
			}
		}
		return keyPair;
	}

	public static KeyStore getKeyStore() throws NoSuchAlgorithmException, NoSuchProviderException {
		if (keyStore == null) {
			synchronized (trustAllCerts) {
				if (keyStore == null) {
					keyStore = createKeyStoreDynamically(getKeyPair(), "1234567");
				}
			}
		}
		return keyStore;
	}

	public static char[] getKeyStorePw() {
		return keyStorePw;
	}

	public static SSLContext getSSLContext() throws NoSuchAlgorithmException, UnrecoverableKeyException,
			KeyStoreException, NoSuchProviderException, KeyManagementException {
		if (ctx == null) {
			KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
			getKeyStore();
			keyMgrFactory.init(keyStore, keyStorePw);

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyMgrFactory.getKeyManagers(), trustAllCerts, null);

			ctx = context;
		}
		return ctx;
	}

	/**
	 * Generates a KeyPair
	 * 
	 * @param keyPairAlgorithm
	 *            e.g. "RSA"
	 * @param secRandAlgorithm
	 *            e.g. "SHA1PRNG"
	 * @param strength
	 *            keySize
	 * @return generated KeyPair
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static KeyPair generateKeyPair(String keyPairAlgorithm, String secRandAlgorithm, int strength)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		checkSecurityProvider();

		KeyPairGenerator generator = KeyPairGenerator.getInstance(keyPairAlgorithm, "BC");
		SecureRandom random = SecureRandom.getInstance(secRandAlgorithm);
		generator.initialize(strength, random);
		KeyPair pair = generator.generateKeyPair();

		return pair;
	}

	/**
	 * Generates a RSA KeyPair with SHA1PRNG as SecureRandom and with keySize
	 * 1024
	 * 
	 * @return Generated KeyPair
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static KeyPair generateDefaultRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		return generateKeyPair("RSA", "SHA1PRNG", 1024);
	}

	private static void checkSecurityProvider() {
		if (Security.getProvider("BC") == null) {
			synchronized (CryptoUtils.class) {
				if (Security.getProvider("BC") == null) {
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				}
			}
		}
	}

	public static byte[] encrypt(Key publicKey, Object object) throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
			IllegalBlockSizeException, BadPaddingException {
		return encrypt(publicKey, serialize(object));
	}

	public static byte[] encrypt(Key publicKey, byte[] bytes) throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		Cipher c = Cipher.getInstance(rsaCypther, "BC");
		c.init(Cipher.ENCRYPT_MODE, publicKey);
		return blockCipher(bytes, Cipher.ENCRYPT_MODE, c);
	}

	public static NodeChainMessage decryptMessage(byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		byte[] decrypted = decrypt(bytes);
		Object o = deserialize(decrypted);
		if (o instanceof NodeChainMessage) {
			return (NodeChainMessage) o;
		}
		throw new IllegalArgumentException("decrypted byte[] is not a NodeChainMessage");
	}

	public static byte[] decrypt(byte[] bytes) throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		Cipher c = Cipher.getInstance(rsaCypther, "BC");
		c.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
		byte[] result = blockCipher(bytes, Cipher.DECRYPT_MODE, c);
		return result;
	}

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	/**
	 * Creates an encrypted ServerSocket
	 * 
	 * @param port
	 * @return
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	public static ServerSocket createEncryptedServerSocket(int port) throws IOException, UnrecoverableKeyException,
			KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		return getSSLContext().getServerSocketFactory().createServerSocket(port);
	}

	public static Socket createEncryptedSocket(String hostname, int port) throws UnknownHostException, IOException,
			NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException {
		return getSSLContext().getSocketFactory().createSocket(hostname, port);
	}

	public static KeyStore createKeyStoreDynamically(final KeyPair rsaKeyPair, String keyStorePw) {
		try {
			/**
			 * * JKS: Java Keystore (Sun's Keystore format) PKCS #12: Public-Key
			 * Cryptography Standards #12 Keystore (RSA's Personal Information
			 * Exchange Syntax Standard) JCEKS: Java Cryptography Extension
			 * Keystore (More secure version of JKS) JKS (case sensitive): Case
			 * sensitive JKS BKS: Bouncy Castle Keystore (Bouncy Castle's
			 * version of JKS) UBER: Bouncy Castle UBER Keystore (More secure
			 * version of BKS) GKR: GNU Keyring keystore (requires GNU Classpath
			 * version 0.90 or later installed)
			 */
			KeyStore ks = null;
			ks = KeyStore.getInstance("UBER");
			ks.load(null);

			char[] pw = keyStorePw.toCharArray();
			CryptoUtils.keyStorePw = pw;

			final X509Certificate certificate = createCertificate(rsaKeyPair);

			certificate.checkValidity();

			final X509Certificate[] certificateChain = { certificate };

			ks.setKeyEntry("rsa-public", rsaKeyPair.getPublic(), pw, null);
			ks.setKeyEntry("rsa-private", rsaKeyPair.getPrivate(), pw, certificateChain);

			return ks;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static X509Certificate createCertificate(KeyPair keyPair) throws OperatorCreationException,
			InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException {
		// Generate self-signed certificate
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.OU, "test");
		builder.addRDN(BCStyle.O, "test");
		builder.addRDN(BCStyle.CN, "name");

		Date notBefore = new Date(System.currentTimeMillis() - (1000 * 3600));
		Date notAfter = new Date(System.currentTimeMillis() + (1000 * 3600 * 24 * 365));
		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

		X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(builder.build(), serial, notBefore,
				notAfter, builder.build(), keyPair.getPublic());
		ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(
				keyPair.getPrivate());
		X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC")
				.getCertificate(certGen.build(sigGen));
		cert.checkValidity(new Date());
		cert.verify(cert.getPublicKey());

		return cert;
	}

	private static TrustManager[]	trustAllCerts	= new TrustManager[] { new X509TrustManager() {
														public void checkClientTrusted(
																java.security.cert.X509Certificate[] certs,
																String authType) {
														}

														public void checkServerTrusted(
																java.security.cert.X509Certificate[] certs,
																String authType) {
														}

														public java.security.cert.X509Certificate[] getAcceptedIssuers() {
															return null;
														}
													} };

	private synchronized static byte[] blockCipher(byte[] bytes, int mode, Cipher cipher)
			throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException,
			IOException {
		// string initialize 2 buffers.
		// scrambled will hold intermediate results
		byte[] scrambled = new byte[0];

		// toReturn will hold the total result
		byte[] toReturn = new byte[0];
		// if we encrypt we use 100 byte long blocks. Decryption requires 128
		// byte long blocks (because of RSA)
		// TODO: -50 is not very good optimized :D
		int length = (mode == Cipher.ENCRYPT_MODE) ? (1024 / 8) - 50 : (1024 / 8);

		if (length > bytes.length) {
			length = bytes.length;
		}

		// another buffer. this one will hold the bytes that have to be modified
		// in this step
		byte[] buffer = new byte[(bytes.length > length ? length : bytes.length)];

		for (int i = 0; i < bytes.length; i++) {

			// if we filled our buffer array we have our block ready for de- or
			// encryption
			if ((i > 0) && (i % length == 0)) {
				// execute the operation
				// System.out.println(length);
				scrambled = cipher.doFinal(buffer);

				// add the result to our total result.
				toReturn = append(toReturn, scrambled);
				// here we calculate the length of the next buffer required
				int newlength = length;

				// if newlength would be longer than remaining bytes in the
				// bytes array we shorten it.
				if (i + length > bytes.length) {
					newlength = bytes.length - i;
				}
				// clean the buffer array
				buffer = new byte[newlength];
			}
			// copy byte into our buffer.
			buffer[i % length] = bytes[i];
		}

		// this step is needed if we had a trailing buffer. should only happen
		// when encrypting.
		// example: we encrypt 110 bytes. 100 bytes per run means we "forgot"
		// the last 10 bytes. they are in the buffer array
		scrambled = cipher.doFinal(buffer);

		// final step before we can return the modified data.
		toReturn = append(toReturn, scrambled);

		return toReturn;
	}

	private static byte[] append(byte[] prefix, byte[] suffix) {
		byte[] toReturn = new byte[prefix.length + suffix.length];
		for (int i = 0; i < prefix.length; i++) {
			toReturn[i] = prefix[i];
		}
		for (int i = 0; i < suffix.length; i++) {
			toReturn[i + prefix.length] = suffix[i];
		}
		return toReturn;
	}

	private static byte[] cut(byte[] b, int start, int end) {
		byte[] result = new byte[end - start];
		int index = 0;
		for (int i = start; i < end; i++) {
			result[index++] = b[i];
		}
		return result;
	}

	/*
	 * Maybe used later -----
	 */

	/**
	 * Generates an AES SecretKey. Can be used as session-key.
	 * 
	 * @param keySize
	 *            Size of the session-key
	 * @return generated SecretKey
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static SecretKey generateSecretAESKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");
		keyGen.init(keySize);
		SecretKey secretKey = keyGen.generateKey();
		return secretKey;
	}

	/**
	 * Generates encrypted byte[] of the given session-key ready for transfer to
	 * communication-partner
	 * 
	 * @param publicKey
	 *            PublicKey of the communication-partner used to encrypt session
	 *            key
	 * @param sKey
	 *            This session key will be used for encryption/decryption of the
	 *            network-data-stream
	 * @return encrypted byte[]
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] wrapSecretKey(Key publicKey, SecretKey sKey) throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Cipher c = Cipher.getInstance("RSA", "BC");
		c.init(Cipher.WRAP_MODE, publicKey);
		byte[] result = c.wrap(sKey);
		return result;
	}

	/**
	 * Decrypts the byte[] and generates the session-key used for communication
	 * 
	 * @param privateKey
	 *            own private-key
	 * @param sKeyBytes
	 *            encrypted session-key
	 * @return decrypted session-key
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 */
	public static SecretKey unwrapSecretKey(Key privateKey, byte[] sKeyBytes) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("RSA", "BC");
		c.init(Cipher.UNWRAP_MODE, privateKey);
		SecretKey sKey = (SecretKey) c.unwrap(sKeyBytes, "AES", Cipher.SECRET_KEY);
		return sKey;
	}
}
