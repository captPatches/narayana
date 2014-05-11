package org.jboss.narayana.infinispankvstore;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class MillNode {

	private static String machineId = "";

	public static void main(String[] args) {

		// select the machine from the command line
		// Why doesn't -Djgroups.bind_addr work. Rarr
		if (args.length == 1) {
			switch (args[0]) {
			case "002":
			case "004":
			case "005":
				machineId = "-mill" + args[0] + "-";
				break;
			default:
				machineId = "-";
			}
		} else {
			machineId = "-";
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
									"jgroups-tcp" + machineId + "cfg.xml")
							.addProperty("clusterName", "b3408933-cluster")
							.build());

			// Define Cache Configuration
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.REPL_SYNC);
			cb.clustering().stateTransfer().fetchInMemoryState(true);
			manager.defineConfiguration("repl-cache", cb.build());

			// Define Distributed Cache
			cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.REPL_SYNC);
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
