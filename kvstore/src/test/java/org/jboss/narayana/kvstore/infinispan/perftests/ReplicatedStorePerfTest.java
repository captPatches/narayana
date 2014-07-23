package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class ReplicatedStorePerfTest extends ObjectStorePerfTester {

	@Override
	protected TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.kvstore.infinispan.ReplicatedStore");

		setMessage("Replicated Cache Store");
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
