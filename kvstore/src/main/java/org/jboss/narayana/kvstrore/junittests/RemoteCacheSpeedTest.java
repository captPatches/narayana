package org.jboss.narayana.kvstrore.junittests;

import static org.junit.Assert.*;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.jboss.narayana.kvstore.XAResourceImpl;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class RemoteCacheSpeedTest {

	
	@Test
	public void remoteCacheTest() {
		boolean endReached;

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.HotrodRemoteCacheKVStore");

		TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

		try {
			transactionManager.begin();

			XAResource xaResource1 = new XAResourceImpl(true);
			XAResource xaResource2 = new XAResourceImpl(false);

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
