package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.HashSet;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class SetupCachedSet {

	public static void main(String[]args) throws Exception {
	
	System.setProperty("java.net.preferIPv4Stack", "true");
	Cache<Boolean, Set<String>> c = new DefaultCacheManager("generic-test-cfg.xml").getCache("dis");
	c.put(true, new HashSet<String>());
	
	c.get(true).add(("Stark"));
	
	System.out.println("Setup Complete, run UpdatingCachedListOtherNodeTest before terminating this JVM");	
	System.out.println(c.get(true));
	}
	
}
