package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Current incarnation is used for testing the Mill Node automatically picking
 * up which config file to use
 * 
 * @author patches
 * 
 */
public class FirstExecExpt {

	public static void main(String[] args) {

		String[] command = { "/bin/sh", "-c", "hostname | cut -d'.' -f1" };
		System.out.println(command);
		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "-" + in.readLine() + "-";
			in.close();
			System.out.println(line);
			// String[] parts = line.split("\\.");
			// System.out.println(parts[0]);

			// while ((line = in.readLine()) != null)
			// System.out.println(line);

			// in.close();

			System.out.println("Done");

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
