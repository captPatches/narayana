package org.jboss.narayana.kvstore.infinispan.nodes;

import java.io.IOException;

import org.infinispan.manager.DefaultCacheManager;

/**
 * Runs an infinispan node for testing purposes that has knowledge of a
 * replicated cache store and a distributed cache store
 * 
 * @author patches
 * 
 */
public class NodeForTesting {

	public static void main(String[] args) {
		
		System.setProperty("java.net.preferIPv4Stack", "true");
		
		try {
			DefaultCacheManager manager = new DefaultCacheManager("multi-cache-cfg.xml");
			manager.getCache("distributed-cache");
			//manager.getCache("replication-cache");
			System.out.println("Cluster Size: " + manager.getClusterSize());
			System.out.println("Node Started Successfully");
		} catch (IOException ioe) {
			System.out.println("Node Failed to Start - no Config File");
		} catch (Exception e) {
			System.out.print("Node Failed:\n" + e.getMessage());
		}
	}
//  iptables -I INPUT -s 192.168.0.0/24,172.17.130.0/22 -p udp -j ACCEPT
}
