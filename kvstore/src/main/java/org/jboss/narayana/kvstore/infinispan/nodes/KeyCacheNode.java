package org.jboss.narayana.kvstore.infinispan.nodes;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class KeyCacheNode {

	private final DefaultCacheManager manager; 
	private final Cache<String,Boolean> keyCache;
	
	private KeyCacheNode() throws Exception {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		
		keyCache = manager.getCache("key-cache");
		keyCache.addListener(new NodeListener());
	}
	
	private int view() {
		return manager.getClusterSize();
	}
	
	public static void main(String[]args) {
		try {
			System.out.println("cluster size at start: "+new KeyCacheNode().view());
		} catch (Exception e) {
			System.err.println("this hasne't worked");
		}
	}
	
	
}
