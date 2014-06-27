package org.jboss.narayana.kvstore.infinispan.learning;

class PrintHostname {
	public static void main(String args[]) throws java.net.UnknownHostException {
		String name = java.net.InetAddress.getLocalHost().getHostName();
		System.out.println(name.subSequence(0, 3));
  }
}