package com.gerardng.networkchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.gerardng.networkchat.server.ServerObject;
import com.gerardng.networkchat.server.UniqueIdentifier;

public class Client {
	private static final long serailVersionUID = 1L;

	// Our connection to the network
	private DatagramSocket datagramSocket;
	private InetAddress ip;
	private String name, address;
	private Thread send;
	private int port;
	private int ID = -1;

	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	// Creates individual thread to send packet through a socket
	public void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				// DataGram needs port if you are sending
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
	
	// Creates a packet to receive data from the socket
	public String receive() {
		// Create a packet storage with a byte array
		byte[] data = new byte[1024];
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
	
	public boolean connect(String address) {
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
	
	// Close the socket
	public void quit() {
		new Thread() {
			public void run() {
				synchronized(datagramSocket) {
					datagramSocket.close();
				}
			}
		}.start();
	}

	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int id) {
		this.ID = id;
	}
}
