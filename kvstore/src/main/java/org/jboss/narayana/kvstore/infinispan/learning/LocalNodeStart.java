package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LocalNodeStart {

	public static void main(String[]args) {
		
		String command = "java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.NodeForTesting";
		String[] TestNodeUp = {
				"/bin/sh",
				"-c",
				"jps | grep NodeForTesting"
		};
		
		try {
			System.out.println("Starting Node \n\r");
			Runtime.getRuntime().exec(command);
			Process p = Runtime.getRuntime().exec(TestNodeUp);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(line);
				if(line != null) System.out.println("okay Dokey");
			}
			in.close();
			
			
			//Process p = Runtime.getRuntime().exec(command);
			//BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//String line;
			//while((line = in.readLine()) != null) {
				//System.out.println(line);
			//	if(line == "Node Started Successfully") System.out.println("okay Dokey");
		//	}
		//	in.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
}
