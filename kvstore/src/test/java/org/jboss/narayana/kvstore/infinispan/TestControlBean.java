package org.jboss.narayana.kvstore.infinispan;

public class TestControlBean {

	private final static int threadsNum = 20;
	private final static int transCount = 100000;

	public static int threadsNum() {
		return threadsNum;
	}

	public static int transCount() {
		System.out.println("TEST-ITERS: " + transCount);
		return transCount;
	}
}
