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

public class Client extends JFrame {
	private static final long serailVersionUID = 1L;

	private JPanel contentPane;
	
	private String name, address;
	private int port;
	private JTextField txtMessage;
	private JTextArea txtHistory;
	private DefaultCaret caret;
	// Our connection to the network
	private DatagramSocket datagramSocket;
	private InetAddress ip;
	private Thread send;

	public Client(String name, String address, int port) {
		setTitle("GChat Client");
		this.name = name;
		this.address = address;
		this.port = port;
		if(!openConnection(address)) {
			System.err.println("Error connecting!");
			console("Error connecting!");
		}
		createWindow();
		console("Client.java:Connecting to " + address + " on port: " + port + " with alias: " + name);
		console("Client.java:Successfully connected");
		String connection = name + " connected from: " + address + ":" + port;
		send(connection.getBytes());
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
					send(txtMessage.getText());
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
				send(txtMessage.getText());
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
	}
	
	public void send(String message) {
		if(message.equals("")) {
			return;
		}
		message = name + ": " + message;
		console(message);
		send(message.getBytes());
		txtHistory.setCaretPosition(txtHistory.getDocument().getLength());
		txtMessage.setText("");
	}
	
	// Creates individual thread to send packet through a socket
	public void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				// Datagram needs port if you are sending
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length, ip, port);
				try {
					datagramSocket.send(datagramPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	public String receive() {
		// Create a packet storage with a byte array
		byte[] data = new byte[512];
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
		try {
			// Socket receives the packet
			// Potential to cause error, will sit until it receives data into the packet
			datagramSocket.receive(datagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Display output
		String message = new String(datagramPacket.getData());
		return message;
	}
	
	public boolean openConnection(String address) {
		// Convert String to InetAddress
		try {
			// No parameters means connect to any available port
			datagramSocket = new DatagramSocket();
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch(SocketException e) {
			e.printStackTrace();
			return false;			
		}
		return true;
	}

}
