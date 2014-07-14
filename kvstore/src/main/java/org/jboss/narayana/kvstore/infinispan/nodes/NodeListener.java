package org.jboss.narayana.kvstore.infinispan.nodes;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;

@Listener
public class NodeListener {
	
	private int i=0;
	
	@CacheEntryCreated
	public void dataAdded
	(CacheEntryCreatedEvent<String, byte[]> event) {
		if(event.isPre()) {
			System.out.println("Going to add new Entry: " + event.getKey());
		}
		else {
			System.out.println("added new entry: " + event.getKey());
		}
		System.out.println(i++);
	}
	
	@CacheEntryRemoved
	public void dataRemoved(CacheEntryRemovedEvent<String, byte[]> event) {
		if(event.isPre()) {
			System.out.println("Going to remove Entry: " + event.getKey());
		}
		else {
			System.out.println("removed entry: " + event.getKey());
		}
	}
	
	@CacheEntryVisited
	public void dataVisited(CacheEntryVisitedEvent<String, byte[]> event) {
		if(event.isPre()) {
			System.out.println(event.getKey() + ": is about to be visited");
		}
		else {
			System.out.println(event.getKey() + ": has had a visitor");
		}
	}

}
