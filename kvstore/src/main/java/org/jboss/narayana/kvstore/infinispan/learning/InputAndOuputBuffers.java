package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.Arrays;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.state.InputBuffer;
import com.arjuna.ats.arjuna.state.OutputBuffer;

public class InputAndOuputBuffers {

	private String name;
	private byte[] data;
	private Uid uid;
	
	private InputAndOuputBuffers() {
		name = "Stark";
		uid = new Uid();
		data = name.getBytes();	
	}
	
	private void go() throws Exception {
		
		OutputBuffer ob = new OutputBuffer();
		ob.packString(name);
		ob.packBytes(data);
		
		InputBuffer ib = new InputBuffer(ob.buffer());
		String foo = ib.unpackString();
		byte[] moo = ib.unpackBytes();
		System.out.printf("%s - %s%n", name, foo);
		
		System.out.println(Arrays.equals(data, moo));
	}
	
	public static void main(String[]args) throws Exception {
		new InputAndOuputBuffers().go();
	}
}
