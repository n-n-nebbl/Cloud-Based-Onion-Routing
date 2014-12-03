package at.onion.chainnode;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForwardingThread implements Runnable {

	private Logger			logger	= LoggerFactory.getLogger(getClass());
	private NetworkService	networkService;
	private Socket			in;
	private Socket			out;
	private ForwardingMode	mode;

	public ForwardingThread(Socket in, Socket out, ForwardingMode mode) {
		this.networkService = new NetworkService();
		this.in = in;
		this.out = out;
		this.mode = mode;
	}

	@Override
	public void run() {
		try {
			networkService.forwardMessage(in.getInputStream(), out.getOutputStream(), mode, in.getReceiveBufferSize());
		} catch (EOFException e) {
		} catch (SocketException e) {
			if (!e.getMessage().toLowerCase().contains("closed")) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.debug(mode.getForwardingMode() + " " + mode.getRouteMode());
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}
}