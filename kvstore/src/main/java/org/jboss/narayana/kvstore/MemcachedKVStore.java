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
import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * KVStore implementation using a memecached backend.
 *
 * This version uses a shared connection pool 'hashed' based on the slot id.
 * Preliminary perf testing shows this is not a great strategy. See also the ThreadLocal variation.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */

public class MemcachedKVStore implements KVStore {

    // TODO concurrency control, transcoder, i18n logging

    String scopePrefix = "test_";
    String host = "localhost:11211";

    MemcachedClient[] clients = new MemcachedClient[8];


    private static final int SIZE = 1024;

    private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false = unallocated, true = allocated
    private final String[] keys = new String[SIZE];
    private final byte[] dummyData = new byte[10];
    private final int lifetimeSeconds = 60*60*24;

//    private static final byte[][] store = new byte[SIZE][];

    @Override
    public void start() throws Exception {
        for(int i = 0; i < slotAllocation.length; i++) {
            slotAllocation[i] = new AtomicBoolean(false);
            keys[i] = scopePrefix+i;
        }

        for(int i = 0; i < clients.length; i++) {
            clients[i] = new MemcachedClient( new BinaryConnectionFactory(), AddrUtil.getAddresses(host));
        }
    }

    @Override
    public void stop() throws Exception {
        for(int i = 0; i < clients.length; i++) {
            clients[i].shutdown();
            clients[i] = null;
        }
    }

    @Override
    public String getStoreName() {
        return this.getClass().getSimpleName()+" "+this;
    }

    @Override
    public void delete(long id) throws Exception {

        int intId = (int)id;
        OperationFuture<Boolean> future = clients[(int)(id%clients.length)].set(keys[intId], lifetimeSeconds, dummyData);
        boolean completed = future.get();
        if(!completed) {
            throw new RuntimeException("failed to delete "+id);
        }

        slotAllocation[intId].set(false);
    }

    @Override
    public void add(long id, byte[] data) throws Exception {

        int intId = (int)id;
        OperationFuture<Boolean> future = clients[(int)(id%clients.length)].set(keys[intId], lifetimeSeconds, data);
        boolean completed = future.get();
        if(!completed) {
            throw new RuntimeException("failed to write "+id);
        }

    }

    @Override
    public void update(long id, byte[] data) throws Exception {
        add(id, data);
    }

    @Override
    public long allocateId() {

        for(int i = 0; i < SIZE; i++) {
            if(!slotAllocation[i].get()) {
                if(slotAllocation[i].compareAndSet(false, true)) {
                    return (long)i;
                }
            }
        }

        return -1L;
    }

    // TODO maxID
    @Override
    public List<KVStoreEntry> load() throws Exception {

        List<KVStoreEntry> entries = new LinkedList<KVStoreEntry>();

        return entries;
    }
}
