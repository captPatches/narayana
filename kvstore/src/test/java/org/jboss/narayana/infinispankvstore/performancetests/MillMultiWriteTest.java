package org.jboss.narayana.infinispankvstore;

import io.narayana.perf.PerformanceTester;
import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class MillMultiWriteTest {

	private class MultiWriteWorker implements Worker {

		private Cache<String, String> c;
		private String id;

		private long initTimemillis = -1;
		private long workTimeMillis = -1;
		private long finiTimeMillis = -1;

		@Override
		public Object doWork(Object context, int niters, Result opts)
				throws Exception {

			if (context != null)
				throw new IllegalArgumentException(
						"Object:context must be null - i.e. first arg in methd call must be null");

			for (int i = 0; i < niters; i++) {

				c.put(id + "_" + i, "Dummy Data" + 3 * i);
			}

			return null;

		}

		@Override
		public void init() {
			initTimemillis = System.currentTimeMillis();
		}

		@Override
		public void fini() {
			finiTimeMillis = System.currentTimeMillis();
		}

		

		public MultiWriteWorker(Cache<String, String> c, String id) {
			this.c = c;
			this.id = id;
		}

	}

	// ///////////

	private String machineId;
	private Cache<String, String> c;

	private int niters = 1000;
	private int threadCnt = 20;

	@Before
	public void setup() {

		String[] CMD = { "/bin/sh", "-c", "hostname | cut -d'.' -f1" };

		try {
			Process p = Runtime.getRuntime().exec(CMD);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			machineId = "-" + in.readLine() + "-";
			in.close();
		} catch (Exception e) {
			// Use default localhost configuration.
			System.err.println("Can't find hostname - Using default Localhost");
			machineId = "";
		}

		try {
			EmbeddedCacheManager manager = new DefaultCacheManager(
					GlobalConfigurationBuilder
							.defaultClusteredBuilder()
							.transport()
							.defaultTransport()
							.addProperty(
									"configurationFile",
									"configlib/jgroups-tcp" + machineId
											+ "cfg.xml")
							.addProperty("clusterName", "b3408933-cluster")
							.build());

			// Define Cache Configuration
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.REPL_SYNC);
			cb.clustering().stateTransfer().fetchInMemoryState(true);
			manager.defineConfiguration("rep", cb.build());

			c = manager.getCache("rep");
		} catch (Exception e) {
			throw new RuntimeException("No Available Cache.");
		}
	}

	@Test
	public void test() {

		PerformanceTester tester = new PerformanceTester();
		MultiWriteWorker worker = new MultiWriteWorker(c, machineId);

		Result opts = new Result(niters, threadCnt);

		tester.measureThroughput(worker, opts);
		System.out
				.printf("\nRESULTS: Infinispan Distributed Mode: %d Txs / second (total time: %d)\n",
						opts.getThroughput(), opts.getTotalMillis());

	}

}
