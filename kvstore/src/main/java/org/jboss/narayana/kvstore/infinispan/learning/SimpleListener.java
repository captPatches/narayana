package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

@Listener
public class SimpleListener {

	@CacheEntryCreated
	public void dataAdded
	(CacheEntryCreatedEvent<String, String> event) {
		if(event.isPre()) {
			System.out.println("Going to add new Entry: " + event.getKey());
		}
		else {
			System.out.println("added new entry: " + event.getKey());
		}
	}
	
	@CacheEntryRemoved
	public void dataRemoved(CacheEntryRemovedEvent<String, String> event) {
		if(event.isPre()) {
			System.out.println("Going to remove Entry: " + event.getKey());
		}
		else {
			System.out.println("removed entry: " + event.getKey());
		}
	}

}
