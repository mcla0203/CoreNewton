package com.cn.gui;

import javax.swing.JOptionPane;

import com.cn.constants.ClientConstants;

public class LoginResponseGUI {
	String successTitle;
	String successMessage;
	
	String noCharsFailureTitle;
	String noCharsFailureMessage;
	
	String usrNotFoundTitle;
	String usrNotFoundMessage;
	
	public void getSuccessDialog() {
		JOptionPane.showMessageDialog(null,successMessage,successTitle,JOptionPane.PLAIN_MESSAGE);
		return;
	}
	
	public void getNoCharsDialog() {
		JOptionPane.showMessageDialog(null,noCharsFailureMessage,noCharsFailureTitle,JOptionPane.PLAIN_MESSAGE);
		return;
	}
	
	public void getUserNotFoundDialog() {
		JOptionPane.showMessageDialog(null,usrNotFoundMessage,usrNotFoundTitle,JOptionPane.PLAIN_MESSAGE);
		return;
	}

	public LoginResponseGUI() {
		successTitle = "Logged in!";
		successMessage = ClientConstants.LOGIN_SUCCESS;
		
		noCharsFailureTitle = "No characters!";
		noCharsFailureMessage = ClientConstants.NO_CHARS_CREATED;
		
		usrNotFoundTitle = "User not found.";
		usrNotFoundMessage = ClientConstants.USER_NOT_FOUND;
	}
}