package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class DistSizeCheckPerfTest extends MillTester {

	@Override
	public TransactionManager getTransManager() {

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
	
		if(runOnMill()) {
			setMessage("(Mill) Distributed Cluster Size Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillDistSizeCheckStore"
					);
			
		}
		else {
			setMessage("Distributed Cluster Size Checking Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.DistributedSizeCheckStore");
		}
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
