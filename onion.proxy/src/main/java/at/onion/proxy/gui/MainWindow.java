package at.onion.proxy.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.ProxyFactory;
import at.onion.proxy.TCPConnection;
import at.onion.proxy.proxyconnection.ProxyConnection;
import at.onion.proxy.socks4.Socks4TCPConnection;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class MainWindow extends JFrame {

	private static final long	serialVersionUID	= 3380807942528288991L;

	private Logger				logger				= LoggerFactory.getLogger(this.getClass());

	private ProxyManager		manager				= new ProxyManager(this);
	private JTextField			txtHost;
	private JTextField			txtPort;

	public MainWindow() {
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
		setSize(514, 298);
		getContentPane().setLayout(new BorderLayout());

		JSeparator separator = new JSeparator();

		JPanel panel = new JPanel();

		JButton btnExit = new JButton("Exit");

		// On exit click.
		btnExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				exitProgramm();
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 426,
												Short.MAX_VALUE)
										.addComponent(panel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 426,
												Short.MAX_VALUE).addComponent(btnExit, Alignment.TRAILING))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE).addGap(72)
						.addComponent(btnExit).addContainerGap()));

		final JComboBox comboBox = new JComboBox();

		for (Class<? extends ProxyConnection> a : ProxyFactory.getProxyTypes())
			comboBox.addItem(a.getSimpleName());

		final JLabel lblStatus = new JLabel("Status:");
		lblStatus.setVerticalAlignment(SwingConstants.TOP);

		final JCheckBox chckbxSocks5 = new JCheckBox("SOCKS5");
		final JCheckBox chckbxSocks4 = new JCheckBox("SOCKS4");

		chckbxSocks5.setSelected(true);
		chckbxSocks4.setSelected(true);

		final JButton btnStart = new JButton("Start Proxy");

		Timer timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (manager.isRunning()) {
					btnStart.setText("Stop Proxy");
					lblStatus.setText("Status: Running");
					chckbxSocks5.setEnabled(false);
					chckbxSocks4.setEnabled(false);
					comboBox.setEnabled(false);
				} else {
					btnStart.setText("Start Proxy");
					lblStatus.setText("Status: Stopped");
					chckbxSocks5.setEnabled(true);
					chckbxSocks4.setEnabled(true);
					comboBox.setEnabled(true);
				}
			}
		});
		timer.start();
		JLabel lblIp = new JLabel("Host:");

		txtHost = new JTextField();
		txtHost.setColumns(10);
		txtHost.setText("localhost");

		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setText("8001");

		JLabel lblPort = new JLabel("Port:");

		// On exit click.
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!manager.isRunning()) {
					List<Class<? extends TCPConnection>> allowedProxyConnections = new ArrayList<Class<? extends TCPConnection>>();

					if (chckbxSocks4.isSelected())
						allowedProxyConnections.add((Class<? extends TCPConnection>) Socks5TCPConnection.class);
					if (chckbxSocks5.isSelected())
						allowedProxyConnections.add((Class<? extends TCPConnection>) Socks4TCPConnection.class);

					Class<? extends TCPConnection>[] allowed = new Class[allowedProxyConnections.size()];

					for (int i = 0; i < allowedProxyConnections.size(); i++) {
						allowed[i] = allowedProxyConnections.get(i);
					}

					if (allowedProxyConnections.size() > 0) {
						for (Class<? extends ProxyConnection> connectionProxyClass : ProxyFactory.getProxyTypes()) {
							if (connectionProxyClass.getSimpleName().equals(comboBox.getSelectedItem())) {
								manager.startServer(allowed, connectionProxyClass, txtHost.getText().trim(),
										new Integer(txtPort.getText().trim().length() == 0 ? "-1" : txtPort.getText()
												.trim()));
								return;
							}
						}

						JOptionPane.showMessageDialog(null, "Combobox selection not found...!");
						exitProgramm();
					} else {
						JOptionPane.showMessageDialog(null, "No proxy type selected!");
					}
				} else {

					manager.stopServer();
				}

			}
		});

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addGroup(
								gl_panel.createParallelGroup(Alignment.LEADING, false)
										.addComponent(comboBox, 0, 178, Short.MAX_VALUE)
										.addComponent(chckbxSocks5)
										.addComponent(chckbxSocks4)
										.addComponent(btnStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_panel.createParallelGroup(Alignment.LEADING, false)
										.addGroup(
												gl_panel.createSequentialGroup()
														.addComponent(lblIp)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(txtHost, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addGap(28)
														.addComponent(lblPort)
														.addGap(3)
														.addComponent(txtPort, GroupLayout.PREFERRED_SIZE, 57,
																GroupLayout.PREFERRED_SIZE))
										.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addGroup(
								gl_panel.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_panel.createSequentialGroup()
														.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 25,
																GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chckbxSocks5)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(chckbxSocks4)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(btnStart))
										.addGroup(
												gl_panel.createSequentialGroup()
														.addGroup(
																gl_panel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(lblIp)
																		.addComponent(txtHost,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(lblPort)
																		.addComponent(txtPort,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 104,
																Short.MAX_VALUE))).addContainerGap()));
		panel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);

		setVisible(true);

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
