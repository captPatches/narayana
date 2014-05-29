package org.jboss.narayana.infinispankvstore;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.objectstore.RecoveryStore;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

/**
 * Test to make sure the ObjectStore is functioning as expected.
 * 
 * The object store is loaded with fake data, of which a random amount is
 * removed
 * 
 * It then checks to find out how much is in there to make sure it is thhe
 * expected amount.
 * 
 * @author patches
 * 
 */
public class ObjectStoreRecoveryV1Test {

	@Before
	public void setUp() {

		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType",
				"com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor");

		System.setProperty(
				"KVStoreEnvironmentBean.storeImplementationClassName",
				"org.jboss.narayana.infinispankvstore.NoReplInfinispanKVStore");

		// emptyObjectStore();
	}

	@Test
	public void test() {

		RecoveryStore recoveryStore = StoreManager.getRecoveryStore();

		final int numTrans = 1000;
		final Uid[] ids = new Uid[numTrans];
		final int fakeData = 0xdeedbaaf;
		final String type = "/KVStoreTests/ObjectStoreRecoveryV1Test";
		final int rand = getRandom(numTrans);

		// Write Data for 1000 transactions
		for (int i = 0; i < numTrans; i++) {
			OutputObjectState dummyState = new OutputObjectState();

			try {
				dummyState.packInt(fakeData);
				ids[i] = new Uid();
				recoveryStore.write_committed(ids[i], type, dummyState);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * Remove all but a random number of transactions from the log.
		 */
		for (int i = 0; i < (numTrans - rand); i++) {
			try {
				recoveryStore.remove_committed(ids[i], type);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		InputObjectState ios = new InputObjectState();
		boolean passed = true;

		try {
			if (recoveryStore.allObjUids(type, ios, StateStatus.OS_UNKNOWN)) {
				Uid id = new Uid(Uid.nullUid());
				int numberOfEntries = 0;

				do {
					try {
						id = UidHelper.unpackFrom(ios);
					} catch (Exception e) {
						id = Uid.nullUid();
					}

					if (id.notEquals(Uid.nullUid())) {
						numberOfEntries++;

						boolean found = false;

						for (int i = 0; i < ids.length; i++) {
							if (id.equals(ids[i]))
								found = true;
						}

						if (passed && !found) {
							passed = false;

							System.err.println("Found Unknown Transaction");
						}
					}
				} while (id.notEquals(Uid.nullUid()));

				if ((numberOfEntries == rand) && passed) {
					System.err.println("Would attempt recovery on "
							+ numberOfEntries + " dead transactions.");
				} else {
					passed = false;

					System.err.println("Expected " + rand + " and got "
							+ numberOfEntries);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		assertTrue(passed);
	}

	@After
	public void tearDown() {

		StoreManager.shutdown();
	}

	/**
	 * Clear the object store from the Infinispan Cache
	 * 
	 * @return true on success and false on failure
	 */
	public boolean emptyObjectStore() {

		// TODO

		// probably not needed as shutting down the store manager should have a
		// similar
		// effect.

		return true;
	}

	/**
	 * Hide functionalilty for generating psudeo random num, not useful for main
	 * test method
	 * 
	 * @param max
	 *            - largest value to return
	 * @return a randomly generated int, less than or equal to the supplied max
	 *         and greater than 0
	 */
	public int getRandom(int max) {

		Random gen = new Random();
		return gen.nextInt(max) + 1;
	}

}
