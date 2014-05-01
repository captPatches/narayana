package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class DistributedInfinispanNode1 {

	public static void main(String[] args) throws Exception  {

		EmbeddedCacheManager manager = new DefaultCacheManager(
				"infinispan-dist-cfg.xml");
		manager.getCache("distributed-cache").addListener(new SimpleListener());

	}

}
