package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;

public class CacheControllerExtention extends CacheContoller {

	@Listener
	class LocalListener {
		
		@CacheEntryCreated
		public void entryMade(CacheEntryCreatedEvent<String, Boolean> event) {
			String key = event.getKey();
			
			System.out.println("THis Key Added to Cache 2: "+key);
		}
	}
	
	
	private Cache<String, Boolean> cache2;

	public CacheControllerExtention() {
		super(false);
		cache2 = manager.getCache("key-cache");
		cache2.addListener(new LocalListener());
		Cache<String, String> tmpCache = manager.getCache("dis");
		setCache(tmpCache);
	}
	
	
	public void cache2Write(BufferedReader in) {		
		try {
			System.out.print("key: ");
			String key = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			cache2.put(key, false);
		//	System.err.println("Acks List: " + acks);
		} catch (NoInputException e) {
			System.err.println("No Value Entered - Nothing Added");
			return;
		} catch (IOException e) {
			System.err.println("Input Exception Caught");
			return;
		}
	}
}
