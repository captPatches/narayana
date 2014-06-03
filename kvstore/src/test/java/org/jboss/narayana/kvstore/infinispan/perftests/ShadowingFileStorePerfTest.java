package org.jboss.narayana.kvstore.infinispan.perftests;

public class ShadowingFileStorePerfTest extends MillTester {

	@Override
	public void setup() {
		if(runOnMill() ) {
			System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir",
					"/work/b3048933/shadowingStore");
			setMessage("(Default) Shadowing Store (Mill Mode");
		}
		setMessage("(Default) Shadowing Store");
	}

}
