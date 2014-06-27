package org.jboss.narayana.kvstore.infinispan.learning;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractJUnitTest {

	private String msg = getMessage();
	
	@Before
	public abstract void setup();
	
	@Test
	public void test() {
		System.out.println(msg);
	}
	
	protected abstract String getMessage();
	
	
}
