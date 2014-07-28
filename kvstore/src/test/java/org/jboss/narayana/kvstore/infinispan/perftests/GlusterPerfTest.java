package org.jboss.narayana.kvstore.infinispan.perftests;

import javax.transaction.TransactionManager;

public class GlusterPerfTest extends ObjectStorePerfTester {

	@Override
	protected TransactionManager getTransManager() {

		/*
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir",
				"/mnt/gluster");
		 */

		setMessage("tmpfs and gluster based objectstore");

		return com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

}
