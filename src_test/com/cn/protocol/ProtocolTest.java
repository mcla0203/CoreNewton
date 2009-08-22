package com.cn.protocol;

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
	
}
