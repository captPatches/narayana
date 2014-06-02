package org.jboss.narayana.kvstore.infinispan.nodes;

import java.io.IOException;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class NodeForTestingVerbose {
	public static void main(String[] args) {

		System.setProperty("java.net.preferIPv4Stack", "true");

		try {
			EmbeddedCacheManager manager = new DefaultCacheManager(
					"multi-cache-cfg.xml");
			
			manager.getCache("distributed-cache").addListener(new NodeListener());
			manager.getCache("replication-cache").addListener(new NodeListener());
			
			System.out.println("Node Started Successfully");
		} catch (IOException ioe) {
			System.out.println("Node Failed to Start - no Config File");
		} catch (Exception e) {
			System.out.print("Node Failed:\n" + e.getMessage());
		}
	}
}
