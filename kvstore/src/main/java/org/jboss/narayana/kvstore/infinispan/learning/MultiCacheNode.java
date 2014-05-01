package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class MultiCacheNode {

	public static void main(String[] args) throws Exception {

		SimpleListener listener = new SimpleListener();

		EmbeddedCacheManager manager = new DefaultCacheManager(
				"multi-cache-cfg.xml");
		manager.getCache("distributed-cache").addListener(listener);
		manager.getCache("replication-cache").addListener(listener);

	}

}
