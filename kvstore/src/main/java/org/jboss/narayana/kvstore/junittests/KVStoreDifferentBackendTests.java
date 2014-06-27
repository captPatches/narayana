package org.jboss.narayana.kvstore.junittests;

import static org.junit.Assert.*;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.jboss.narayana.kvstore.XAResourceImpl;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class KVStoreDifferentBackendTests {
	
	@Test
	public void infinispanNoReplTest() {
		
		boolean endReached;
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.infinispankvstore.NoReplInfinispanKVStore");

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        try {
        	transactionManager.begin();

        	XAResource xaResource1 = new XAResourceImpl();
        	XAResource xaResource2 = new XAResourceImpl();

        	transactionManager.getTransaction().enlistResource(xaResource1);
        	transactionManager.getTransaction().enlistResource(xaResource2);

        	transactionManager.commit();
        	StoreManager.shutdown();

        	endReached = true;

        } catch(Exception e) {
        	endReached = false;
        }
        
        //assertEquals(endReached, true);   
    }
	
	
	@Test
	public void infinispanTest() {
		
		
		boolean endReached;
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.infinispankvstore.SimpleReplInfinispanKVStore");

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        try {
        	transactionManager.begin();

        	XAResource xaResource1 = new XAResourceImpl();
        	XAResource xaResource2 = new XAResourceImpl();

        	transactionManager.getTransaction().enlistResource(xaResource1);
        	transactionManager.getTransaction().enlistResource(xaResource2);

        	transactionManager.commit();
        	StoreManager.shutdown();

        	endReached = true;

        } catch(Exception e) {
        	endReached = false;
        }
        
        //assertEquals(endReached, true);   
    }
	
	@Test
	public void memcachedTest() {
		
		
		boolean endReached;
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.kvstore.MemcachedKVStore");

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        try {
        	transactionManager.begin();

        	XAResource xaResource1 = new XAResourceImpl();
        	XAResource xaResource2 = new XAResourceImpl();

        	transactionManager.getTransaction().enlistResource(xaResource1);
        	transactionManager.getTransaction().enlistResource(xaResource2);

        	transactionManager.commit();
        	StoreManager.shutdown();

        	endReached = true;

        } catch(Exception e) {
        	endReached = false;
        }
        
        assertEquals(endReached, true);   
    }
	
	@Test
	public void repCacheTest() {
		
		
		boolean endReached;
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.kvstore.ReplCacheStore");

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        try {
        	transactionManager.begin();

        	XAResource xaResource1 = new XAResourceImpl();
        	XAResource xaResource2 = new XAResourceImpl();

        	transactionManager.getTransaction().enlistResource(xaResource1);
        	transactionManager.getTransaction().enlistResource(xaResource2);

        	transactionManager.commit();
        	StoreManager.shutdown();

        	endReached = true;

        } catch(Exception e) {
        	endReached = false;
        }
        
        assertEquals(endReached, true);   
    }

}
