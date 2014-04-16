package org.jboss.narayana.kvstore.infinispan.kvstorecore;

import java.math.BigInteger;

import javax.transaction.TransactionManager;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import org.jboss.narayana.infinispankvstore.KVStoreWorkerTM;
import org.jboss.narayana.infinispankvstore.StoreType;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class PerformnceTestingTest {
	
	@Test
	public void performanceTest() {
	
		//Initial Check
		int threadNum = 20;
		int jobsCount = 100000;
		
		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		// Create TransactionManager within the worker
		//KVStoreWorker worker = new KVStoreWorker(StoreType.INFINISPAN);
		
		// Try creating TransactionManager at this level and passing down to worker - might be faster
		KVStoreWorkerTM worker = new KVStoreWorkerTM(getTransManager(StoreType.INFINISPAN));
		
		Result<BigInteger> opts = new Result<BigInteger>(threadNum, jobsCount);
		/*Result<BigInteger> res = /**/tester.measureThroughput(worker, opts);
		
		System.out.printf("Test performance for %d!: %d Txs / second (total time: %d )",
                opts.getNumberOfCalls(), opts.getThroughput(), opts.getTotalMillis());
		
		// Move the store shutdown
		StoreManager.shutdown();
		
		//assertEquals(true, true);
		
	}
	
	private TransactionManager getTransManager(StoreType storeType) {
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
                "com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

        System.setProperty("KVStoreEnvironmentBean.storeImplementationClassName",
                "org.jboss.narayana." + storeType.getType());
		
		return (TransactionManager) com.arjuna.ats.jta.TransactionManager.transactionManager();
		
	}

}
