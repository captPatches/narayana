package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class ReplicatedStorePerfTest extends MillTester {

	@Override
	protected TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		if(runOnMill()) {
			setMessage("(Mill) Replicated Cache Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillReplCacheStore");
		} else {
			setMessage("Replicated Cache Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.ReplicatedStore");
		}
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
