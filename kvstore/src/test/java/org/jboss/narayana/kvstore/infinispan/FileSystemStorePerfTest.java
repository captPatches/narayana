package org.jboss.narayana.kvstore.infinispan;

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

	TransactionManager tm;

	@Before
	public void setup() {

		// Set System properties to use infinispanKVStore
		System.setProperty(
				"ObjectStoreEnvironmentBean.storeImplementationClassName",
				"com.arjuna.ats.internal.arjuna.objectstore.FileLockingStore");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

	}

	@Test
	public void speedTest() {

		int threadsNum = 20;
		int transCount = 5000000;

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		Worker<BigInteger> worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		// discount any tests that contain an error
		if (opts.getErrorCount() > 0)
			throw new RuntimeException("There was error - Test Failed!!");

		System.out
				.printf("\nRESULTS: FileLockingStore: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());

	}

	@After
	public void tearDown() {
		StoreManager.shutdown();

	}

}
