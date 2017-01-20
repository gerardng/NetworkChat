package com.gerardng.networkchat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtName;
	private JLabel lblName;
	private JTextField txtAddress;
	private JLabel lblIp;
	private JTextField txtPort;
	private JLabel lblPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(67, 50, 165, 28);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		lblName = new JLabel("Name:");
		lblName.setBounds(119, 33, 61, 16);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setColumns(10);
		txtAddress.setBounds(67, 118, 165, 28);
		contentPane.add(txtAddress);
		
		lblIp = new JLabel("IP Address:");
		lblIp.setToolTipText("eg 192.168.0.2");
		lblIp.setBounds(100, 101, 80, 16);
		contentPane.add(lblIp);
		
		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(67, 190, 165, 28);
		contentPane.add(txtPort);
		
		lblPort = new JLabel("Port:");
		lblPort.setToolTipText("eg 8192");
		lblPort.setBounds(119, 173, 61, 16);
		contentPane.add(lblPort);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}
		});
		btnLogin.setBounds(92, 237, 117, 29);
		contentPane.add(btnLogin);
	}
	
	/**
	 * Login
	 */
	private void login(String name, String address, int port) {
		// TODO Auto-generated method stub
		System.out.println(name + " " + address + " " + port);
		new Client(name, address, port);
	}
}
