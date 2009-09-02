package com.cn.gui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class ClientGUI {

	public ClientGUI() {
		this(null);
	}

	public ClientGUI(ClientInterface client) {
		try {
			JPanel panel=new JPanel();  
			   
			  //Set JPanel layout using GridLayout  
			  panel.setLayout(new GridLayout(4,1));  
			   
			  //Create a label with text (Username)  
			  JLabel username=new JLabel("Username");  
			   
			  //Create a label with text (Password)  
			  JLabel password=new JLabel("Password");  
			   
			  //Create text field that will use to enter username  
			  JTextField textField=new JTextField(20);  
			   
			  //Create password field that will be use to enter password  
			  JPasswordField passwordField=new JPasswordField(20);  
			   
			  //Add label with text (username) into created panel  
			  panel.add(username);  
			   
			  //Add text field into created panel  
			  panel.add(textField);  
			   
			  //Add label with text (password) into created panel  
			  panel.add(password);  
			   
			  //Add password field into created panel  
			  panel.add(passwordField);  
			   
			  //Create a window using JFrame with title ( Two text component in JOptionPane )  
			  JFrame frame=new JFrame("Core Newton Frame");  
			   
			  //Set default close operation for JFrame  
			  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
			   
			  //Set JFrame size  
			  frame.setSize(300,300);  
			   
			  //Set JFrame locate at center  
			  frame.setLocationRelativeTo(null);  
			   
			  //Make JFrame invisible  
			  frame.setVisible(true);  
			   
			  //Show JOptionPane that will ask user for username and password  
			  int a=JOptionPane.showConfirmDialog(frame,panel,"Login to Core Newton",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);  
			   
			  //Operation that will do when user click 'OK'  
			  if(a==JOptionPane.OK_OPTION)  
			  {  
			   if(textField.getText().equals("mcla0203")&&new String(passwordField.getPassword()).equals("123"))  
			   {
			    JOptionPane.showMessageDialog(frame,"You are James Bond","Successfully logged in!",JOptionPane.PLAIN_MESSAGE);
			   }  
			   else  
			   {
			    JOptionPane.showMessageDialog(frame,"You are not James Bond","Login failed!",JOptionPane.ERROR_MESSAGE);  
			   }  
			  }  
			ClientPanel.client = client;

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}

@SuppressWarnings("serial")
class ClientPanel extends JPanel {

	public static ClientInterface client = null;

	public ClientPanel() {
		JTabbedPane jtbExample = new JTabbedPane();
//		
//		JPanel jplInnerPanel0 = new ConfPanel();
//		jtbExample.addTab("Configure", jplInnerPanel0);
//		
//		JPanel jplInnerPanel1 = new PostPanel(Const.CLIENT_CLIP_UPLOAD_PATH+Const.CLIENT_CLIP_UPLOAD_LIST);
//		jtbExample.addTab("Posting", jplInnerPanel1);
//		//jtbExample.setSelectedIndex(0);
//		
//		JPanel jplInnerPanel2 = new ListPanel();
//		jtbExample.addTab("Retrieval", jplInnerPanel2);
		
		// Add the tabbed pane to this panel.
		setLayout(new GridLayout(1, 1));
		add(jtbExample);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ClientGUI gui = new ClientGUI();
	}
}

