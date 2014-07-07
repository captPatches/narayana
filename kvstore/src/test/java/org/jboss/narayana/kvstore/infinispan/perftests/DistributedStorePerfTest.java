package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class DistributedStorePerfTest extends MillTester {

	@Override
	protected TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		if(runOnMill()) {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillDistCacheStore");
			
			setMessage("(Mill) Distributed Cache Store");
		} else {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.DistributedStore");
			setMessage("Distibuted Cache Store");
		}
		
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

}