package com.gerardng.networkchat.server;

import java.net.InetAddress;

// Stores information about the clients connected to the Server
public class ServerClient {
	public String name;
	public InetAddress address;
	public int port;
	// Differentiate clients with same address and port
	private final int ID;
	// Server will send a timed package to client and await reply to ensure the client is still there
	public int attempt = 0;
	
	public ServerClient(String name, InetAddress address, int port, final int ID) {
		this.name = name;
		this.address =address;
		this.port = port;
		this.ID = ID;
	}
	
	public int getID() {
		return this.ID;
	}
}
