package org.jboss.narayana.kvstore.infinispan.nodes;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public abstract class AbstractNode {

	private final DefaultCacheManager manager;
	private final Cache<String, byte[]> cache;
	
	public AbstractNode(String cacheName, String cfgFile) throws Exception {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager(cfgFile);
		// Check the node will be using a Cache that has been defined
		// and that other nodes will know about
		if( ! manager.cacheExists(cacheName)) {
			System.out.printf("Unknown Cache: %s Has Been Requested%n", cacheName);
			System.exit(1);
		}
		cache = manager.getCache(cacheName);
		go();
	}
	
	private void go() {
		System.out.printf("Cluster Size On Startup: %s%n", manager.getClusterSize());
		System.out.printf("Node: %s - Cache: %s Started Successfully%n", manager.getNodeAddress(), cache.getName());
		manager.stop();
	}
	
	protected DefaultCacheManager getManager() {
		return manager;
	}
	
	protected Cache<String, byte[]> getCache() {
		return cache;
	}
}
