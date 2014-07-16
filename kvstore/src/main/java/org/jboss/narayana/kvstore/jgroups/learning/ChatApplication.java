package org.jboss.narayana.kvstore.jgroups.learning;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class ChatApplication extends ReceiverAdapter {

	private JChannel channel;
	private String username = System.getProperty("user.name", "n/a");
	private final List<String> state = new LinkedList<String>();
	
	private void start() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("chatCluster");
		eventLoop();
		channel.close();
	}
	
	private void eventLoop() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for(;;) {
			try {
				System.out.print(">:"); System.out.flush();
				String line = in.readLine().toLowerCase();
				if(line.startsWith("quit") || line.startsWith("exit")) break;
				line = "[" + username + "]"+line;
				Message msg = new Message(null, null, line);
				channel.send(msg);
			} catch (Exception e) {
				// IGNORE
			}
		}
	}
	
	public static void main(String[]args) throws Exception {
		new ChatApplication().start();
	}
	
	@Override
	public void viewAccepted(View new_view) {
		System.out.println("** View: " + new_view);
	}
	
	@Override
	public void receive(Message msg) {
		System.out.println(msg.getSrc() + ": " + msg.getObject());
	}
	
	@Override
	public void getState(OutputStream output) throws Exception {
		synchronized(state) {
	        Util.objectToStream(state, new DataOutputStream(output));
		}
	}
	
	public void setState(InputStream input) throws Exception {

	    List<String> list;
	    list=(List<String>)Util.objectFromStream(new DataInputStream(input));

	    synchronized(state) {
	        state.clear();
	        state.addAll(list);
	    }
	    
	    System.out.println(list.size() + " messages in chat history):");
	    for(String str: list) {
	        System.out.println(str);
	    }
	}
}
