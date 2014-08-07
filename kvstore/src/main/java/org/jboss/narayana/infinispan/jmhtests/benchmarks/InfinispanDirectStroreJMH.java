package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

public class InfinispanDirectStroreJMH {

	private static TransactionManager tm;

	public InfinispanDirectStroreJMH() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"org.jboss.narayana.kvstore.infinispan.InfinispanStoreAdapter");
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void infinispanReplicatedStoreWorker() throws NotSupportedException,
			SystemException, IllegalStateException, RollbackException,
			SecurityException, HeuristicMixedException,
			HeuristicRollbackException {

		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();
	
	}

}
