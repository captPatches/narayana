package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class BehaviourTester {

	public static void main(String[]args) {
		
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		
		boolean exit = false;
		CacheContoller controller = new CacheContoller();
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
			case "add":
				//controller.cache2Write(in);
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
			case "rpc":
				controller.rpcTry();
				break;
			case "mem":
				controller.getMembers();
				break;
			case "get":
				controller.get(in);
				break;
			case "co":
				// Provide output describing whether or not this node is the current co-orindator.
				controller.co();
				break;
			default:
			}
		}
	}
}
