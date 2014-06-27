package org.jboss.narayana.infinispankvstore.objectstoretests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVObjectStoreAdaptor;
import com.arjuna.ats.internal.arjuna.objectstore.kvstore.KVStoreEnvironmentBean;

/**
 * Test all methods of the KVStore Adaptor, having set the store implementation
 * to use InfinispanKVObjectStore
 * 
 * @author patches
 *
 */
public class ObjectStoreTests {
	
	private final KVStoreEnvironmentBean kvseb = new KVStoreEnvironmentBean();
	private KVObjectStoreAdaptor kvstore;
	
	@Before
	public void setup() {
		
		try {
			kvseb.setstoreImplementation(new org.jboss.narayana.infinispankvstore.InfinispanWithEmbeddedClusteredReplicationKVStore());
			kvstore = new KVObjectStoreAdaptor(kvseb);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testInfinispanWithEmbeddedClusteredReplicationKVStore() throws Exception {
		
		final OutputObjectState buff = new OutputObjectState();
        final String tn = "/StateManager/junit";
        
        for (int i = 0; i < 100; i++)
        {
            Uid u = new Uid();
            
            kvstore.write_uncommitted(u, tn, buff);
            
            kvstore.commit_state(u, tn);
            
            assertTrue(kvstore.currentState(u, tn) != StateStatus.OS_UNCOMMITTED);
            
            InputObjectState ios = new InputObjectState();
            
            kvstore.allObjUids("", ios);
            
            assertTrue(kvstore.read_uncommitted(u, tn) == null);
            
            kvstore.write_committed(u, tn, buff);
            kvstore.read_committed(u, tn);
            
            assertTrue(!kvstore.remove_uncommitted(u, tn));
            
            kvstore.remove_committed(u, tn);
            
            assertTrue(!kvstore.hide_state(u, tn));
            
            assertTrue(!kvstore.reveal_state(u, tn));
        }
		
	}
}
