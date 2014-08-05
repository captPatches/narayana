package org.jboss.narayana.kvstore.infinispan.recovery;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class PopulateObjectStore {

	private final TransactionManager tm;

	private PopulateObjectStore() {

		System.setProperty("java.net.preferIPv4Stack", "true");

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.kvstore.infinispan.DistributedStore");

		System.setProperty("com.arjuna.ats.arjuna.nodeIdentifier",
				"capt_patches_node");
		System.err.printf("Node Id: %s%n",
				BeanPopulator.getDefaultInstance(CoreEnvironmentBean.class)
						.getNodeIdentifier());

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	private void go() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, SecurityException, HeuristicMixedException, HeuristicRollbackException {

		tm.begin();

		// enlist some resources
		tm.getTransaction().enlistResource(
				new DummyXAResource(DummyXAResource.faultType.NONE));
		tm.getTransaction().enlistResource(
				new DummyXAResource(DummyXAResource.faultType.NONE));
		tm.getTransaction().enlistResource(
				new DummyXAResource(DummyXAResource.faultType.HALT));

		// Attempt to commit resource which will cause system to halt
		System.out.println("Halting VM when attempting to commit Resource");
		tm.commit();
	}

	public static void main(String[] args) throws IllegalStateException, SecurityException, NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		new PopulateObjectStore().go();

	}

}
