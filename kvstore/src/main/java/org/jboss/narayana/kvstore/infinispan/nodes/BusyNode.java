package org.jboss.narayana.kvstore.infinispan.nodes;

import javax.transaction.TransactionManager;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class BusyNode {

	private TransactionManager tm;
	private String nodeId;

	private BusyNode() throws CoreEnvironmentBeanException, RuntimeException {
		System.setProperty("java.net.preferIPv4Stack", "true");

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.kvstore.infinispan.DistributedStore");
		try {
			nodeId = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			nodeId = "Default: " + System.currentTimeMillis();
		}
		
		BeanPopulator.getDefaultInstance(CoreEnvironmentBean.class).setNodeIdentifier(nodeId);
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	private void go() {

		while (true) {
			try {
				tm.begin();
				tm.getTransaction().enlistResource(new DummyXAResourceImpl());
				tm.getTransaction().enlistResource(new DummyXAResourceImpl());
				tm.commit();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	public static void main(String[]args) throws CoreEnvironmentBeanException, RuntimeException {
		new BusyNode().go();
		StoreManager.shutdown();
		System.out.println("Finished");
	}

}
