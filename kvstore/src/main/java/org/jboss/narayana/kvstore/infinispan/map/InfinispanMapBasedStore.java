package org.jboss.narayana.kvstore.infinispan.map;

import java.io.SyncFailedException;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreAPI;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;

public class InfinispanMapBasedStore implements ObjectStoreAPI {

	@Override
	public boolean commit_state(Uid u, String tn) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputObjectState read_committed(Uid u, String tn)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputObjectState read_uncommitted(Uid u, String tn)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove_uncommitted(Uid u, String tn)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write_uncommitted(Uid u, String tn, OutputObjectState buff)
			throws ObjectStoreException {
		throw new ObjectStoreException("Method Not Implemented");
	}

	@Override
	public boolean fullCommitNeeded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove_committed(Uid u, String tn)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write_committed(Uid u, String tn, OutputObjectState buff)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sync() throws SyncFailedException, ObjectStoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStoreName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean allObjUids(String s, InputObjectState buff, int m)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allObjUids(String s, InputObjectState buff)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allTypes(InputObjectState buff) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int currentState(Uid u, String tn) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hide_state(Uid u, String tn) throws ObjectStoreException {
		throw new ObjectStoreException("Method not implemented");
	}

	@Override
	public boolean reveal_state(Uid u, String tn) throws ObjectStoreException {
		throw new ObjectStoreException("Method not Implemented");
	}

	@Override
	public boolean isType(Uid u, String tn, int st) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	
}
