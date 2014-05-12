package org.jboss.narayana.infinispankvstore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class MillNode {

	// Pipes in single line son't work, so execute as as script
	private final static String[] CMD = {
			"/bin/sh",
			"-c",
			"hostname | cut -d'.' -f1" 
			};
	private static String machineId = "";

	public static void main(String[] args) {

		//find which machine to run on.
		try {
			Process p = Runtime.getRuntime().exec(CMD);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			machineId = "-" + in.readLine() + "-";
			in.close();	
		} catch (Exception e) {
			// Use default localhost configuration.
			System.err.println("Can't find hostname - Using default Localhost");
			machineId = "";
		}

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

		} catch (Exception e) {
			throw new RuntimeException();
		}

	}

}
