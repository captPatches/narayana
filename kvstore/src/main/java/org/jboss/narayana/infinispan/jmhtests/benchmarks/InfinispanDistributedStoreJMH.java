package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.arjuna.ats.arjuna.objectstore.StoreManager;

@State(Scope.Benchmark)
public class InfinispanDistributedStoreJMH {

	private static TransactionManager tm;
	private static AtomicBoolean finalRun = new AtomicBoolean(false);
	
	public InfinispanDistributedStoreJMH() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.kvstore.infinispan.DistributedStore");
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void infinispanDistributedStoreWorker()
			throws NotSupportedException, SystemException,
			IllegalStateException, RollbackException, SecurityException,
			HeuristicMixedException, HeuristicRollbackException {

		if(finalRun.get()) {
			return;
		}
		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();

	}
	
	@TearDown(Level.Trial)
	public void tearDown() throws SystemException {
		if(finalRun.compareAndSet(false, true)) {
			StoreManager.shutdown();
		}
	}
}
