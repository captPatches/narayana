package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;

@Listener
public class NonVerboseListener {

	@CacheStarted
	public void cacheStarted(CacheStartedEvent event) {
		System.out.println("CacheStarted");
	}
	
	@CacheStopped
	public void cacheStopped(CacheStoppedEvent event) {
		System.out.println("CacheStopped");
	}
	
	@CacheEntryCreated
	public void dataAdded(CacheEntryCreatedEvent<String, String> event) {
		if (event.isPre()) {
			System.out.println("Going to add new Entry: " + event.getKey());
		} else {
			System.out.println("added new entry: " + event.getKey());
		}
	}

	@CacheEntryRemoved
	public void dataRemoved(CacheEntryRemovedEvent<String, String> event) {
		if (event.isPre()) {
			System.out.println("Going to remove Entry: " + event.getKey());
		} else {
			System.out.println("removed entry: " + event.getKey());
		}
	}

}