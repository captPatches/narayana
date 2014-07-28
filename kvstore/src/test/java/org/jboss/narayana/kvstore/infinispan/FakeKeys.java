package org.jboss.narayana.kvstore.infinispan;

/**
 * Used to place keys into keyCache for a "dead member" to prove that dead
 * members with keys are not removed from the key-cache.
 * 
 * @author patches
 * 
 */
public class FakeKeys {

	DistributedStore store;

	public FakeKeys() {
		System.setProperty(
				"java.net.preferIPv4Stack","true");
		store = new DistributedStore();
	}

	public void go() throws Exception {
		store.start();
		byte[] data = new String("Stark").getBytes();
		
		long id = store.allocateId();
		store.add(id, data);
		System.out.println(store.allKeys());
		
		// Assume this store fails.
		store.stop();
		System.exit(0);
	}		
	
	public static void main(String[]args) throws Exception {
		new FakeKeys().go();
	}
}
