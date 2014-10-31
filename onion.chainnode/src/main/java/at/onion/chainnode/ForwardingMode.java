package at.onion.chainnode;

import java.security.Key;

public class ForwardingMode {

	public static final int FORWARDING_NODE_TO_NODE = 1;
	public static final int FORWARDING_NODE_TO_TARGET = 2;
	public static final int FORWARDING_TARGET_TO_NODE = 3;
	
	public static final int ROUTE_TO_SERVER = 11;
	public static final int ROUTE_TO_CLIENT = 12;
	
	private int forwardingMode;
	private int routeMode;
	
	private Key clientPublicKey;
	
	public ForwardingMode(int forwardingMode, int routeMode) {
		this(forwardingMode, routeMode, null);
		if(routeMode == ROUTE_TO_CLIENT) {
			throw new IllegalArgumentException("RouteToClientMode needs the public key of the client!");
		}
	}
	
	public ForwardingMode(int forwardingMode, int routeMode, Key clientKey) {
		this.forwardingMode = forwardingMode;
		this.routeMode = routeMode;
		this.clientPublicKey = clientKey;
	}
	
	public int getForwardingMode() {
		return this.forwardingMode;
	}
	
	public int getRouteMode() {
		return this.routeMode;
	}
	
	public Key getClientPublicKey() {
		return this.clientPublicKey;
	}
}
