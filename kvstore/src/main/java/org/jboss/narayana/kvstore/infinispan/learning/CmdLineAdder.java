package org.jboss.narayana.kvstore.infinispan.learning;

public class CmdLineAdder {

	public static void main(String[]args) {

		for(String s : args) {
			System.out.println(s);
		}
		
		/*
		if(args.length != 2) {
			System.err.println("Moo Rarr");
		}
		else {
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			System.out.println(a + b);
		}  */
	}
	
}
