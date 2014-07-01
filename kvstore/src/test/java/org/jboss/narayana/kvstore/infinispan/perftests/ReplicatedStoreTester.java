package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class ReplicatedStoreTester extends MillTester {

	@Override
	public TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		if(runOnMill()) {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillReplCacheStore");
			
			setMessage("(Mill) Replicated Cache Store");
		} else {
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.ReplicatedStore");
			setMessage("Replicated Cache Store");
		}
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
