package org.jboss.narayana.kvstore.infinispan.perftests;

public class DirectStorePerfTest extends MillTester {

	@Override
	public void setup() {
		System.setProperty("ObjectStoreEnvironmentBean.objectStoreType", 
				"org.jboss.narayana.mapstore.infinispan.ReplDirectStore");
		
		if(runOnMill()) {
			setMessage("(Running on Mill DirectStore");
		}
		else {
			setMessage("DirectStore");
		}
	}
}
