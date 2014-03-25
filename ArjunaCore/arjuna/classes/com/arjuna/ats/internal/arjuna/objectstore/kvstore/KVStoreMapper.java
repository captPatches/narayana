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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputBuffer;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputBuffer;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

/**
 * Sits between the IntermediateStore API, and the KVStore API, providing local cache and id mapping services.
 * This is a bean suitable for hooking into the app server lifecycle.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public class KVStoreMapper implements IntermediateStore
{
    private final KVStore kvStore;

    private final ConcurrentMap<String,Map<Uid, KVStoreEntry>> content = new ConcurrentHashMap<String, Map<Uid, KVStoreEntry>>();

    public KVStoreMapper(KVStoreEnvironmentBean environmentBean) {
        kvStore = environmentBean.getStoreImplementation();
    }

    public void stop() throws Exception {
        kvStore.stop();
    }

    public void start() throws Exception {

        kvStore.start();

        List<KVStoreEntry> kvStoreEntryList = kvStore.load();

        if(kvStoreEntryList == null || kvStoreEntryList.size() == 0) {
            return;
        }

        for(KVStoreEntry kvStoreEntry : kvStoreEntryList) {
            InputBuffer inputBuffer = new InputBuffer(kvStoreEntry.getData());
            Uid uid = UidHelper.unpackFrom(inputBuffer);
            String typeName = inputBuffer.unpackString();
            getContentForType(typeName).put(uid, kvStoreEntry);
            // don't unpack the rest yet, we may never need it. read_committed does it on demand.
        }

// TODO        maxID = kvstoreLoadInformation.getMaxID();
    }

    /**
     * Remove the object's committed state.
     *
     * @param uid  The object to work on.
     * @param typeName The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     * @throws ObjectStoreException if things go wrong.
     */
    public boolean remove_committed(Uid uid, String typeName) throws ObjectStoreException
    {
        try {
            long id = getId(uid, typeName); // look up the id *before* doing the remove from state, or it won't be there any more.
            getContentForType(typeName).remove(uid);
            kvStore.delete(id);
        } catch(Exception e) {
            throw new ObjectStoreException(e);
        }

        return true;
    }

    /**
     * Write a new copy of the object's committed state.
     *
     * @param uid    The object to work on.
     * @param typeName   The type of the object to work on.
     * @param txData The state to write.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     * @throws ObjectStoreException if things go wrong.
     */
    public boolean write_committed(Uid uid, String typeName, OutputObjectState txData) throws ObjectStoreException
    {
        try {
            OutputBuffer outputBuffer = new OutputBuffer();
            UidHelper.packInto(uid, outputBuffer);
            outputBuffer.packString(typeName);
            outputBuffer.packBytes(txData.buffer());
            long id = getId(uid, typeName);
            byte[] data = outputBuffer.buffer();

            // yup, there is a race condition here.
            if(getContentForType(typeName).containsKey(uid)) {
                kvStore.update(id, data);
            } else {
                kvStore.add(id, data);
            }

            KVStoreEntry kvStoreEntry = new KVStoreEntry(id, data);
            getContentForType(typeName).put(uid, kvStoreEntry);
        } catch(Exception e) {
            throw new ObjectStoreException(e);
        }

        return true;
    }

    /**
     * Read the object's committed state.
     *
     * @param uid  The object to work on.
     * @param typeName The type of the object to work on.
     * @return the state of the object.
     * @throws ObjectStoreException if things go wrong.
     */
    public InputObjectState read_committed(Uid uid, String typeName) throws ObjectStoreException
    {
        KVStoreEntry kvStoreEntry = getContentForType(typeName).get(uid);
        if(kvStoreEntry == null) {
            return null;
        }

        // this repeated unpacking is a little inefficient - subclass RecordInfo to hold unpacked form too?
        // not too much of an issue as log reads are done for recovery only.
        try {
            InputBuffer inputBuffer = new InputBuffer(kvStoreEntry.getData());
            Uid unpackedUid = UidHelper.unpackFrom(inputBuffer);
            String unpackedTypeName = inputBuffer.unpackString();
            InputObjectState inputObjectState = new InputObjectState(uid, typeName, inputBuffer.unpackBytes());
            return inputObjectState;
        } catch(Exception e) {
            throw new ObjectStoreException(e);
        }
    }

    public boolean contains(Uid uid, String typeName) {
        KVStoreEntry kvStoreEntry = getContentForType(typeName).get(uid);
        return kvStoreEntry != null;
    }

    /**
     * @return the "name" of the object store. Where in the hierarchy it appears, e.g., /ObjectStore/MyName/...
     */
    public String getStoreName()
    {
        return kvStore.getStoreName();
    }

    public String[] getKnownTypes() {
        return content.keySet().toArray(new String[content.size()]);
    }

    public Uid[] getUidsForType(String typeName) {
        Set<Uid> keySet = getContentForType(typeName).keySet();
        return keySet.toArray(new Uid[keySet.size()]);
    }

    /////////////////////////////////

    private Map<Uid, KVStoreEntry> getContentForType(String typeName) {
        Map<Uid, KVStoreEntry> result = content.get(typeName);
        if(result == null) {
            content.putIfAbsent(typeName, new ConcurrentHashMap<Uid, KVStoreEntry>());
            result = content.get(typeName);
        }
        return result;
    }

    private long getId(Uid uid, String typeName) throws Exception {
        KVStoreEntry kvStoreEntry = getContentForType(typeName).get(uid);
        if(kvStoreEntry != null) {
            return kvStoreEntry.getId();
        } else {
            return kvStore.allocateId();
        }
    }
}
