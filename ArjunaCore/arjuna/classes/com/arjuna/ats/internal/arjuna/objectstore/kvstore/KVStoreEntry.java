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

/**
 * TODO
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public class KVStoreEntry {
    private final long id;
    private final byte[] data;

    public KVStoreEntry(long id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}