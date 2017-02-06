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
					byte[] data = new byte[1024];
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
		String string = new String(datagramPacket.getData());
		if (string.startsWith("/c/")) {
			// UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + "(" + id + ") connected!");
			clients.add(new ServerClient(name, datagramPacket.getAddress(), datagramPacket.getPort(), id));
			String ID = "/c/" + id;
			send(ID, datagramPacket.getAddress(), datagramPacket.getPort());
		} else if (string.startsWith("/m/")) {
			sendToAll(string);
		} else {
			System.out.println(string);
		}
	}
	
	private void sendToAll(String message) {
		if (message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(message);
		}
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
	
	// calls the send method
	private void send(String message, InetAddress address, int port) {
		message += "/e/";
		send(message.getBytes(), address, port);
	}

}
