package org.jboss.narayana.kvstore.infinispan.networktests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class LocalNode {

	private final NodeController ctrl;
	
	public LocalNode(String node) {
		ctrl = new NodeController(node);
	}
	
	public void start() {
		boolean exit = false;
		CacheController controller = new CacheController();
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
				controller.write(in);
				break;
			case "remove":
				controller.delete(in);
				break;
			case "view":
				controller.viewAll();
				break;
			case "exit":
				exit = true;
				System.exit(1);
				break;
			default:
			}
		}
	}
}
