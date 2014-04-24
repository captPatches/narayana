package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FirstExecExpt {

	public static void main(String[] args) {

		String command = "ls -l";  ///home/patches";
		System.out.println(command);
		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = null;
			while ((line = in.readLine()) != null)
				System.out.println(line);

			in.close();
			
			System.out.println("Done");
			
		} catch (Exception e) {
			System.out
					.println("Somethhing went wrong but I'm not going to give you a stack trace!!");
		}
	}
}
