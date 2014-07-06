package org.jboss.narayana.kvstore.infinispan.splitting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.narayana.kvstore.infinispan.learning.NoInputException;

public class NodeController {
	private Cache<String, String> cache;
	private DefaultCacheManager manager;
	
	public NodeController(String node) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		if(node == null) {
			node = "jgroups-tcp-cfg.xml";
		} else {
			node = "netconfig/node-"+node+"-cfg.xml";
		}
		
		try {
			manager = new DefaultCacheManager(
						GlobalConfigurationBuilder
							.defaultClusteredBuilder()
							.transport()
							.defaultTransport()
							.addProperty("configurationFile",
									node)
						//			"netconfig/node-"+node+"-cfg.xml")
						//			"jgroups-tcp-cfg.xml")
					.addProperty("clusterName", "splitting-cluster").build());

			// Define Cache Configuration
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(5);
		//	cb.clustering().stateTransfer().fetchInMemoryState(true);
			manager.defineConfiguration("dist-cache", cb.build());
			cache = manager.getCache("dist-cache");
		} catch (Exception e) {
			throw new RuntimeException("Cache Creation Failed");
		}
	}
	
	public void write(BufferedReader in) {
		try {
			System.out.print("key: ");
			String key = in.readLine();
			System.out.print("value: ");
			String value = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			if(value.equals("") || value.equals(null)) throw new NoInputException();
			cache.put(key, value);
		} catch (NoInputException e) {
			System.err.println("No Value Entered - Nothing Added");
			return;
		} catch (IOException e) {
			System.err.println("Input Exception Caught");
			return;
		}
		catch (Exception e) {
			System.err.println("Cache Write Failed");
		}
	}
	
	public void viewAll() {
		for(String key : cache.keySet()) {
			System.out.println("key: "+key+" - value: "+cache.get(key));
		}
	}
	
	public void delete(BufferedReader in) {
		try {
			System.out.print("key: ");
			String key = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			cache.remove(key);
		} catch (Exception e) {
			System.err.println("Remove Failed");
		}
	}
	
	public void start() {
		boolean exit = false;
		BufferedReader in = new BufferedReader(new InputStreamReader (System.in));
		
		while(exit == false) {
			System.out.print("cmd $: ");
			String cmd = null;
			try {
				cmd = in.readLine();
			} catch (IOException e) {
				cmd = null;
			}
			switch (cmd) {
			case "put":
				this.write(in);
				break;
			case "remove":
				this.delete(in);
				break;
			case "view":
				this.viewAll();
				break;
			case "exit":
				exit = true;
				System.exit(1);
				break;
			case "size":
				System.out.println("Cluster Size: "+this.manager.getClusterSize());
				break;
			default:
			}
		}
	}
}
