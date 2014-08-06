package org.jboss.narayana.infinispan.jmhtests.benchmarks;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.parameters.TimeValue;

public abstract class AbstractObjStPrefJMH {

	protected Options otps() {

		TimeValue tv = new TimeValue(10, TimeUnit.MINUTES);
		return new OptionsBuilder()
				.forks(1)
				.measurementTime(tv)
				.measurementIterations(1).build();
	}

}
