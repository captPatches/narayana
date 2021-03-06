/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2014
 * @author JBoss, by Red Hat.
 */

package org.jboss.narayana.infinispankvstore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

/**
 * 
 * Prototype Infinispan KVStore
 * @author James Brealey
 *
 */
public class SimpleReplInfinispanKVStore implements KVStore {

	
	String scopePrefix = "test_";
	// Setup an Infinispan cache
	private Cache<String, byte[]> c;

	private static final int SIZE = 1024;

	private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false = unallocated, true = allocated
	private final String[] keys = new String[SIZE];
	
	//TODO
	// Understand andd implement the lifetime
	//private final int lifetimeSeconds = 60*60*24;

	
	@Override
	public void start() throws Exception {

		// Currently creating one cache per transaction
		// Default cache lives on local node in main memory
		EmbeddedCacheManager manager = new DefaultCacheManager(GlobalConfigurationBuilder.defaultClusteredBuilder()
				   .transport().defaultTransport() //.addProperty("configurationFile", "jgroups.xml")
				   .build()
				);
		manager.defineConfiguration("cluster-cache", new ConfigurationBuilder()
			.clustering().cacheMode(CacheMode.REPL_SYNC)
			.build());
		
		c = manager.getCache("cluster-cache");

		// Set all slots to "unused"
		for(int i = 0; i < slotAllocation.length; i++) {
			slotAllocation[i] = new AtomicBoolean(false);
			keys[i] = scopePrefix + i;
		}
	}

	@Override
	public void stop() throws Exception {
		// Stop the cache
		//EmbeddedCacheManager.remove(c);
		c.stop();

	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName()+" "+this;
	}

	@Override
	public void delete(long id) throws Exception {
		
		c.remove(id);
		slotAllocation[(int) id].set(false);

	}

	@Override
	public void add(long id, byte[] data) throws Exception {
		// put data into cache
		
		// TODO Sort out replication count and timeout
		
		c.put(keys[(int)id],  data);

	}

	@Override
	public void update(long id, byte[] data) throws Exception {
		// TODO
		// Error checking to ensure the update occurs
		c.replace(keys[(int)id], data);

	}

	@Override
	public List<KVStoreEntry> load() throws Exception {
		
		//TODO
		// Return a useful list...
		
		return new LinkedList<KVStoreEntry>();
	}

	@Override
	public long allocateId() throws Exception {
		
		// Method stolen from MemCachedKVStore
		for(int i = 0; i < SIZE; i++) {
			if(!slotAllocation[i].get()) {
				if(slotAllocation[i].compareAndSet(false, true)) {
					return (long)i;
				}
			}
		}

		return -1L;
	}

}
