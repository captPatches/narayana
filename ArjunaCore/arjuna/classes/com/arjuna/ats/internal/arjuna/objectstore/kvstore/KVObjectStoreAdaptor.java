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

import java.io.IOException;
import java.io.SyncFailedException;
import java.util.HashSet;
import java.util.Set;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreAPI;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

/**
 * Adaptor class that wraps the IntermediateStore to make it look like an ObjectStore.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public class KVObjectStoreAdaptor implements ObjectStoreAPI
{
    private final IntermediateStore store;

    public KVObjectStoreAdaptor() throws IOException {
        KVStoreEnvironmentBean environmentBean = BeanPopulator.getDefaultInstance(KVStoreEnvironmentBean.class);

        store = new KVStoreMapper(environmentBean);
    }

    // used for standalone bootstrap via StoreManager
    public KVObjectStoreAdaptor(KVStoreEnvironmentBean environmentBean) throws IOException {

        store = new KVStoreMapper(environmentBean);
    }

    @Override
    public void start()
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.start()");
        }

        try {
            store.start();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop()
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.stop()");
        }

        try {
            store.stop();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the object's shadowed state.
     *
     * @param u  The object to work on.
     * @param tn The type of the object to work on.
     * @return the state of the object.
     */
    @Override
    public InputObjectState read_uncommitted(Uid u, String tn) throws ObjectStoreException
    {
        throw new ObjectStoreException(tsLogger.i18NLogger.get_method_not_implemented());
    }

    /**
     * Remove the object's uncommitted state.
     *
     * @param u  The object to work on.
     * @param tn The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean remove_uncommitted(Uid u, String tn) throws ObjectStoreException
    {
    	 if (tsLogger.logger.isTraceEnabled()) {
             tsLogger.logger.trace("LogStore.remove_uncommitted(" + u + ", " + tn + ")");
         }

         return false;
    }


    private String ensureTypenamePrefix(String typeName)
    {
        if(!typeName.startsWith("/")) {
            typeName = "/"+typeName;
        }
        return typeName;
    }

    /**
     * Read the object's committed state.
     *
     * @param u  The object to work on.
     * @param typeName The type of the object to work on.
     * @return the state of the object.
     */
    @Override
    public InputObjectState read_committed(Uid u, String typeName) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.read_committed("+u+", "+typeName+")");
        }

        typeName = ensureTypenamePrefix(typeName);

        return store.read_committed(u, typeName);
    }

    /**
     * Remove the object's committed state.
     *
     * @param u  The object to work on.
     * @param typeName The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean remove_committed(Uid u, String typeName) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.remove_committed("+u+", "+typeName+")");
        }

        typeName = ensureTypenamePrefix(typeName);

        return store.remove_committed(u, typeName);
    }

    /**
     * Hide the object's state in the object store. Used by crash
     * recovery.
     *
     * @param u  The object to work on.
     * @param tn The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean hide_state(Uid u, String tn) throws ObjectStoreException
    {
        throw new ObjectStoreException(tsLogger.i18NLogger.get_method_not_implemented());
    }

    /**
     * Reveal a hidden object's state.
     *
     * @param u  The object to work on.
     * @param tn The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean reveal_state(Uid u, String tn) throws ObjectStoreException
    {
        throw new ObjectStoreException(tsLogger.i18NLogger.get_method_not_implemented());
    }

    /**
     * Commit the object's state in the object store.
     *
     * @param u  The object to work on.
     * @param tn The type of the object to work on.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean commit_state(Uid objUid, String tName) throws ObjectStoreException
    {
        
    	 if (tsLogger.logger.isTraceEnabled()) {
             tsLogger.logger.trace("ShadowingStore.commit_state(" + objUid + ", " + tName + ")");
         }

    	 return true;
    }

    /**
     * @param u  The object to query.
     * @param typeName The type of the object to query.
     * @return the current state of the object's state (e.g., shadowed,
     *         committed ...) [StateStatus]
     */
    @Override
    public int currentState(Uid u, String typeName) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.currentState("+u+", "+typeName+")");
        }

        typeName = ensureTypenamePrefix(typeName);

        if( store.contains(u, typeName)) {
            return StateStatus.OS_COMMITTED;
        } else {
            return StateStatus.OS_UNKNOWN;
        }
    }


    /**
     * Write a copy of the object's uncommitted state.
     *
     * @param u    The object to work on.
     * @param tn   The type of the object to work on.
     * @param buff The state to write.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean write_uncommitted(Uid storeUid, String tName, OutputObjectState state) throws ObjectStoreException
    {
    	if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("FileSystemStore.write_uncommitted(" + storeUid + ", " + tName + ", " + state + ")");
    	}
    	return true;
    }

    /**
     * Write a new copy of the object's committed state.
     *
     * @param u    The object to work on.
     * @param typeName   The type of the object to work on.
     * @param buff The state to write.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean write_committed(Uid u, String typeName, OutputObjectState buff) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.write_committed("+u+", "+typeName+")");
        }

        typeName = ensureTypenamePrefix(typeName);

        return store.write_committed(u, typeName, buff);
    }

    @Override
    public boolean allObjUids(String typeName, InputObjectState foundInstances) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.allObjUids("+typeName+")");
        }

        typeName = ensureTypenamePrefix(typeName);

        return allObjUids(typeName, foundInstances, StateStatus.OS_UNKNOWN);
    }

    /**
     * Obtain all of the Uids for a specified type.
     *
     * @param typeName    The type to scan for.
     * @param foundInstances The object state in which to store the Uids
     * @param matchState    The file type to look for (e.g., committed, shadowed). [StateStatus]
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean allObjUids(String typeName, InputObjectState foundInstances, int matchState) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.allObjUids("+typeName+", "+matchState+")");
        }

        boolean result = true;

        typeName = ensureTypenamePrefix(typeName);

        Uid[] uids = store.getUidsForType(typeName); // may contain trailing null elements

        OutputObjectState buffer = new OutputObjectState();

        try
        {
            if(uids != null && (matchState == StateStatus.OS_UNKNOWN || matchState == StateStatus.OS_COMMITTED))
            {
                for (Uid uid: uids)
                {
                    if(uid != null) {
                        UidHelper.packInto(uid, buffer);
                    }
                }
            }
            UidHelper.packInto(Uid.nullUid(), buffer);
        }
        catch (IOException e)
        {
            throw new ObjectStoreException(e);
        }

        foundInstances.setBuffer(buffer.buffer());

        return result;
    }


    /**
     * Obtain all types of objects stored in the object store.
     *
     * @param foundTypes The state in which to store the types.
     * @return <code>true</code> if no errors occurred, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean allTypes(InputObjectState foundTypes) throws ObjectStoreException
    {
        if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("KVObjectStore.allTypes()");
        }

        boolean result = true;

        String[] knownTypes = store.getKnownTypes(); // may contain trailing null elements
        Set<String> typeSet = new HashSet<String>();

        if (knownTypes == null || knownTypes.length == 0)
            return true;

        OutputObjectState buffer = new OutputObjectState();

        try
        {
            for (String typeName: knownTypes)
            {
                if(typeName == null) {
                    continue;
                }

                if(typeName.startsWith("/")) {
                    typeName = typeName.substring(1);
                }

                if(typeName.contains("/")) {
                    String value = "";
                    String[] parents = typeName.split("/");
                    for(String parent : parents) {
                        if(parent.length() == 0) {
                            continue;
                        }
                        if(value.length() > 0) {
                            value = value+"/";
                        }
                        value = value+parent;
                        if(!typeSet.contains(value)) {
                            typeSet.add(value);
                            buffer.packString(value);
                        }
                    }
                } else {
                    buffer.packString(typeName);
                }
            }
            buffer.packString("");
        }
        catch (IOException e)
        {
            throw new ObjectStoreException(e);
        }

        foundTypes.setBuffer(buffer.buffer());

        return result;
    }

    /**
     * Some object store implementations may be running with automatic
     * sync disabled. Calling this method will ensure that any states are
     * flushed to disk.
     */
    @Override
    public void sync() throws SyncFailedException, ObjectStoreException
    {
        // null-op in this impl.
    }

    /**
     * @return the "name" of the object store. Where in the hierarchy it appears, e.g., /ObjectStore/MyName/...
     */
    @Override
    public String getStoreName()
    {
        return store.getStoreName();
    }

    @Override
    public boolean fullCommitNeeded()
    {
        return false;
    }

    /**
     * Is the current state of the object the same as that provided as the last
     * parameter?
     *
     * @param u  The object to work on.
     * @param tn The type of the object.
     * @param st The expected type of the object. [StateType]
     * @return <code>true</code> if the current state is as expected,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean isType(Uid u, String tn, int st) throws ObjectStoreException
    {
        return false;
    }
}