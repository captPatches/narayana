package org.jboss.narayana.kvstore.infinispan.networktests;

public class NodeB {

	public static void main(String[]args) {
		new NodeController("b").start();
	}
}
