package org.jboss.narayana.kvstore.infinispan.perftests;

/**
 * Allows tests that will load will Mill-Cluster specific system properties
 * a chance to test if they are running on the Mill cluster or not. This
 * is useful in the current version to allow tests run on the Mill Cluster to 
 * choose a file location other than the default network SSD.
 * 
 * @author patches
 *
 */
public abstract class MillTester extends ObjectStorePerfTester {

	/**
	 * If the test is being run on the Mill-Cluster
	 * return true
	 * @return
	 */
	protected final boolean runOnMill() {
		try {
			String host = java.net.InetAddress.getLocalHost().getHostName();
			if(host.subSequence(0, 4).equals("mill")) return true;
		} catch(Exception e) {
			return false;
		}
		return false;
	}

}
