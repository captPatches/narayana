package org.jboss.narayana.kvstore.infinispan.learning;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Before;
import org.junit.Test;

/**
 * This test must be started after SetupCachedSet Class has been started in
 * seperate JVM.
 * 
 * @author patches
 * 
 */
public class UpdatingCachedListOtherNodeTest {

	private Cache<Boolean, Set<String>> c;

	@Before
	public void setup() throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		c = new DefaultCacheManager("generic-test-cfg.xml").getCache("dis");
	}

	@Test
	public void test() {
		assertTrue("Cache should contain an entry", c.containsKey(true));
		assertTrue("Cache should contain String: \"Stark\"", c.get(true)
				.contains("Stark"));
	}

}
