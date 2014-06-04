package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.HashMap;
import java.util.Map;

public class SystemHaltLearning {

	public static void main(String[]args) {
		
		Map<String, String> testMap = new HashMap<String, String>();
		
		if(!testMap.isEmpty()) {
			System.out.println("testMap Data Found!!");
		} else {
			testMap.put("ned", "Eddard Stark");
			Runtime.getRuntime().halt(1);
			testMap.put("hound", "Sandor Clegane");
			
			System.out.println("Map Populated");
			
		}
		
	}
	
}
