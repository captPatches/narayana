package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class DirectStorePerfTest extends MillTester {

	@Override
	protected TransactionManager getTransManager() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType", 
				"org.jboss.narayana.kvstore.infinispan.InfinispanStoreAdapter"
				);
				
		if(runOnMill()) {
			setMessage("(Running on Mill DirectStore");
		}
		else {
			setMessage("DirectStore");
		}
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
}
