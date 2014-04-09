package org.jboss.narayana.infinispankvstore;

import java.util.ArrayList;
import java.util.List;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStore;

/**
 * Trying out Infinispan for the first time,
 * See what it can do (Or what I can do with it)
 * 
 * @author patches
 *
 */
@SuppressWarnings("unused")
public class FirstInfinispanTest {

	private final static Cache<String, String> c = new DefaultCacheManager().getCache();
	
	
	/**
	 * Pre-populates list with keys
	 * @return
	 */
	private static List<String> keyList() {
		
		List<String> keys = new ArrayList<String>();
		keys.add("ned");
		keys.add("jamie");
		keys.add("john");
		
		return keys;
	}
	
	/**
	 * Pre-populates list with data items
	 * @return
	 */
	private static List<String> dataList() {
		
		List<String> data = new ArrayList<String>();
		data.add("Edddard Stark");
		data.add("Jamie Lannister");
		data.add("John Snow");
		
		return data;
	}
	
	private static String simpleAddTest(List<String> keys, List<String> dataItems) {
				
		for(int i=0; i < keys.size(); i++) {
			c.put(keys.get(i), dataItems.get(i));
		}
		
		String returnStr = "";
		for(String key : keys) {
			returnStr += c.get(key) + "\n";
		}
		
		return returnStr;
		
	}
	
	private static String removeTest(List<String> keys) {
		
		simpleAddTest(keyList(), dataList());
		String key = keys.remove(0);
		c.remove(key);
		
		String returnStr = "";
		for(String readKey : keys) {
			returnStr += c.get(readKey) + "\n";
		}
		return returnStr;
	}
	
	private static String replaceTest(List<String> keys, List<String> dataItems) {
		
		for(int i=0; i < keys.size(); i++) {
			c.put(keys.get(i), dataItems.get(i));
		}
		
		c.replace("john", "John Snow and Ghost");
		
		// Test What happens when attempting to replace a dead key...
		String deadKey = "iDontExist";
		c.replace(deadKey, "FooBar");
		keys.add(deadKey);
		
		
		String returnStr = "";
		for(String key : keys) {
			returnStr += c.get(key) + "\n";
		}
		
		return returnStr;
		
	}
	
	
	public static void main(String[]args) {
			
		//String testStr = simpleAddTest(keyList(), dataList());
		//String testStr = removeTest(keyList());
		String testStr = replaceTest(keyList(), dataList());
		System.out.println(testStr);
		c.stop();
	}
	
}
