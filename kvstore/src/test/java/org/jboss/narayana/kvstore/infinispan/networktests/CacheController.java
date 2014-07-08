package org.jboss.narayana.kvstore.infinispan.networktests;

import java.io.BufferedReader;
import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.narayana.kvstore.infinispan.learning.FullListener;
import org.jboss.narayana.kvstore.infinispan.learning.NoInputException;

public class CacheController {
	private Cache<String, String> cache;
	private DefaultCacheManager manager;
	
	public CacheController() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			manager = new DefaultCacheManager("generic-test-cfg.xml");
			cache = manager.getCache("dis");
			cache.addListener(new FullListener());
		} catch (Exception e) {
			throw new RuntimeException("Cache Creation Failed");
		}
	}
	
	public void write(BufferedReader in) {
		try {
			System.out.print("key: ");
			String key = in.readLine();
			System.out.print("value: ");
			String value = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			if(value.equals("") || value.equals(null)) throw new NoInputException();
			cache.getAdvancedCache(). put(key, value);
		} catch (NoInputException e) {
			System.err.println("No Value Entered - Nothing Added");
			return;
		} catch (IOException e) {
			System.err.println("Input Exception Caught");
			return;
		}
	}
	
	public void viewAll() {
		for(String key : cache.keySet()) {
			System.out.println("key: "+key+" - value: "+cache.get(key));
		}
	}
	
	public void delete(BufferedReader in) {
		try {
			System.out.print("key: ");
			String key = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			cache.remove(key);
		} catch (Exception e) {
			System.err.println("Remove Failed");
		}
	}
	
	public void size() {
		System.out.println("Cluster Size: "+manager.getClusterSize());
	}
	
	public void numOwners() {		
		int numOwners = cache.getAdvancedCache().getCacheConfiguration().clustering().hash().numOwners();
		System.out.println("numOwners: " + numOwners);
	}
}
