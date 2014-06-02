package org.jboss.narayana.kvstore.infinispan.learning;

public class AbstractJUnitSubClass extends AbstractJUnitTest {

	@Override
	public void setup() {
		System.out.println("@Before method functioning");
	}

	@Override
	protected String getMessage() {
		return "This is working";
	}
	
	

}
