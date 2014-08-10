package org.jboss.narayana.infinispan.jmhtests;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class DisQuickJMH {
	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*Distributed.*")
				.forks(Opts.f())
				.measurementIterations(1)
				.measurementTime(Opts.r())
				.warmupIterations(Opts.wi())
				.timeUnit(TimeUnit.SECONDS)
				.threads(Opts.t())
				.build();

		new Runner(opt).run();
	}
}
