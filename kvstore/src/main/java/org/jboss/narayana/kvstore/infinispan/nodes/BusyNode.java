package org.jboss.narayana.kvstore.infinispan.nodes;

import javax.transaction.TransactionManager;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class BusyNode {
	
	private class Worker implements Runnable {

		@Override
		public void run() {
			int cnt = 0;
			while (true) {
				try {
					tm.begin();
					tm.getTransaction().enlistResource(new DummyXAResourceImpl());
					tm.getTransaction().enlistResource(new DummyXAResourceImpl());
					tm.commit();
					cnt++;
					if(cnt == 1000) {
						System.out.println("transaction still going");
						cnt=0;
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		
	}
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

		for(int i=0; i<50; i++) {
			System.out.println("Starting thread: " + i);
			Runnable worker = new Worker();
			Thread thread = new Thread(worker);
			thread.setName(String.valueOf(i));
			thread.start();
		}
		
	}
	
	public static void main(String[]args) throws CoreEnvironmentBeanException, RuntimeException {
		System.out.println("Started: ");
		new BusyNode().go();
		StoreManager.shutdown();
		System.out.println("Finished");
	}

}
