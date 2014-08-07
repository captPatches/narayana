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

public class ShadowingStoreJMH extends MillEnv {

private	static TransactionManager tm;
	
	public ShadowingStoreJMH() {
		if (runOnMill()) {
			System.setProperty("HornetqJournalEnvironmentBean.storeDir",
					"/work/b3048933/hornetQ");
		}
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void shadowingStoreWorker() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, SecurityException, HeuristicMixedException, HeuristicRollbackException {
				
		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();
		
	}
}
