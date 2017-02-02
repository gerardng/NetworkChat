package com.gerardng.networkchat.server;

public class ServerMain {
	private int port;
	private Server server;
	
	// Creates a new server instance with port
	public ServerMain(int port) {
		this.port = port;
		// Server is a runnable
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Usage: java -jar NetworkChat.jar [port]");
			return;
		}
		new ServerMain(Integer.parseInt(args[0]));

	}
}
