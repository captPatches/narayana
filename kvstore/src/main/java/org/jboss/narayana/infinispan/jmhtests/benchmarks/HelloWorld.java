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

public class HelloWorld {

	private	static TransactionManager tm;
	
	public HelloWorld() {
		//System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
		//		"com.arjuna.ats.internal.arjuna.objectstore.VolatileStore");
		tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
	}
	
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public void volatileWorker() throws NotSupportedException, SystemException, IllegalStateException, RollbackException, SecurityException, HeuristicMixedException, HeuristicRollbackException {
				
		tm.begin();
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.getTransaction().enlistResource(new DummyXAResourceImpl());
		tm.commit();
		
	}
	
	/*
	@GenerateMicroBenchmark
	public void helloWorld() {
	
		// Method Intentionally left blank
	}*/
	
	public static void main(String[]ars) throws RunnerException {
		
		TimeValue tv = new TimeValue(20, TimeUnit.SECONDS);
		
		Options opt = new OptionsBuilder().
				include(".*" + HelloWorld.class.getSimpleName() + ".*").
				forks(1).measurementTime(tv).measurementIterations(1).
				build();
		
		new Runner(opt).run();
	}
	
}
