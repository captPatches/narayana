package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class JGroupsStorePerfTest extends ObjectStorePerfTester {

	@Override
	protected TransactionManager getTransManager() {
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
					"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
		
		System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName", 
				"org.jboss.narayana.kvstore.jgroups.JGroupsReplHashMapStore");
		
		setMessage("JGroups ReplMap Cache Store");
		
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	
}
