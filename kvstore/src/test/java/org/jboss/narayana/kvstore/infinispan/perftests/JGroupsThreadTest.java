package org.jboss.narayana.kvstore.infinispan.perftests;

import static org.junit.Assert.assertEquals;

import javax.transaction.TransactionManager;

import io.narayana.perf.Result;
import io.narayana.perf.WorkerWorkload;

import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap;
import org.junit.Before;
import org.junit.Test;

public class JGroupsThreadTest {

	private ReplicatedHashMap<String, String> map;
	private int threadsCount = 400;
	private int numberOfJobs = 5000;
	private int batchSize = 10;
	private String message = "testing threads";

	@Before
	public void setup() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		JChannel cnl = new JChannel("udp.xml");
		map = new ReplicatedHashMap<String, String>(cnl);
		cnl.connect("test-cluster1");
	}

	@Test
	public void test() {

		Result<Integer> measurement = new Result<Integer>(threadsCount, numberOfJobs,
				batchSize).measure(new WorkerWorkload<Integer>() {

			@Override
			public Integer doWork(Integer context, int batchSize, Result<Integer> measure) {
				if(context==null) context=0;
				System.out.println("Moo "+context);
				map.put("ned_"+context, "stark");
				return (Integer) context++;
			}

		});

		assertEquals("There should be no errors!!", 0,
				measurement.getErrorCount());
		System.out.printf("\nRESULTS: " + message
				+ ": %d Txs / second (total time: %d)\n",
				measurement.getThroughput(), measurement.getTotalMillis());
	}
}
