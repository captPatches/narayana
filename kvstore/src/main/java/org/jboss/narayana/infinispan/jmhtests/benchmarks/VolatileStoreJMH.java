package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class VolatileStoreJMH extends AbstractObjStPrefJMH {

	private static TransactionManager tm;
	
	public VolatileStoreJMH() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.VolatileStore");
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
	
	@GenerateMicroBenchmark
	@BenchmarkMode(Mode.Throughput)
	public void volatileWorker() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, SecurityException, HeuristicMixedException, HeuristicRollbackException {
		
		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();
		
	}
	
	public static void main(String[]args) throws RunnerException {
		
		//String regexp = ".*" + VolatileStoreJMH.class.getSimpleName() + ".*";
		
		Options opt = new OptionsBuilder().
				include(".*" + VolatileStoreJMH.class.getSimpleName() + ".*").forks(1).build();
		
		new Runner(opt).run();
		
	}
	
	
}
