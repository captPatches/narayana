package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class GlusterPerfTest extends ObjectStorePerfTester {

	@Override
	protected TransactionManager getTransManager() {

		/*
		 * System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
		 * "com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqObjectStoreAdaptor"
		 * );
		 * 
		 * System.setProperty(
		 * "HornetqJournalEnvironmentBean.storeImplementationClassName",
		 * "com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqJournalStore"
		 * );
		 */
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir",
				"/mnt/ramdisk/brick");

		setMessage("tmpfs and gluster based objectstore");

		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

}
