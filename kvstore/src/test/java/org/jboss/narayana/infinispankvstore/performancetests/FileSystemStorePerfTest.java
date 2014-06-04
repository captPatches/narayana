package org.jboss.narayana.infinispankvstore.performancetests;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.jboss.narayana.infinispankvstore.KVStoreWorkerTM;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class FileSystemStorePerfTest {
	
	private TransactionManager tm;
	private int threadsNum;
	private int transCount;

	@Before
	public void setup() {

		// Don't Set System properties to use infinispanKVStore
		// Default Store
		/*
		System.setProperty(
				"ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.FileLockingStore");
		*/
		
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir", "/work/b3048933/ObjectStore");
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
		
		threadsNum = TestControlBean.threadsNum();
		transCount = TestControlBean.transCount();

	}

	@Test
	public void speedTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		Worker<BigInteger> worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		// discount any tests that contain an error
		if (opts.getErrorCount() > 0)
			throw new RuntimeException("There was error - Test Failed!!");

		System.out
				.printf("\nRESULTS: Default File Store: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());

	}

	@After
	public void tearDown() {
		StoreManager.shutdown();

	}

}
