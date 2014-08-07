package org.jboss.narayana.infinispan.jmhtests;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class JGroupsJHM {

	public static void main(String[]args) throws RunnerException {
		TimeValue runTime = new TimeValue(10, TimeUnit.MINUTES);
		Options opt = new OptionsBuilder()
		.include(".*JGroups*.*")
		.forks(1)
		.measurementIterations(5)
		.measurementTime(runTime)
		.warmupIterations(10)
		.timeUnit(TimeUnit.SECONDS)
		.threads(200)
		.build();

		new Runner(opt).run();
	}
}
