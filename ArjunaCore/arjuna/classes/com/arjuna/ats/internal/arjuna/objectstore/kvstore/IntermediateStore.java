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

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;

/**
 * Essentially a simplified rendition of the RecoveryStore(+TxLog+BaseStore) API
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public interface IntermediateStore {

    // from BaseStore:
    public void start() throws Exception;
    public void stop() throws Exception;
    public String getStoreName ();

    // TxLog
    public boolean remove_committed(Uid uid, String typeName) throws ObjectStoreException;
    public boolean write_committed(Uid uid, String typeName, OutputObjectState txData) throws ObjectStoreException;

    // RecoveryStore
    public InputObjectState read_committed(Uid uid, String typeName) throws ObjectStoreException;

    // unerpins RecoveryStore.allObjUids
    public Uid[] getUidsForType(String typeName);

    // unerpins RecoveryStore.allTypes
    public String[] getKnownTypes();

    // underpins RecoveryStore.currentState
    public boolean contains(Uid uid, String typeName);
}
