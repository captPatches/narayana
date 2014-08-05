package org.jboss.narayana.kvstore.infinispan.recovery;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class RecoverTransaction {

	private final RecoveryManager rm;

	private RecoverTransaction() {
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

		rm = RecoveryManager.manager();
	}

	private void go() {
		System.out.printf("Transaction Successfully Recovered: %b%n",
				txRecovered());
	}

	private boolean txRecovered() {

		int commitRequests = DummyXAResource.getCommitRequests();
		System.out.println("[COMMIT REQs] " + commitRequests);
		rm.scan();

		if (commitRequests < DummyXAResource.getCommitRequests()) {
			System.out.println("[COMMIT REQs] " + commitRequests);
			System.out.println("End of Recovery Method - true");
			return true;
		} else if (commitRequests == DummyXAResource.getCommitRequests()) {
			System.out.println("[COMMIT REQs] " + commitRequests);
			System.out.println("End of Recovery Method - false");
			return false;
		} else {
			System.out.println("[COMMIT REQs] " + commitRequests);
			System.out.println("End of Recovery Method - Pear Shaped");
			throw new RuntimeException("Commit Requests Does Not Line Up.");
		}
	}
	
	public static void main(String[]args) {
		new RecoverTransaction().go();
	}
}
