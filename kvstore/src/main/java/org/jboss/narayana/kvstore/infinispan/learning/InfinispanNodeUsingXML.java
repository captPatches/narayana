package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.IOException;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class InfinispanNodeUsingXML {

	public static void main(String[] args) throws IOException {

		EmbeddedCacheManager manager = new DefaultCacheManager(
				"multi-cache-cfg.xml");
		manager.getCache("cluster-cache");
		System.out.println("Node Up and Running");
	}

}
