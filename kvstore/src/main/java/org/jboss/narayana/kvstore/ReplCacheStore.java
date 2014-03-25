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
package org.jboss.narayana.kvstore;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEntry;

import org.jgroups.blocks.ReplCache;
import org.jgroups.jmx.JmxConfigurator;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Preliminary wiring for KVStore backed by JGroups low level API rather than a
 * higher level such as Infinispan or memecahced.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */

public class ReplCacheStore implements KVStore {

    private ReplCache<String,byte[]> cache;
    private static final String BASENAME="replcache";

    private static final int SIZE = 1024;
    private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false = unallocated, true = allocated
    private final String[] keys = new String[SIZE];


    @Override
    public void start() throws Exception {

        for(int i = 0; i < slotAllocation.length; i++) {
            slotAllocation[i] = new AtomicBoolean(false);
            keys[i] = ""+i;
        }

        ////////////////

        long rpc_timeout=1500L, caching_time=30000L;
        boolean migrate_data=true;
        String props="udp.xml";
        String cluster_name="replcache-cluster";

        MBeanServer server= ManagementFactory.getPlatformMBeanServer();

        cache=new ReplCache<String,byte[]>(props, cluster_name);
        cache.setCallTimeout(rpc_timeout);
        cache.setCachingTime(caching_time);
        cache.setMigrateData(migrate_data);
        JmxConfigurator.register(cache, server, BASENAME + ":name=cache");
        JmxConfigurator.register(cache.getL2Cache(), server, BASENAME + ":name=l2-cache");

        cache.start();
    }

    @Override
    public void stop() throws Exception {
        cache.stop();
    }

    @Override
    public String getStoreName() {
        return BASENAME;
    }

    @Override
    public void delete(long id) throws Exception {

        int intId = (int)id;

        cache.remove( keys[intId] );

        slotAllocation[intId].set(false);
    }

    @Override
    public void add(long id, byte[] data) throws Exception {
        int intId = (int)id;

        String key = keys[intId];

        short repl_count = 2;
        long timeout_ms = 10000;

        cache.put(key, data, repl_count, timeout_ms); // k, v, (short)repl, (long)timeout_ms,
    }

    @Override
    public void update(long id, byte[] data) throws Exception {
        add(id, data);
    }

    @Override
    public List<KVStoreEntry> load() throws Exception {
        List<KVStoreEntry> entries = new LinkedList<KVStoreEntry>();
        return entries;
    }

    @Override
    public long allocateId() throws Exception {

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
