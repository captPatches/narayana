package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.IOException;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class MapGetFun {

	private DefaultCacheManager manager;
	private Cache<String, Map<String, String>> cache;
	
	private MapGetFun() throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		cache = manager.getCache("rep");
		
	}
	
	private void go() {
		Map<String, String> map = cache.get("ned");
		
		for(String key : map.keySet()) {
			System.out.println(key + " -- " + map.get(key));
		}
	}
	
	public static void main(String[]args) throws IOException {
		new MapGetFun().go();
	}
	
}
