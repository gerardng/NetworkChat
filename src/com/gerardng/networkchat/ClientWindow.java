package com.gerardng.networkchat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ClientWindow extends JFrame implements Runnable {
	private JPanel contentPane;
	
	private JTextField txtMessage;
	private JTextArea txtHistory;
	private DefaultCaret caret;
	
	private Client client;
	private Thread listen, run;
	private boolean runningFlag = false;
	private JLabel loggedInLabel;
	
	public ClientWindow(String name, String address, int port) {
		runningFlag = true;
		client = new Client(name, address, port);
		if(!client.connect(address)) {
			System.err.println("Error connecting!");
			console("Error connecting!");
		}
		createUI();
		console("ClientWindow.java: Successfully connected to " + address + " on port: " + port + " with alias: " + name);
		String connection = "/c/" + name;
		client.send(connection.getBytes());
		run = new Thread(this, "Running");
		run.start();
	}
	
	// Window builder editor
	private void createUI() {
		setTitle("GChat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{50, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 50, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
				}
			}
		});
		
		txtHistory = new JTextArea();
		txtHistory.setEditable(false);
		
		// Display caret
		caret = (DefaultCaret) txtHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		loggedInLabel = new JLabel("Logged in as: " + client.getName());
		GridBagConstraints gbc_loggedInLabel = new GridBagConstraints();
		gbc_loggedInLabel.insets = new Insets(0, 0, 5, 5);
		gbc_loggedInLabel.gridx = 0;
		gbc_loggedInLabel.gridy = 0;
		contentPane.add(loggedInLabel, gbc_loggedInLabel);
		
		// Apply scrolling
		JScrollPane scrollPage = new JScrollPane(txtHistory);
		
		GridBagConstraints gbc_txtHistory = new GridBagConstraints();
		gbc_txtHistory.insets = new Insets(20, 0, 5, 5);
		gbc_txtHistory.fill = GridBagConstraints.BOTH;
		gbc_txtHistory.gridx = 0;
		gbc_txtHistory.gridy = 1;
		gbc_txtHistory.gridwidth = 2;
		gbc_txtHistory.insets = new Insets(20, 0, 0, 0);
		contentPane.add(scrollPage, gbc_txtHistory);
		txtMessage.setText(">");
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 3;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
				
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 3;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect);
				runningFlag = false;
				client.quit();
			}
		});
		
		setVisible(true);	
		txtMessage.requestFocusInWindow();
	}
	
	// Run method
	public void run() {
		// Parses messages from socket
		listen = new Thread("Listen") {
			public void run() {
				while (runningFlag) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						String text = message.split("/m/|/e/")[1];
						console(text);
					}
				}
			}
		};
		listen.start();
	}
	
	// Prints messages the console
	public void console(String message) {
		txtHistory.append(message + "\n\r");
		txtHistory.setCaretPosition(txtHistory.getDocument().getLength());
	}
	
	// Wrapper method for Client's send(byte[])
	private void send(String message) {
		if (message.equals("")){
			return;
		} else if (message.startsWith("/d/")) {
			client.send(message.getBytes());
			txtMessage.setText("");
		} else {
			message = client.getName() + ": " + message;
			message = "/m/" + message;
			client.send(message.getBytes());
			txtMessage.setText("");
		}

	}
}
