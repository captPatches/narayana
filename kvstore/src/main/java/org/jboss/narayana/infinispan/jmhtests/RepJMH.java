package org.jboss.narayana.infinispan.jmhtests;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class RepJMH {

	public static void main(String[]args) throws RunnerException {

		Options opt = new OptionsBuilder()
		.include(".*Replicated*.*")
		.exclude(".*Direct*.*")
		.forks(Opts.f())
		.measurementIterations(Opts.i())
		.measurementTime(Opts.r())
		.warmupIterations(Opts.wi())
		.timeUnit(TimeUnit.SECONDS)
		.threads(Opts.t())
		.build();

		new Runner(opt).run();
	}
}	
