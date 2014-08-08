package org.jboss.narayana.infinispan.jmhtests;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Provides values such as thenumber of iterations for the JMH Tests
 * @author patches
 *
 */
public class Opts {

	// Measurement Interations
	private static int i = 5;
	
	// Forks
	private static int f = 1;
	
	//warmup iterations
	private static int wi = 10;
	
	// Measurement Run time
	private static TimeValue r = new TimeValue(10, TimeUnit.MINUTES);

	// threads
	private static int t = 200;
	
	public static int i() { return i; }
	public static int f() { return f; }
	public static int wi() { return wi; }
	public static int t() { return t; }
	public static TimeValue r() { return r; }
	
	
}
