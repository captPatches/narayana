package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NodeStartAndStopTest {

	
	@Before
	public void setup() {
	
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("/bin/bash");
		commands.add("-c");
		
		for(int i=4;i<7;i++) {
			commands.add("ssh -t b3048933@mill00" + i +".ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency* org.jboss.naryana.infinispankvstore.MillNode\"" );
		}
		
		String[] command =new String[commands.size()];
		for(int i=0;i<commands.size();i++) {
			command[i] = commands.get(i);
		}
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null)
				System.out.println(line);

			in.close();
		} catch( Exception e) {
			System.out.println(e);
		}		
	}
	
	@Test
	public void test() {
		System.out.println("moo");
	}
	
	/*@After
	public void tearDown() throws Exception {
		for(int i=4;i<7;i++) {
			String command = "ssh b3048933@mill00" + i +"kill -9 $(jps | grep Node | cut -d' ' -f1)";
			Runtime.getRuntime().exec(command);
		}
	}*/
	
}
