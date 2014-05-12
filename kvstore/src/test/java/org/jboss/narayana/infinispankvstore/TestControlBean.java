package org.jboss.narayana.infinispankvstore;

public class TestControlBean {

	private final static int threadsNum = 250;
	private final static int transCount = 10000;

	public static int threadsNum() {
		System.out.println("Thread-Count: " + threadsNum);
		return threadsNum;
	}

	public static int transCount() {
		System.out.println("TEST-ITERS: " + transCount);
		return transCount;
	}
}
