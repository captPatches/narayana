package org.jboss.narayana.kvstore.infinispan.learning;

public class ArgTesting {

	private static int offset;
	
	public static void main(String[]args) {
		
		if(args.length > 1) {
			usage();
		}
		else if(args.length == 0) {
			offset = 0;
		}
		else {
			try {
				offset = Integer.parseInt(args[0]);
			} catch (Exception e) {
				usage();
			}
		}
	
		for(int i=(0+offset);i<(10+offset);i++) {
			System.out.println("Current iteration: "+(i+1));
		}
	}
	
	private static void usage() {
		System.out.println("ArgTesting <int>");
		System.exit(1);
	}
}
