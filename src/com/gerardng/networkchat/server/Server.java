package com.gerardng.networkchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

// Thread
public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private int port;
	private DatagramSocket datagramSocket;
	private Thread runThread, manageThread, receiveThread, sendThread;
	private boolean running = false;
	
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
			clients.add(new ServerClient("alias", datagramPacket.getAddress(), datagramPacket.getPort(), 50));
		} else {
			System.out.println(message);
		}

	}

}
