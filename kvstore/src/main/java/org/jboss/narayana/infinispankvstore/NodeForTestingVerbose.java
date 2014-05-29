package org.jboss.narayana.infinispankvstore;

import java.io.IOException;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.narayana.kvstore.infinispan.learning.SimpleListener;

@SuppressWarnings("unused")
public class NodeForTestingVerbose {

	public static void main(String[] args) {

		// System.setProperty("jgroups.bind_addr", "192.168.1.65");
		System.setProperty("java.net.preferIPv4Stack", "true");

		try {
			EmbeddedCacheManager manager = new DefaultCacheManager(
					"/home/patches/workspace/narayana/kvstore/multi-cache-cfg.xml");
			
			// Object Store Mode
			manager.getCache("distributed-cache").addListener(new NodeListener());
			manager.getCache("replication-cache").addListener(new NodeListener());
			
			// Basic I/O Testing Mode
		//	manager.getCache("distributed-cache").addListener(new SimpleListener());
		//	manager.getCache("replication-cache").addListener(new SimpleListener());
			
			System.out.println("Node Started Successfully");
		} catch (IOException ioe) {
			System.out.println("Node Failed to Start - no Config File");
		} catch (Exception e) {
			System.out.print("Node Failed:\n" + e.getMessage());
		}
	}

}
