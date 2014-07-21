package org.jboss.narayana.kvstore.infinispan.nodes;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public class JGroupsNode {

	private ReplicatedHashMap<String, byte[]> map;
	
	private JGroupsNode() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		JChannel channel = new JChannel("jgroups-udp-cfg.xml");
		map = new ReplicatedHashMap<String, byte[]>(channel) {
			@Override
			public void viewAccepted(View newView) {
				super.viewAccepted(newView);
				System.out.println(newView);
			}
		};
		channel.connect("130489331-ObjectStoreCluster");
	}
	
	private void eventLoop() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {
				System.out.println("$: "); System.out.flush();
				String line = in.readLine().toLowerCase();
				if(line.startsWith("quit") || line.startsWith("exit")) {
					break;
				}
			} catch (Exception e) {
				//IGNORE
			}
		}
	}
	
	public static void main(String[]args) throws Exception {
		new JGroupsNode().eventLoop();
	}
}
