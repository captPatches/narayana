package org.jboss.narayana.kvstore.infinispan.learning;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Before;
import org.junit.Test;

public class UpdatingCachedListTest {

	private Cache<Boolean, Set<String>> c;
	
	@Before
	public void setup() throws IOException {
		c = new DefaultCacheManager("generic-test-cfg.xml").getCache("dis");
		c.put(true, new HashSet<String>());
	}
	
	@Test
	public void test() {
		c.get(true).add("Stark");
		
		assertTrue("Set Should Conatain a value!", c.get(true).contains("Stark"));
	}
}
