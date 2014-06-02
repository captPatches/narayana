package org.jboss.narayana.kvstore.infinispan.learning;

public class SubStrPlaying {

	public static void main(String[]args) {
		
		String ned = "Ned Stark";
		System.out.println(ned.substring(ned.indexOf(' ')));
		System.out.println(ned.substring(0, ned.indexOf('.')));
	}
}
