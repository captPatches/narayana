package org.jboss.narayana.kvstore.infinispan.learning;

import io.narayana.perf.Result;
import io.narayana.perf.Worker;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class NoSizeCheckWorker implements Worker<Integer> {

	private DefaultCacheManager manager;
	private Cache<String, String> cache;

	private long initTimeMills = -1;
	private long finiTimeMills = -1;

	@Override
	public Integer doWork(Integer context, int niters, Result opts)
			throws Exception {

		/*if (context < 0) {
			throw new RuntimeException("Cluster Size cannot be negative");
		}*/

		int size ;
		for (int i = 0; i < niters; i++) {
			cache.put(niters + "_key", niters + "_value");
		}
		return null;
	}

	@Override
	public void init() {
		try {
			manager = new DefaultCacheManager("generic-test-cfg.xml");
			cache = manager.getCache("rep");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		initTimeMills = System.currentTimeMillis();
	}

	@Override
	public void fini() {
		finiTimeMills = System.currentTimeMillis();
	}

}
