package com.gerardng.networkchat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {
	private static final long serailVersionUID = 1L;

	private JPanel contentPane;
	
	private String name, address;
	private int port;

	public Client(String name, String address, int port) {
		setTitle("GChat Client");
		this.name = name;
		this.address = address;
		this.port = port;
		createWindow();
	}

	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setVisible(true);		
	}

}
