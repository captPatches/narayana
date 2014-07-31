package org.jboss.narayana.kvstore.infinispan.nodes;

/**
 * Runs an infinispan node for testing purposes that has knowledge of a
 * replicated cache store and a distributed cache store
 * 
 * @author patches
 * 
 */
public class NodeForTesting extends AbstractNode {

	private NodeForTesting(String cacheName, String cfgFile) throws Exception {
		super(cacheName, cfgFile);
	}

	public static void main(String[] args) {

		String defaultCacheName = "dis";
		String defaultCfgFile = "generic-test-cfg.xml";

		try {
			switch (args.length) {
			case 1:
				new NodeForTesting(args[0], defaultCfgFile);
				break;
			case 2:
				new NodeForTesting(args[0], args[1]);
				break;
			default:
				new NodeForTesting(defaultCacheName, defaultCfgFile);
				break;
			}
		} catch (Exception e) {
			System.out.println("Node Startup Failed");
			System.exit(1);
		}

	}
	// iptables -I INPUT -s 192.168.0.0/24,172.17.130.0/22 -p udp -j ACCEPT
}
