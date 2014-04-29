package org.jboss.narayana.kvstore.infinispan;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;

import java.math.BigInteger;

import javax.transaction.TransactionManager;

import org.jboss.narayana.infinispankvstore.KVStoreWorkerTM;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

public class InfinispanEmbeddedCachePerfTest {

	private TransactionManager tm;
	private int threadsNum;
	private int transCount;

	@Before
	public void setup() {

		// Set System properties to use infinispanKVStore
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.NoReplInfinispanKVStore");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

		threadsNum = TestControlBean.threadsNum();
		transCount = TestControlBean.transCount();
	}

	@Test
	public void speedTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		KVStoreWorkerTM worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		System.out
				.printf("\nRESULTS: Infinispan Embedded performance: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());
	}

	@After
	public void tearDown() {
		StoreManager.shutdown();
	}

}
