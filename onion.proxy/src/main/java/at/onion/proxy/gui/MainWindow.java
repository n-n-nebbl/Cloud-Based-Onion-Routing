package at.onion.proxy.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.proxyconnection.TestProxyConnection;
import at.onion.proxy.socks4.Socks4TCPConnection;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class MainWindow extends JFrame {

	private static final long	serialVersionUID	= 3380807942528288991L;

	private Logger				logger				= LoggerFactory.getLogger(this.getClass());

	public Panel				_ButtonPanel		= new Panel();

	public Button				_StartStop			= new Button();

	public Panel				_ErrorPanel			= new Panel();

	public Panel				_ActionPanel		= new Panel();

	public Label				_title1				= new Label();

	public Label				_title2				= new Label();

	public Label				_ErrorLog			= new Label();

	public Label				_ActionLog			= new Label();

	private ProxyManager		manager				= new ProxyManager(this);

	public MainWindow() {

		manager.startServer(new Class[] { Socks5TCPConnection.class, Socks4TCPConnection.class },
				TestProxyConnection.class);

		createGUI();

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent arg0) {
				arg0.getWindow().dispose();
				doExit();
			}
		});
	}

	private void doExit() {
		logger.info("Closing window...");
		manager.stopServer();
		System.exit(0);

	}

	public void createGUI() {
		setTitle("Proxy Server");
		setSize(500, 300);
		setLayout(new BorderLayout());

		_title1.setText("ErrorLog");
		_title2.setText("ActionLog");

		_ButtonPanel.setLayout(new FlowLayout());
		_ButtonPanel.add(_StartStop, FlowLayout.LEFT);

		_ErrorPanel.setLayout(new BorderLayout());
		_ErrorPanel.add(_title1, BorderLayout.NORTH);
		_ErrorPanel.add(_ErrorLog, BorderLayout.NORTH);

		_ActionPanel.setLayout(new BorderLayout());
		_ActionPanel.add(_title2, BorderLayout.NORTH);
		_ActionPanel.add(_ActionLog, BorderLayout.NORTH);

		add(_ButtonPanel);
		add(_ErrorPanel);
		add(_ActionPanel);

		setVisible(true);

	}

	public void add_ErrorLog(String s) {
		Label l = new Label();
		l.setText(s);
		_ErrorPanel.add(l, BorderLayout.SOUTH);
		_ErrorPanel.repaint();
	}

	public void add_ActionLog(String s) {
		Label l = new Label();
		l.setText(s);
		_ActionPanel.add(l, BorderLayout.SOUTH);
		_ActionPanel.repaint();
	}

	public void exitProgramm() {
		// this will make sure WindowListener.windowClosing() et al. will be
		// called.
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

		// Not necessary -> already to windowClosing
		doExit();
	}
}
