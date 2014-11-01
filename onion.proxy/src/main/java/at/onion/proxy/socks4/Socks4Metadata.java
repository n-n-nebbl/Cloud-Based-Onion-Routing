package at.onion.proxy.socks4;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import at.onion.proxy.SocksException;

public class Socks4Metadata {
	public static final int	proxySocketTimeout			= (int) MILLISECONDS.convert(1, MINUTES);

	public final static int	SOCKS4_Reserved				= 0x00;

	public final static int	SOCKS4_Version				= 0x04;
	public final static int	SOCKS4_REP_SUCCEEDED		= 90;
	public final static int	SOCKS4_REP_REJECTED			= 91;
	public final static int	SOCKS4_REP_IDENT_FAIL		= 92;
	public final static int	SOCKS4_REP_USERID			= 93;

	public final static int	SOCKS4_CMD_TCP_CONNECTION	= 0x01;
	public final static int	SOCKS4_CMD_TCP_BINDING		= 0x02;

	public static String getSocks4ResponseName(int response) throws SocksException {
		switch (response) {
		case SOCKS4_REP_SUCCEEDED:
			return "request granted (succeeded)";
		case SOCKS4_REP_REJECTED:
			return "request rejected or failed";
		case SOCKS4_REP_IDENT_FAIL:
			return "cannot connect identd";
		case SOCKS4_REP_USERID:
			return "user id not matched";
		default:
			throw new SocksException("Response " + response + " not implemented.");
		}
	}
}