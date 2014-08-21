package org.jboss.narayana.kvstore.infinispan.objectstoretests;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.recovery.RecoveryManager;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

public class LocalRepDummyRecoveryTest {

	private final static String NODE_ID = "rec_node";
	
	private TransactionManager tm;
	private RecoveryManager rm;
	
	@BeforeClass
	public static void propertySetup() throws CoreEnvironmentBeanException, RuntimeException {

		System.setProperty("java.net.preferIPv4Stack", "true");
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.kvstore.infinispan.DistributedStore");
		
		List<String> xaRecoveryNodes = new LinkedList<String>();
		xaRecoveryNodes.add(NODE_ID);
		BeanPopulator.getDefaultInstance(JTAEnvironmentBean.class).setXaRecoveryNodes(xaRecoveryNodes);
		BeanPopulator.getDefaultInstance(CoreEnvironmentBean.class).setNodeIdentifier(NODE_ID);
	}
	
	@Before
	public void setup() {		
	
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		rm = RecoveryManager.manager();
	}
	/**
	 * First scan to see if there are any transactions to recover. If there
	 * are, recover them, else try to commit a new transaction (that will fail).
	 * @throws SystemException 
	 * @throws NotSupportedException 
	 * @throws RollbackException 
	 * @throws IllegalStateException 
	 * @throws HeuristicRollbackException 
	 * @throws HeuristicMixedException 
	 * @throws SecurityException 
	 */
	@Test
	public void recoveryTest() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, SecurityException, HeuristicMixedException, HeuristicRollbackException {
		
		if(txRecovered() ) {
			System.out.println("Recovery Successfully Performed");
			return;
		}
		
		/* 
		 * Assuming there was no transaction recovered
		 * start one that will fail, ensuring that there will
		 * be a transaction to recover on the next run
		 */
		// Begin Transaction
		tm.begin();
		
		//enlist some resources
		tm.getTransaction().enlistResource(new DummyXAResource(DummyXAResource.faultType.NONE));
		tm.getTransaction().enlistResource(new DummyXAResource(DummyXAResource.faultType.NONE));
		tm.getTransaction().enlistResource(new DummyXAResource(DummyXAResource.faultType.HALT));
		
		// Attempt to commit resource which will cause system to halt
		System.out.println("Halting VM when attempting to commit Resource");
		tm.commit();
	}
	
	
	/**
	 *Attempt recovery - return true if something to recover was found, and false
	 * if nothing was found.
	 */
	private boolean txRecovered() {
		int commitRequests = DummyXAResource.getCommitRequests();
		System.out.println("[COMMIT REQs] "+commitRequests);
		rm.scan();
		
		if(commitRequests < DummyXAResource.getCommitRequests()) {
			System.out.println("[COMMIT REQs] "+commitRequests);
			System.out.println("End of Recovery Method - true");
			return true;
		}
		else if(commitRequests == DummyXAResource.getCommitRequests() ) {
			System.out.println("[COMMIT REQs] "+commitRequests);
			System.out.println("End of Recovery Method - false");
			return false;
		}
		else {
			System.out.println("[COMMIT REQs] "+commitRequests);
			System.out.println("End of Recovery Method - Pear Shaped");
			throw new RuntimeException("Commit Requests Does Not Line Up.");
		}
	}
}
