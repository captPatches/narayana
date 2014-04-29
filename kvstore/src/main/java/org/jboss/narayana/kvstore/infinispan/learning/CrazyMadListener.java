package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;

/**
 * First attempt to be able to use listeners to stop no.
 * 
 * Doesn't Work.  Ugh!
 * @author patches
 *
 */
@Listener
@SuppressWarnings("unused")
public class CrazyMadListener {

//	private final EmbeddedCacheManager manager;

//	public CrazyMadListener(EmbeddedCacheManager manager) {
//		this.manager = manager;
//	}

	@CacheEntryCreated
	public void dataAdded(CacheEntryCreatedEvent<String, String> event) {
		System.out.println(event.getValue());
	}

	@CacheStopped
	public void cacheStopped(CacheStoppedEvent event) {
		System.out.println("Moo!!");
		// manager.stop();
	}
}
