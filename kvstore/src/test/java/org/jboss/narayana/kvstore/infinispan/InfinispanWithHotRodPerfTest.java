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

public class InfinispanWithHotRodPerfTest {

	TransactionManager tm;

	@Before
	public void setup() {

		// Set System properties to use infinispanKVStore
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.HotrodRemoteCacheKVStore");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

	}

	@Test
	public void speedTest() {

		int threadsNum = 20;
		int transCount = 1000000;

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		Worker<BigInteger> worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		if (opts.getErrorCount() > 0)
			throw new RuntimeException("mehhh some errors");

		System.out
				.printf("\nRESULTS: Infinispan (hotrod) performance: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());

	}

	@After
	public void tearDown() {
		StoreManager.shutdown();
	}

}
