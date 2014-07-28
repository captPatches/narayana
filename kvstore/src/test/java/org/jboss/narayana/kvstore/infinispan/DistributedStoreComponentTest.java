package org.jboss.narayana.kvstore.infinispan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DistributedStoreComponentTest {

	private DistributedStore store;

	@Before
	public void setup() {
		System.setProperty(
				"java.net.preferIPv4Stack","true");
		store = new DistributedStore();
	}

	@Test
	@Ignore
	public void deadMembersTest() throws Exception {
		Random rand = new Random();
		int randomInt = 10000 + (int) (rand.nextFloat() * 90000);
		String key;

		assertEquals("Testing Hostname in key-cache", 1, store.allKeys().size());

		try {
			key = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			key = "default_hostname";
		}
		key += "-" + randomInt;
		assertTrue("adding key:", store.addKey(key));
		assertEquals("there should be two keys in KeyCache now", 2, store
				.allKeys().size());

		Set<String> deadMembers = store.getDeadMembers();
		assertEquals("There shoud be only 1 dead member:", 1,
				deadMembers.size());
		assertEquals(
				"The dead member should be the fictional one added earlier",
				deadMembers.toString(), "[" + key + "]");

		// test that eviction works:
		store.load();
		assertEquals("load(): Remove Dead Memebers", 1, store.allKeys().size());
	}

	@Test
	public void kvstoreMethodsTest() throws Exception {

		store.start();

		String stark = "Ned Stark";
		String lannister = "Jamie Lannister";

		long id = store.allocateId();
		assertEquals("id should be 0", 0L, id);
		store.add(id, stark.getBytes());

		long id2 = store.allocateId();
		assertEquals("2nd ID should be 1", 1L, id2);
		store.add(id2, lannister.getBytes());

		addDeadMember();
		assertEquals("Adding fake member", 2, store.allKeys().size());

		System.out.println(store.allKeys() + " -- " + store.getDeadMembers());
		store.load();

		// Hopefully Dead member has been removed
		System.out.println(store.allKeys() + " -- " + store.getDeadMembers());
		assertEquals("Remove Dead Members with no keys", 1, store.allKeys()
				.size());

		// Adding a dead member with new keys
		String[] command = {"java", "-cp", "target/test-classes/:target/classes/:target/dependency/*", "org.jboss.narayana.kvstore.infinispan.FakeKeys" };
		try {
			Process p = new ProcessBuilder(command).start();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line;
			while((line = in.readLine())!= null) {
				System.err.println("--" + line);
			}
			
			in.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		
		//There should now be 1 dead member
		System.out.println(store.getDeadMembers()+" -- "+store.allKeys());
		assertEquals("Dead Member number check", 1, store.getDeadMembers().size());
		assertEquals("Checking entries in keyCache", 2, store.allKeys().size());
		
	}

	private void addDeadMember() {
		Random rand = new Random();
		int randomInt = 10000 + (int) (rand.nextFloat() * 90000);
		String key;

		assertEquals("Testing Hostname in key-cache", 1, store.allKeys().size());

		try {
			key = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			key = "default_hostname";
		}
		key += "-" + randomInt;
		store.addKey(key);
	}
}
