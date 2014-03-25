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
package com.arjuna.ats.internal.arjuna.objectstore.kvstore;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A KVStore impl that uses in-process memory storage, for test purposes.
 * @see com.arjuna.ats.internal.arjuna.objectstore.VolatileStore
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public class MapStore implements KVStore {

    private static final int SIZE = 1024;

    private final AtomicBoolean[] slotAllocation = new AtomicBoolean[SIZE]; // false = unallocated, true = allocated

    private static final byte[][] store = new byte[SIZE][];

    @Override
    public void start() throws Exception {
        for(int i = 0; i < slotAllocation.length; i++) {
            slotAllocation[i] = new AtomicBoolean(false);
        }
    }

    @Override
    public void stop() throws Exception {
        // null-op
    }

    @Override
    public String getStoreName() {
        return this.getClass().getSimpleName()+" "+this;
    }

    @Override
    public void delete(long id) throws Exception {
        store[(int)id] = null;
        slotAllocation[(int)id].set(false);
    }

    @Override
    public void add(long id, byte[] data) throws Exception {
        store[(int)id] = data;
    }

    @Override
    public void update(long id, byte[] data) throws Exception {
        store[(int)id] = data;
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

//        for(ConcurrentHashMap.Entry<Long,byte[]> entry : store.entrySet()) {
//            entries.add(new KVStoreEntry(entry.getKey(), entry.getValue()));
//        }

        return entries;
    }
}
