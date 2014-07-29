package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.IOException;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

public class MapsNode {

	@Listener
	class TestListener {

		@CacheEntryCreated
		public void cacheEntryCreated(CacheEntryCreatedEvent e) {
			if (e.isPre()) {
				System.out.println("Created: " + e.getKey());
			}
		}

		@CacheEntryModified
		public void cacheEntryUpdated(CacheEntryModifiedEvent e) {
			if (e.isPre()) {
				System.out.println("Modified: " + e.getKey());
			}
		}

		@ViewChanged
		public void viewChanged(ViewChangedEvent e) {
			System.out.println(e.getNewMembers().size());
		}
	}

	private DefaultCacheManager manager;
	private Cache<String, Map<String, String>> cache;

	private MapsNode() throws IOException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		manager = new DefaultCacheManager("generic-test-cfg.xml");
		cache = manager.getCache("rep");
		cache.addListener(new TestListener());

		manager.addListener(new TestListener());
	}

	private void go() {
		System.out.println("View: " + manager.getClusterSize());
	}

	public static void main(String[] args) throws IOException {
		new MapsNode().go();
	}

}
