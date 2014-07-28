package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ProcessBuildingFun {

	public static void main(String[]args) throws IOException {
		
		String line;
		String[] cmd = { "java", "-cp", "target/classes/", "org.jboss.narayana.kvstore.infinispan.learning.HelloWorld" };
		
		Process p = new ProcessBuilder(cmd).start();
		
		System.out.println(Arrays.toString(cmd));
		BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		while((line = out.readLine()) != null) {
			System.out.println(line);
		}
		
		BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while((line = err.readLine()) != null) {
			System.out.println(line);
		}
	}
	
}
