package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanGettingClusterView {

	public static void main(String[]args) throws Exception {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		EmbeddedCacheManager manager = new DefaultCacheManager("multi-cache-cfg.xml");
		manager.addListener(new ClusterViewListener());
		Cache<String, String> c = manager.getCache("replication-cache");
		
		c.put("ned", "Eddard Stark");
		System.out.println(manager.getMembers().size());
	}
}
