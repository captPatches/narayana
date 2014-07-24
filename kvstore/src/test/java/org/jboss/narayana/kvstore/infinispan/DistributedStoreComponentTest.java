package org.jboss.narayana.kvstore.infinispan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DistributedStoreComponentTest {

	private DistributedStore store= new DistributedStore();

	@Before
	public void setup() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		//store = new DistributedStore();
	}

	@Test
	public void deadMembersTest() {
		Random rand = new Random();
		int randomInt =  10000 + (int) (rand.nextFloat() * 90000);
		String key;
		
		assertEquals("Testing Hostname in key-cache", 1, store.allKeys().size());
		
		try {
			key = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			key = "default_hostname";
		}
		key += "-"+randomInt;
		assertTrue("adding key:", store.addKey(key));
		assertEquals("there should be two keys in KeyCache now", 2, store.allKeys().size());
		
		Set<String> deadMembers = store.getDeadMembers();
		assertEquals("There shoud be only 1 dead member:", 1, deadMembers.size());
		assertEquals("The dead member should be the fictional one added earlier", deadMembers.toString(), "["+key+"]");
	}
}


