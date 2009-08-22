package com.cn.server;

import junit.framework.Assert;
import junit.framework.TestCase;
import com.cn.server.Server;

/**
 * Class to test to ServerStartup class.
 * @author Michael
 */
public class ServerTest extends TestCase {

	public void testMain() {
		Server s = new Server();
		Assert.assertTrue(s.openSocket());
	}
	
}