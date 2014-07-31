package org.jboss.narayana.kvstore.infinispan.nodes;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Runs an infinispan node for testing purposes that has knowledge of a
 * replicated cache store and a distributed cache store
 * 
 * @author patches
 * 
 */
public class NodeForTesting {

	private DefaultCacheManager manager;
	private Cache<String, byte[]> cache;

	private NodeForTesting(String cacheName, String cfgFile) throws Exception {
			
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager(cfgFile);
		if (!manager.getCacheNames().contains(cacheName)) {
			System.out.printf("Invalid / Unknown Cache: <%s> Requested",
					cacheName);
			System.exit(1);
		}
		cache = manager.getCache(cacheName);
	}

	private void go() {		
		System.out.printf("Cluster Size: %d%n", manager.getClusterSize());
		System.out.printf(
				"Node: [%s] using cache: [%s] has successfully started",
				manager.getNodeAddress(), cache.getName());
	}

	public static void main(String[] args) {

		String defaultCacheName = "dis";
		String defaultCfgFile = "generic-test-cfg.xml";

		try {
			switch (args.length) {
			case 1:
				new NodeForTesting(args[0], defaultCfgFile).go();
				break;
			case 2:
				new NodeForTesting(args[0], args[1]).go();
				break;
			default:
				new NodeForTesting(defaultCacheName, defaultCfgFile).go();
				break;
			}
		} catch (Exception e) {
			System.out.println("Node Startup Failed");
			System.exit(1);
		}

	}
	// iptables -I INPUT -s 192.168.0.0/24,172.17.130.0/22 -p udp -j ACCEPT
}
