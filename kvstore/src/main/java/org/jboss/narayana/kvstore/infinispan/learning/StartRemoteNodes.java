package org.jboss.narayana.kvstore.infinispan.learning;

public class StartRemoteNodes {

	
	public static void main(String[]args) throws Exception {
		
		String command = "ssh -t b3048933@mill004.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode \"";
		Runtime.getRuntime().exec(command);
		
		command = "ssh -t b3048933@mill005.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode \"";
		Runtime.getRuntime().exec(command);
		
		command = "ssh -t b3048933@mill006.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode \"";
		Runtime.getRuntime().exec(command);
		
		
		
	}
	
}
