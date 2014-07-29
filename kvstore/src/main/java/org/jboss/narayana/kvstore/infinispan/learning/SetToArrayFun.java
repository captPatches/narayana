package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.HashSet;
import java.util.Set;

public class SetToArrayFun {

	private String[] strArray;
	private Set<String> strSet;
	
	private SetToArrayFun() {
		strSet = new HashSet<String>();
		strSet.add("Ned Stark");
		strSet.add("Jamie Lannister");
		strSet.add("Daenerys Targaeran");
		
		strArray = strSet.toArray(new String[strSet.size()]);
	}
	
	private void go() {
		for(int i=0; i<strArray.length; i++) {
			System.out.println(i + ": " + strArray[i]);
		}
	}
	
	public static void main(String[]args) {
		new SetToArrayFun().go();
	}
}
