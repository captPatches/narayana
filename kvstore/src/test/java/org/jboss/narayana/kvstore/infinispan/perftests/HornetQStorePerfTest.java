package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class HornetQStorePerfTest extends MillTester {

	@Override
	public TransactionManager getTransManager() {
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqObjectStoreAdaptor");

		System.setProperty(
				"HornetqJournalEnvironmentBean.storeImplementationClassName",
				"com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqJournalStore");

		if(runOnMill() ) {
			System.setProperty("HornetqJournalEnvironmentBean.storeDir", "/work/b3048933/hornetQ");
		}
		
		setMessage("HornetQ Journalling Store");
		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
}
