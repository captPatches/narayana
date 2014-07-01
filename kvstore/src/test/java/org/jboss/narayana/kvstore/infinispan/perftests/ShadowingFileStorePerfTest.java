package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class ShadowingFileStorePerfTest extends MillTester {

	@Override
	protected TransactionManager getTransManager() {
		// Make sure not to use the network drive on the Mill
		if(runOnMill() ) {
			System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir",
					"/work/b3048933/shadowingStore");
			setMessage("(Default) Shadowing Store (Mill Mode");
		}
		setMessage("(Default) Shadowing Store");
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

}
