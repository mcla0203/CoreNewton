package com.cn.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginGUI {
	
	public JTextField getUserNameField() {
		return usernameField;
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}


	JTextField usernameField;
	JPasswordField passwordField;
	JPanel panel;  
	JLabel username;
	JLabel password;
	
	public int getLoginDialog() {
		usernameField.requestFocus();
		int i = JOptionPane.showConfirmDialog(null,panel,"Login to Core Newton",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
		return i;
	}

	public LoginGUI() {
		try {
			panel=new JPanel();  

			//Set JPanel layout using GridLayout  
			panel.setLayout(new GridLayout(4,1));  

			username=new JLabel("Username");  
			password=new JLabel("Password");  

			usernameField=new JTextField(20);
			usernameField.setFocusable(true);
			passwordField=new JPasswordField(20);  

			panel.add(username);  
			panel.add(usernameField);  
			panel.add(password);  
			panel.add(passwordField);
			
			usernameField.requestFocus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
