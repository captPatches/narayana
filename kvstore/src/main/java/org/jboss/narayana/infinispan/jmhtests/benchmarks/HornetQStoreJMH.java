package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import java.util.concurrent.TimeUnit;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class HornetQStoreJMH extends MillEnv {

	private static TransactionManager tm;

	public HornetQStoreJMH() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqObjectStoreAdaptor");

		System.setProperty(
				"HornetqJournalEnvironmentBean.storeImplementationClassName",
				"com.arjuna.ats.internal.arjuna.objectstore.hornetq.HornetqJournalStore");

		if (runOnMill()) {
			System.setProperty("HornetqJournalEnvironmentBean.storeDir",
					"/work/b3048933/hornetQ");
		}

		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void hornetQStoreWorker() throws NotSupportedException,
			SystemException, IllegalStateException, RollbackException,
			SecurityException, HeuristicMixedException,
			HeuristicRollbackException {

		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();

	}
	
	public static void main(String[] ars) throws RunnerException {		
		
		TimeValue runTime = new TimeValue(2, TimeUnit.SECONDS);
		Options opt = new OptionsBuilder()
				.include(".*" + HornetQStoreJMH.class.getSimpleName() +".*")
				.forks(1)
				.measurementIterations(1)
				.measurementTime(runTime)
				.warmupIterations(2)
				.timeUnit(TimeUnit.SECONDS)
				.threads(200)
				.build();

		new Runner(opt).run();
	}
}
