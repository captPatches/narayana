package org.jboss.narayana.infinispankvstore;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.jboss.narayana.kvstore.XAResourceImpl;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class InfinispanKVStoreTest {
    
	public static void main(String[] args) throws Exception {

        System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana.infinispankvstore.SimpleInfinispanKVStore");

        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

        transactionManager.begin();

        XAResource xaResource1 = new XAResourceImpl(true);
        XAResource xaResource2 = new XAResourceImpl(false);

        transactionManager.getTransaction().enlistResource(xaResource1);
        transactionManager.getTransaction().enlistResource(xaResource2);

        transactionManager.commit();

        StoreManager.shutdown();
    }
}
