package at.onion.chainnode;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;

public class ForwardingThread implements Runnable {

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
			networkService.forwardMessage(in.getInputStream(), out.getOutputStream(), mode);
		} catch (EOFException e) {
		} catch (SocketException e) {
			if (!e.getMessage().toLowerCase().contains("closed")) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println(mode.getForwardingMode() + " " + mode.getRouteMode());
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