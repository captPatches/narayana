package org.jboss.narayana.kvstore.jgroups.learning;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.ReplicatedHashMap;

public class MapExperimentation extends ReceiverAdapter {

	private ReplicatedHashMap<String, String> testMap;
	//private ReplCache<String, String> testMap;
	
	public MapExperimentation() throws Exception {
		System.setProperty("java.net.preferIPv4Stack",  "true");
		JChannel channel = new JChannel("udp.xml");
//		channel.setReceiver(this);
		channel.connect("my-cluster");
		testMap = new ReplicatedHashMap<String, String>(channel) {
		
			@Override
			public void viewAccepted(View new_view) {
				System.out.println(new_view.size());
			}
			
		};
	//testMap = new ReplCache<String, String>("jgroups-udp-cfg.xml", "my-cluster");
		testMap.start(3000);
		System.out.println(testMap.size());
	}
	
	private void eventLoop() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for(;;) {
			try {
				System.out.print("$:"); System.out.flush();
				String line = in.readLine().toLowerCase();				
				if(line.startsWith("quit") || line.startsWith("exit")) {
					System.exit(1);
					break;
				} else if(line.startsWith("put")) {
					String[] commands = line.split(" ");
					switch (commands.length) {
					case 1:
						put();
						break;
					case 2:
						put(commands[1]);
						break;
					default:
						put(commands[1], commands[2]);
						break;
					}
				} else if(line.startsWith("get")) {
					get(line);
				}
			} catch (Exception e) {
				// IGNORE
			}
		}
	}
	
	private void put(String key, String value) {
		if(nullString(key) || nullString(value)) {
			System.out.println("Key or value is null");
			return;
		}
		testMap.put(key, value);
	}
	
	private void put(String key) {
		if(nullString(key)) {
			System.out.println("Key or value is null");
			return;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("value needed $: "); System.out.flush();
			String value = in.readLine();
			if(nullString(value)) {
				System.out.println("value cannot be null");
				return;
			}
			testMap.put(key, value);
		} catch (Exception e) {
			throw new RuntimeException("hehehehehe");
		}
	}
	
	private void put() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String key;
		String value;
		try {
			System.out.println("key: "); System.out.flush();
			key = in.readLine();
			System.out.println("value: "); System.out.flush();
			value = in.readLine();
			if(nullString(key) || nullString(value)) {
				System.out.println("Key or Value cannot be null!");
				return;
			}
			testMap.put(key, value);
		} catch (Exception e) {
			System.out.println("MOOOOOO!!");
		}
	}
	
	private void get(String line) {
		String[] cmd = line.split(" ");
		switch (cmd.length) {
		case 1:
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("enter key $:"); System.out.flush();
			try {
				String key = in.readLine();
				if(!nullString(key)) {
					System.out.println(testMap.get(key));
				}
			} catch (Exception e) {
				System.out.println("get Exception moo!");
			}
			break;
		case 2:
			if(nullString(cmd[1])) break;
			System.out.println(testMap.get(cmd[1]));
		default:
			break;
		}
	}
	
	private boolean nullString(String str) {
		if(str == null || str == "") return true;
		return false;
	}
	
	public static void main(String[]args) throws Exception {
		new MapExperimentation().eventLoop();
	}
}
