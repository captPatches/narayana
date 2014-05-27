package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecSSHTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		int i = 6;
		String[] command = 
		//		"/bin/sh",
		//		"-c",
		//		"ssh -t b3048933@mill00"+i+".ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode \"" ;
			{"/bin/sh", "-c", "ssh b3048933@mill004.ncl.ac.uk last | grep 'May 12'" };
				//"ssh -t b3048933@mill006.ncl.ac.uk \"cd narayana/kvstore ; java -cp target/classes/:target/dependency/* org.jboss.narayana.infinispankvstore.MillNode \"" };
		try {
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = in.readLine()) != null)
				System.out.println(line);

			in.close();

			System.out.println("Done");

		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
