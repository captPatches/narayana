package org.jboss.narayana.kvstore.infinispan.learning;

public class SplittingAString {

	public static void main(String[]args) {
		
		String easyKey = "daenerys_234";
		String sillyKey = "deanerys_Targaryan_The_millionth_moo_700";
		
		System.out.println(easyKey.substring(easyKey.indexOf('_') +1));
		Long id = Long.parseLong(sillyKey.substring(sillyKey.lastIndexOf('_')+1));
		System.out.println(id + 29L);
	}
}
