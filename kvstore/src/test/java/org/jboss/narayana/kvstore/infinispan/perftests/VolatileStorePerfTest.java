package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class VolatileStorePerfTest extends ObjectStorePerfTester {

	@Override
	public TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.VolatileStore");
		
		setMessage("Volatile Store (Java Native)");
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
