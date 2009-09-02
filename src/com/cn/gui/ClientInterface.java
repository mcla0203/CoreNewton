package com.cn.gui;

public interface ClientInterface {
	/*
	 * Return 0 for success, -1 for failure
	 */
	public int doLOGIN(String serverName, int portNum, String userName);
	public int doLOGOUT();
	public int doPOST(String genre, String title, String clipName, int clipSize, String full, String date);
	public int doSCORE(String genre, String id, int score);
	public int doCLIP(String clipName);
	
	/*
	 * Return file name for success; otherwise return null
	 */
	public String doDOWNLOAD(String peerURL, String clipName, int downloadMode);
}
