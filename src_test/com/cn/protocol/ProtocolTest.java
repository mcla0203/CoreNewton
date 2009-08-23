package com.cn.protocol;

import com.cn.constants.ProtocolConstants;

import junit.framework.TestCase;

public class ProtocolTest extends TestCase {

	public void testParseSimple() {
		String s = "<Hello><World!>";
		String x = "  <Bigger string: >" + s;	
		String[] sArray = Protocol.getRequestArgsSimple(s);
		String[] xArray = Protocol.getRequestArgsSimple(x);
		
		assertEquals(sArray.length, 2);
		assertEquals(xArray.length, 3);
		assertEquals(sArray[0], "Hello");
		assertEquals(sArray[1], "World!");
		assertEquals(xArray[0], "Bigger string: ");
		assertEquals(xArray[1], "Hello");
		assertEquals(xArray[2], "World!");
	}
	
	public void testCreateSuccessResponse() {
		String s1 = ProtocolConstants.DEATH + "<123>";
		String s2 = Protocol.createCharacterDiedResponse(123);
		assertEquals(s1, s2);
	}
	
}
