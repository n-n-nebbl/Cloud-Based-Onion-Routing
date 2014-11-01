package at.onion.proxy.socks5;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import at.onion.proxy.SocksException;

public class Socks5Metadata {
	public static final int	proxySocketTimeout			= (int) MILLISECONDS.convert(1, MINUTES);

	public final static int	SOCKS5_Version				= 0x05;
	public final static int	SOCKS5_Reserved				= 0x00;

	public final static int	SOCKS5_HOSTNAMETYPE_IPV4	= 0x01;
	public final static int	SOCKS5_HOSTNAMETYPE_IPV6	= 0x04;
	public final static int	SOCKS5_HOSTNAMETYPE_NAME	= 0x03;

	public final static int	SOCKS5_REP_SUCCEEDED		= 0x00;
	public final static int	SOCKS5_REP_FAIL				= 0x01;
	public final static int	SOCKS5_REP_NALLOWED			= 0x02;
	public final static int	SOCKS5_REP_NUNREACH			= 0x03;
	public final static int	SOCKS5_REP_HUNREACH			= 0x04;
	public final static int	SOCKS5_REP_REFUSED			= 0x05;
	public final static int	SOCKS5_REP_EXPIRED			= 0x06;
	public final static int	SOCKS5_REP_CNOTSUP			= 0x07;
	public final static int	SOCKS5_REP_ANOTSUP			= 0x08;
	public final static int	SOCKS5_REP_INVADDR			= 0x09;

	public final static int	SOCKS5_AUTH_NOAUTH			= 0x00;
	public final static int	SOCKS5_AUTH_GSSAPI			= 0x01;
	public final static int	SOCKS5_AUTH_USERPASS		= 0x02;
	public final static int	SOCKS5_AUTH_CHAP			= 0x03;
	public final static int	SOCKS5_AUTH_EAP				= 0x05;
	public final static int	SOCKS5_AUTH_MAF				= 0x08;
	public final static int	SOCKS5_AUTH_REJECT			= 0xFF;

	public static String getSocks5ResponseName(int response) throws SocksException {
		switch (response) {
		case SOCKS5_REP_SUCCEEDED:
			return "succeeded";
		case SOCKS5_REP_FAIL:
			return "general SOCKS server failure";
		case SOCKS5_REP_NALLOWED:
			return "connection not allowed by ruleset";
		case SOCKS5_REP_NUNREACH:
			return "Network unreachable";
		case SOCKS5_REP_HUNREACH:
			return "Host unreachable";
		case SOCKS5_REP_REFUSED:
			return "connection refused";
		case SOCKS5_REP_EXPIRED:
			return "TTL expired";
		case SOCKS5_REP_CNOTSUP:
			return "Command not supported";
		case SOCKS5_REP_ANOTSUP:
			return "Address not supported";
		case SOCKS5_REP_INVADDR:
			return "Invalid address";
		default:
			throw new SocksException("Response " + response + " not implemented.");
		}
	}

	public static int convertTwoBytesToPort(byte b2, byte b1) {
		return (int) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
	}

	public static String convertIPAddress(byte[] rawBytes, int nr) {
		int i = nr;
		String ipAddress = "";
		for (byte raw : rawBytes) {
			ipAddress += (raw & 0xFF);
			if (--i > 0) {
				ipAddress += ".";
			}
		}

		return ipAddress;
	}
}