package com.gerardng.networkchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Thread
public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private int port;
	private DatagramSocket datagramSocket;
	private Thread runThread, manageThread, receiveThread, sendThread;
	private boolean running = false;
	private Thread send;
	
	public Server(int port) {
		this.port = port;
		try {
			datagramSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		//Thread(target Obj's run method, name)
		runThread = new Thread(this, "Server");
		runThread.start();
	}

	@Override
	public void run() {
		running = true;
		System.out.println("Server.java:Server started on port " + port);
		manageClients();
		receive();
	}
	
	private void manageClients() {
		manageThread = new Thread("Manage") {
			public void run() {
				while(running) {
					// Running threads
				}
			}
		};
		manageThread.start();
	}
	
	private void receive() {
		receiveThread = new Thread("Receive") {
			public void run() {
				while(running) {
					// Running threads
					byte[] data = new byte[512];
					DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
					try {
						datagramSocket.receive(datagramPacket);
						//datagramPacket.getAddress();
						//datagramPacket.getPort();
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(datagramPacket);

					System.out.println("Server.java:Clients connected (" + clients.size() + ")");
					for(ServerClient cli : clients) {
						System.out.println(cli.address.toString() + " " + cli.port);
					}
				}
			}
		};
		receiveThread.start();
	}
	
	private void process(DatagramPacket datagramPacket) {
		String message = new String(datagramPacket.getData());
		if(message.startsWith("/c/")) {
			// guaranteed to be unique ID 128-bit value
			//UUID id = UUID.randomUUID();
			clients.add(new ServerClient(message.substring(3), datagramPacket.getAddress(), datagramPacket.getPort(), UniqueIdentifier.getIdentifier()));
		} else if(message.startsWith("/m/")) {
			sendToAll(message);
		}
		else {
			System.out.println(message);
		}
	}
	
	private void sendToAll(String message) {
		for(ServerClient client : clients) {
			send(message.getBytes(), client.address, client.port);
		}
	}
	
	// Creates a new thread to send byte data to specified inet address and port in packet
	public void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, port);
				try {
					datagramSocket.send(datagramPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

}
