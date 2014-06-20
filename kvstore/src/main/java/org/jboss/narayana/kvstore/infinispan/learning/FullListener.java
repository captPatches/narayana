package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.Map.Entry;
import java.util.Set;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

@Listener
public class FullListener {

	@ViewChanged
	public void viewChanged(ViewChangedEvent event) {
		/*
		Cache<String, String> cache = event.getCacheManager().getCache();
		Set<String> keys = cache.keySet();
		for(String key : keys) {
			System.out.println("key: "+key+" - value: "+cache.get(key));
		}*/
		
		Set<Entry<Object, Object>> entries = event.getCacheManager().getCache("rep").entrySet();
		for(Entry<Object,Object> entry : entries) {
			System.out.println("key: "+entry.getKey()+" - value: "+entry.getValue());
		}
		
		
		System.out.println("MOOOO!!!!");

	}
	
	@CacheEntryCreated
	public void entryCreated(CacheEntryCreatedEvent<String, String> event) {
		String key = event.getKey();
		String val = event.getValue();
		
		System.out.println("Added - key: " + key + " - value: " + val);
	}
	
	@CacheEntryModified
	public void entryUpdated(CacheEntryModifiedEvent event) {
		System.out.println("Modified - key: "+event.getKey()+" - value: "+event.getValue());	
	}	
}
