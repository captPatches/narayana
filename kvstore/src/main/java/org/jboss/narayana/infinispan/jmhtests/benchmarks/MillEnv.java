package org.jboss.narayana.infinispan.jmhtests.benchmarks;

/**
 * Useful for benchmark testing on the mill cluster (University provided
 * resource), provides a method that returns true if running on the mill
 * environment. Knowing that the machine is running on the mill cluster will
 * force the log to be written to local file system instead of the default nfs
 * 
 * @author patches
 * 
 */
public abstract class MillEnv {

	/**
	 * If the test is being run on the Mill-Cluster return true
	 * 
	 * @return
	 */
	protected final boolean runOnMill() {
		try {
			String host = java.net.InetAddress.getLocalHost().getHostName();
			if (host.subSequence(0, 4).equals("mill"))
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

}
