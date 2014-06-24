package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class DistSizeCheckPerfTest extends MillTester {

	protected TransactionManager getTM() {

		// As UDP currently seems to be working on the Mill
		// There should be no need for Mill Box Specific setup files 
		// to be used.
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");
	
		if(runOnMill()) {
			setMessage("(Mill) Distributed Cache Store");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.mill.MillDistSizeCheckStore"
					);
			
		}
		else {
			setMessage("Distributed Infinispan CacheStore");
			System.setProperty(
					"KVStoreEnvironmentBean.storeImplementationClassName",
					"org.jboss.narayana.kvstore.infinispan.DistributedSizeCheckStore");
		}
		
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
}
