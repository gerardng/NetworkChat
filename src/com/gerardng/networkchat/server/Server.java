package com.gerardng.networkchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

// Thread
public class Server implements Runnable {
	
	private List<ServerObject> clients = new ArrayList<ServerObject>();
	
	private int port;
	private DatagramSocket datagramSocket;
	private Thread runThread, receiveThread;
	private boolean runningFlag = false;
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
		runningFlag = true;
		System.out.println("Server.java:Server started on port " + port);
		receive();
		Scanner scanner = new Scanner(System.in);
		while(runningFlag) {
			String line = scanner.nextLine();
			if(line.equalsIgnoreCase("quit")) {
				quit();
			} else {
				printUsage();
			}
		}
	}
	
	// Method to handle receive thread
	private void receive() {
		receiveThread = new Thread("Receive") {
			public void run() {
				while(runningFlag) {
					// Running threads
					byte[] data = new byte[1024];
					DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
					try {
						datagramSocket.receive(datagramPacket);
						//datagramPacket.getAddress();
						//datagramPacket.getPort();
					} catch (SocketException e) {
						// prevents java from printing a stack trace for normal socket close
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					parsePacket(datagramPacket);
					// information about clients connected
					System.out.println("Server.java: Clients connected (" + clients.size() + ")");
					for (ServerObject client : clients) {
						System.out.println(client.address.toString() + " " + client.port);
					}
				}
			}
		};
		receiveThread.start();
	}
	
	// Parses packet headers
	private void parsePacket(DatagramPacket datagramPacket) {
		String string = new String(datagramPacket.getData());
		if (string.startsWith("/c/")) {
			// UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + "(" + id + ") connected!");
			clients.add(new ServerObject(name, datagramPacket.getAddress(), datagramPacket.getPort(), id));
			String message = "/c/" + id + "/e/";
			send(message.getBytes(), datagramPacket.getAddress(), datagramPacket.getPort());
			// send to all clients
		} else if (string.startsWith("/m/")) {
			String text = string.substring(3);
			text = text.split("/e/")[0];
			System.out.println(string);
			for(ServerObject client : clients) {
				send(string.getBytes(), client.address, client.port);
			}
		} else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			// disconnect(id, status[1 - disconnect, 2 - timeout])
			disconnect(Integer.parseInt(id), 1);
		} else {
			System.out.println(string);
		}
	}
	
	// Creates a new thread to send byte data to specified InetAddress and port in packet
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
	
	// disconnects a client
	private void disconnect(int id, int status) {
		ServerObject c = null;
		for(ServerObject client : clients) {
			if(client.getID() == id) {
				c = client;
				clients.remove(client);
				break;
			}
		}
		String message = "";
		if(status == 1) {
			message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress() + ":" + c.getPort() + " disconnected";
		} else if (status == 2) {
			message = "Client " + c.getName() + " (" + c.getID() + ") @ " + c.getAddress() + ":" + c.getPort() + " timed out";
		}
		System.out.println(message);
	}

	// closes the server
	private void quit() {
		StringBuffer strBuff = new StringBuffer();
		for(ServerObject client : clients) {
			strBuff.append(client.getName() + " (" + client.getID() + ")\n");
			disconnect(client.getID(), 1);
		}
		runningFlag = false;
		System.out.println("Disconnecting clients: " + strBuff);
		datagramSocket.close();
	}
	
	// prints all commands available at the terminal
	private void printUsage() {
		System.out.println("Quit ->  Disconnects all the clients and closes the server");
	}
}
