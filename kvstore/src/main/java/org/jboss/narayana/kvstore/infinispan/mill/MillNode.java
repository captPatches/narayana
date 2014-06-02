package org.jboss.narayana.kvstore.infinispan.mill;

import java.io.IOException;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 * Provides an infinispan node that contains the object store's replication
 * for use on the mill cluster, useful for performance testing different cluster
 * sizes
 * 
 * @author patches
 *
 */
public class MillNode {

	private static String machineId = getHostname();

	// Slightly less heavy weight method of acquiring the hostname
	// than starting a shell script
	private static String getHostname() {
		String hostname;
		try {
			// Get full hostname
			hostname = java.net.InetAddress.getLocalHost().getHostName();
			// remove the ".ncl.ac.uk"
			hostname = hostname.substring(0, hostname.indexOf('.') -1);
		} catch (IOException e) {
			hostname = "";
			System.err.println("Using Default Hostname");
		}
		
		return hostname;
	}
	
	public static void main(String[] args) {

		// Start the CacheManager and retrieve the Cache
		// Setup Cache Programmatically.
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager(
					GlobalConfigurationBuilder
							.defaultClusteredBuilder()
							.transport()
							.defaultTransport()
							.addProperty("configurationFile",
									"configlib/jgroups-tcp" + machineId + "cfg.xml")
							.addProperty("clusterName", "b3408933-cluster")
							.build());

			// Define Cache Configuration
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.REPL_SYNC);
			cb.clustering().stateTransfer().fetchInMemoryState(true);
			manager.defineConfiguration("repl-cache", cb.build());

			// Define Distributed Cache
			cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.DIST_SYNC);
			cb.clustering().hash().numOwners(3);
			cb.clustering().stateTransfer().fetchInMemoryState(true);
			manager.defineConfiguration("dist-cache", cb.build());

			manager.getCache("repl-cache");
			manager.getCache("dist-cache");
			System.out.println("Node Started Successfully");

		} catch (Exception e) {
			System.out.println("Node Failed To Start");
			System.out.println(e);
			throw new RuntimeException();
		}

	}

}
