package org.jboss.narayana.kvstore.infinispan.nodes;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

/**
 * This Node is useful for testing that the node is recieving the keys as
 * expected
 * 
 * @author patches
 * 
 */
public class NodeForTestingVerbose {

	/*
	 * Unfortunately it seems when a class extends another class that starts a cache,
	 * it fails to keep the cache running indefinitely.
	 */
	
	@Listener
	private class NodeListener {

		@CacheEntryCreated
		public void entryCreated(CacheEntryCreatedEvent<String, byte[]> event) {
			if (!event.isPre()) {
				System.out.println(event.getKey());
			}
		}

		@ViewChanged
		public void viewChanged(ViewChangedEvent event) {
			System.out.printf("New Cluster Size: %s%n", event.getNewMembers()
					.size());
		}
	}
	
	private final DefaultCacheManager manager;
	private final Cache<String, byte[]> cache;

	private NodeForTestingVerbose(String cacheName, String cfgFile)
			throws Exception {
		
		manager = new DefaultCacheManager(cfgFile);
		if( ! manager.getCacheNames().contains(cacheName)) {
			System.out.printf("Invalid Cache \"%s\" requested", cacheName);
		}
		cache = manager.getCache(cacheName);
		
		// Add Listener
		NodeListener listener = new NodeListener();
		cache.addListener(listener);
		manager.addListener(listener);
		
	}

	public static void main(String[] args) {
		String defaultCacheName = "dis";
		String defaultCfgFile = "generic-test-cfg.xml";		
		
		try {
			switch (args.length) {
			case 1:
				new NodeForTestingVerbose(args[0], defaultCfgFile);
				break;
			case 2:
				new NodeForTestingVerbose(args[0], args[1]);
				break;
			default:
				new NodeForTestingVerbose(defaultCacheName, defaultCfgFile);
				break;
			}
		} catch (Exception e) {
			System.out.println("Node Startup Failed");
			System.exit(1);
		}
	}
}
