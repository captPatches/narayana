package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class InfinispanMaps {

	private DefaultCacheManager manager;
	private Cache<String, Map<String, String>> cache;
	
	private InfinispanMaps() throws IOException {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		cache = manager.getCache("rep");
		System.out.println("Size: " + manager.getClusterSize());
	}
	
	private void go() {
		if( cache.containsKey("ned")) {
			cache.get("ned").put("Arya", "Stark");
		}
		else {
			cache.put("ned", new HashMap<String, String>());
			cache.get("ned").put("Jamie", "Lannister");
		}
		
		System.out.println(cache.get("ned").keySet());
	}
	
	public static void main(String[]args) throws IOException {
		new InfinispanMaps().go();
	}
}
