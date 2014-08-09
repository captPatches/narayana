package org.jboss.narayana.kvstore.infinispan.nodes;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class NodeForDirectStoreTesting {

	private final DefaultCacheManager manager;
	private final Cache<String, byte[]> cache;
	
	private NodeForDirectStoreTesting(String cacheName, String cfgFile) throws IOException {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager(cfgFile);
		cache = manager.getCache(cacheName);
	}

	// output some generic info about the cache being used
	private void go() {
		System.out.println("Cluster Size: " + manager.getClusterSize());
		System.out.printf(
				"Node: %s - using cache: %s - has started successfuly\n",
				manager.getNodeAddress(), cache.getName());
	}

	public static void main(String[] args) {

		String cacheName;
		String cfgFile;

		
		switch (args.length) {
		case 1:
			cacheName = args[0];
			cfgFile = "generic-test-cfg.xml";
			break;
		case 2:
			cacheName =args[0];
			cfgFile = args[1];
			break;	
		default:
			cacheName = "dis";
			cfgFile = "generic-test-cfg.xml";
			break;
		}
		
		try {
			new NodeForDirectStoreTesting(cacheName, cfgFile).go();
		} catch (Exception e) {
			System.out.printf("Node Failed to start: %s\n", e.getMessage());
			System.exit(1);
		}
		
	}
	// iptables -I INPUT -s 192.168.0.0/24,172.17.130.0/22 -p udp -j ACCEPT
}