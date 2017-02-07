package com.gerardng.networkchat.server;

import java.net.InetAddress;

// Stores information about the clients connected to the Server
public class ServerObject {
	public String name;
	public InetAddress address;
	public int port;
	// Differentiate clients with same address and port
	private final int ID;
	
	public ServerObject(String name, InetAddress address, int port, final int ID) {
		this.name = name;
		this.address =address;
		this.port = port;
		this.ID = ID;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getAddress() {
		return this.address.toString();
	}
}
