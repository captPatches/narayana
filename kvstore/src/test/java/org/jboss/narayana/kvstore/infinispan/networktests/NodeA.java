package org.jboss.narayana.kvstore.infinispan.networktests;

public class NodeA {

	public static void main(String[]args) {
		new NodeController("a").start();
	}
}
