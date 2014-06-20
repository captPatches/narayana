package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class LearningIn {

	private static Cache<String, String> cache;
	private static EmbeddedCacheManager manager;
	
	private static void updateCache(Cache<String, String> cache) {
		
		String key = null;
		String value = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		while(true == true) {
			try {
				System.out.print("Key: ");
				key = in.readLine();
				System.out.print("Value: ");
				value = in.readLine();
			} catch (IOException e) {
				System.err.println(e);
			}
			if(key != null && value != null) {
				cache.put(key, value);
			} else {
				System.err.print("Values Null");
			}
		}
	}
	
	public static void main(String[] args) {

		System.setProperty("java.net.preferIPv4Stack", "true");
		
		try {
		manager = new DefaultCacheManager("generic-test-cfg.xml");
	//	manager.addListener(new FullListener());
		cache = manager.getCache("rep");
		cache.addListener(new FullListener());
		} catch (Exception e) {
			System.err.println(e);
		}
		
		updateCache(cache);
	}
	
}
