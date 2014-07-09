package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class DistSizeCheckPerfTest extends ObjectStorePerfTester {

	@Override
	public TransactionManager getTransManager() {

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
	
				setMessage("Distributed Cluster Size Checking Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.DistributedSizeCheckStore");
		
			return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
