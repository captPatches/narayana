package org.jboss.narayana.kvstore.infinispan.splitting;

public class NodeA {

	public static void main(String[]args) {
		new NodeController("a").start();
	}
}
