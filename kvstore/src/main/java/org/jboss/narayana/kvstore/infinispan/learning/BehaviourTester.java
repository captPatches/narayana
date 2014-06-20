package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BehaviourTester {

	public static void main(String[]args) {
		
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
			case "remove":
				controller.delete(in);
				break;
			case "view":
				controller.viewAll();
				break;
			case "exit":
				exit = true;
				break;
			default:
			}
		}
	}
}
