package com.cn.client.senders;

import com.cn.constants.ServerConstants;

public class Sender {

	public World World = null;
	public Auth Auth = null; 
	public Chat Chat = null;
	
	public Sender() {
		World = new World(ServerConstants.HOSTNAME, ServerConstants.PORT);
		Auth = new Auth(ServerConstants.HOSTNAME, ServerConstants.AUTH_PORT);
		Chat = new Chat(ServerConstants.HOSTNAME, ServerConstants.CHAT_PORT);
	}
	
	public void processInput(String[] input, boolean isLoggedIn, boolean isPlaying, String charName, String userName) {
		World.processInput(input, isLoggedIn, isPlaying);
		Auth.processInput(input, userName, isPlaying);
		Chat.processInput(input, charName);
	}
	
}
