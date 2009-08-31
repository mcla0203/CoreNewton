//package com.cn.client;
//
//import com.cn.constants.Constants;
//import com.cn.constants.ProtocolConstants;
//import com.cn.protocol.Protocol;
//import junit.framework.TestCase;
//
//public class ClientTestIntegration extends TestCase {
//
//	public Client client = null;
//	
//	public void init() {
//		client = new Client();
//		client.doLOGIN("login authtest 1234".split(" "));
//	}
//	
//	public void cleanup() {
//		client.disconnectFromServer();
//		client = null;
//	}
//	
//	/**
//	 * Tests the connection to the server.
//	 */
//	public void testConnectToServer() {
//		init();
//		assertEquals(0, init());
//		cleanup();
//	}
//	
//	
//	/**
//	 * Tests that the client can get monsters.
//	 */
//	public void testGetMonsters() {
//		init();
//		String request = Protocol.getMonstersRequest();
//		String response = client.sendToServerAndGetResponse(request);
//		assertTrue(response != null);
//		
//		String[] monsterIds = Protocol.getRequestArgsSimple(response);
//		for(String id : monsterIds) {
//			assertTrue(Double.valueOf(id) != null);
//		}
//		cleanup();
//	}
//	
//	/**
//	 * Tests that the client can attack the monsters.
//	 */
//	public void testAttack() {
//		init();
//		String request1 = Protocol.getMonstersRequest();
//		String response1 = client.sendToServerAndGetResponse(request1);
//		assertTrue(response1 != null);
//		
//		String[] monsterIds = Protocol.getRequestArgsSimple(response1);
//		Double singleId = Double.valueOf(monsterIds[0]);
//		
//		String request2 = Protocol.attackRequest(50, singleId, "authtestname1");
//		String response2 = client.sendToServerAndGetResponse(request2);
//		assertTrue(response2.equals(ProtocolConstants.SUCCESS + Protocol.getRequestArgsSimple(response2)[1]));
//		cleanup();
//	}
//	
//	/**
//	 * Tests that the client can kill monsters.
//	 */
//	public void testAttackUntilDead() {
//		init();
//		String request1 = Protocol.getMonstersRequest();
//		String response1 = client.sendToServerAndGetResponse(request1);
//		assertTrue(response1 != null);
//		
//		String[] monsterIds = Protocol.getRequestArgsSimple(response1);
//		Double singleId = Double.valueOf(monsterIds[1]);
//		
//		String request2 = Protocol.attackRequest(50, singleId, "");
//		client.sendToServerAndGetResponse(request2);
//		client.sendToServerAndGetResponse(request2);
//		String response2 = client.sendToServerAndGetResponse(request2);
//		
//		assertTrue(Constants.DEATH.equals(Protocol.getRequestCmdSimple(response2)));
//		cleanup();
//	}
//}
