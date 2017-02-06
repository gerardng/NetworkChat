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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientWindow extends JFrame implements Runnable {
	private JPanel contentPane;
	
	private JTextField txtMessage;
	private JTextArea txtHistory;
	private DefaultCaret caret;
	
	private int ID = -1;
	private Client client;
	private Thread listen, run;
	private boolean running = false;
	
	public ClientWindow(String name, String address, int port) {
		setTitle("GChat Client");
		client = new Client(name, address, port);
		if(!client.openConnection(address)) {
			System.err.println("Error connecting!");
			console("Error connecting!");
		}
		createWindow();
		console("Client.java:Connecting to " + address + " on port: " + port + " with alias: " + name);
		console("Client.java:Successfully connected");
		String connection = "/c/" + name + "/e/";
		client.send(connection.getBytes());
		run = new Thread(this, "Running");
		run.start();
		running = true;
	}
	
	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{50, 0, 0};
		gbl_contentPane.rowHeights = new int[]{50, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText(), true);
				}
			}
		});
		
		txtHistory = new JTextArea();
		txtHistory.setEditable(false);
		
		// Display caret
		caret = (DefaultCaret) txtHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		// Apply scrolling
		JScrollPane scrollPage = new JScrollPane(txtHistory);
		
		GridBagConstraints gbc_txtHistory = new GridBagConstraints();
		gbc_txtHistory.insets = new Insets(20, 0, 5, 5);
		gbc_txtHistory.fill = GridBagConstraints.BOTH;
		gbc_txtHistory.gridx = 0;
		gbc_txtHistory.gridy = 0;
		gbc_txtHistory.gridwidth = 2;
		gbc_txtHistory.insets = new Insets(20, 0, 0, 0);
		contentPane.add(scrollPage, gbc_txtHistory);
		txtMessage.setText(">");
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
				
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText(), true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);	
		txtMessage.requestFocusInWindow();
	}
	
	public void console(String message) {
		txtHistory.append(message + "\n\r");
		txtHistory.setCaretPosition(txtHistory.getDocument().getLength());
	}
	
	private void send(String message, boolean text) {
		if (message.equals("")) return;
		if (text) {
			message = client.getName() + ": " + message;
			message = "/m/" + message + "/e/";
			txtMessage.setText("");
		}
		client.send(message.getBytes());
	}
	
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while(running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						String text = message.substring(3);
						text = text.split("/e/")[0];
						console(text);
					}
				}
			}
		};
		listen.start();
	}
	
	public void run() {
		// creates a new thread
		listen();
	}

}
