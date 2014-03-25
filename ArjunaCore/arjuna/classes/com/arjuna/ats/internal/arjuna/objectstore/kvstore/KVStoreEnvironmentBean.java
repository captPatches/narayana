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

import com.arjuna.ats.internal.arjuna.common.ClassloadingUtility;

/**
 * TODO
 * TODO mbean
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2014-03
 */
public class KVStoreEnvironmentBean {

    private volatile String storeImplementationClassName = "com.arjuna.ats.internal.arjuna.objectstore.kvstore.MapStore";
    private volatile KVStore storeImplementation = null;


    /**
     * Returns the class name of the KVStore implementation to use.
     *
     * Default: "com.arjuna.ats.internal.arjuna.objectstore.kvstore.MapStore"
     *
     * @return the name of a class implementing KVStore.
     */
    public String getStoreImplementationClassName()
    {
        return storeImplementationClassName;
    }

    /**
     * Sets the class name of the KVStore implementation to use.
     *
     * @param storeImplementationClassName the name of a class implementing KVStore.
     */
    public void setStoreImplementationClassName(String storeImplementationClassName)
    {
        synchronized(this) {
            if(storeImplementationClassName == null)
            {
                this.storeImplementation = null;
            }
            else if(!storeImplementationClassName.equals(this.storeImplementationClassName))
            {
                this.storeImplementation = null;
            }
            this.storeImplementationClassName = storeImplementationClassName;
        }
    }

    /**
     * Returns an instance of a class implementing com.arjuna.ats.arjuna.utils.KVStore.
     *
     * If there is no pre-instantiated instance set and classloading or instantiation fails,
     * this method will log an appropriate warning and return null, not throw an exception.
     *
     * @return a KVStore implementation instance, or null.
     */
    public KVStore getStoreImplementation()
    {
        if(storeImplementation == null && storeImplementationClassName != null)
        {
            synchronized(this) {
                if(storeImplementation == null && storeImplementationClassName != null) {
                    storeImplementation = ClassloadingUtility.loadAndInstantiateClass(KVStore.class, storeImplementationClassName, null);
                }
            }
        }

        return storeImplementation;
    }

    /**
     * Sets the instance of com.arjuna.ats.arjuna.utils.KVStore
     *
     * @param instance an Object that implements KVStore, or null.
     */
    public void setstoreImplementation(KVStore instance)
    {
        synchronized(this)
        {
            KVStore oldInstance = this.storeImplementation;
            storeImplementation = instance;

            if(instance == null)
            {
                this.storeImplementationClassName = null;
            }
            else if(instance != oldInstance)
            {
                String name = ClassloadingUtility.getNameForClass(instance);
                this.storeImplementationClassName = name;
            }
        }
    }

}
