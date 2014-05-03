package org.jboss.narayana.infinispankvstore;

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

public class InfinispanWithEmbeddedReplicationPerfTest {

	private TransactionManager tm;
	private int threadsNum;

	private int transCount;

	@Before
	public void setup() {

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.InfinispanWithEmbeddedClusteredReplicationKVStore");

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

		threadsNum = TestControlBean.threadsNum();
		transCount = TestControlBean.transCount();

	}

	@Test
	public void perTest() {

		PerformanceTester<BigInteger> tester = new PerformanceTester<BigInteger>();
		Worker<BigInteger> worker = new KVStoreWorkerTM(tm);

		Result<BigInteger> opts = new Result<BigInteger>(threadsNum, transCount);
		tester.measureThroughput(worker, opts);

		// discount any tests that contain an error
		if (opts.getErrorCount() > 0)
			throw new RuntimeException("There was error - Test Failed!!");

		System.out
				.printf("\nRESULTS: Replicating InfinispanStore: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());

	}

	@After
	public void teardown() {
		StoreManager.shutdown();
	}

}
