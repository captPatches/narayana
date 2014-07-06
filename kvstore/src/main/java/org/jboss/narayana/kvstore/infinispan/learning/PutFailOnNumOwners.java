package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class PutFailOnNumOwners {

	private static DefaultCacheManager manager;
	private static Cache<String, String> c;
	
	public static void main(String[]args) throws Exception {
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		c = manager.getCache("dis");
		
		c.put("ned", "stark");
		System.out.println("Size: "+manager.getClusterSize());
		System.out.println("Entry: "+c.get("ned"));
	}
}
