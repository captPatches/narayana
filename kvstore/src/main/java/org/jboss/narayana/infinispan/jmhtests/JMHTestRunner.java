package org.jboss.narayana.infinispan.jmhtests;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


public class JMHTestRunner {
	public static void main(String[] ars) throws RunnerException {		
		
		TimeValue runTime = new TimeValue(10, TimeUnit.SECONDS);
		Options opt = new OptionsBuilder()
				.include(".*DistributedStoreJMH.*")
				//.exclude(".*Infinispan.*")
				.forks(1)
				.measurementIterations(4)
				.measurementTime(runTime)
				.warmupIterations(5)
				.timeUnit(TimeUnit.SECONDS)
				.threads(200)
				.build();

		new Runner(opt).run();
	}
}
