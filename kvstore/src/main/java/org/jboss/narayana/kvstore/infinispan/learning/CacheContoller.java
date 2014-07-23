package org.jboss.narayana.kvstore.infinispan.learning;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.read.KeySetCommand;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.rpc.RpcOptions;
import org.infinispan.remoting.transport.Address;

public class CacheContoller {

	private Set<String> acks = new HashSet<String>();
	private Cache<String, String> cache;
	
	protected DefaultCacheManager manager;
	
	public CacheContoller() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			manager = new DefaultCacheManager("generic-test-cfg.xml");
			cache = manager.getCache("dis");
			cache.addListener(new FullListener(acks));
		} catch (Exception e) {
			throw new RuntimeException("Cache Creation Failed");
		}
	}
	
	public CacheContoller(Boolean moo) {
		
	}
	
	protected void setCache(Cache<String, String> cache) {
		this.cache = cache; 
	}
	
	public void write(BufferedReader in) {		
		try {
			System.out.print("key: ");
			String key = in.readLine();
			System.out.print("vale: ");
			String value = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			if(value.equals("") || value.equals(null)) throw new NoInputException();
			cache.put(key, value);
		//	System.err.println("Acks List: " + acks);
		} catch (NoInputException e) {
			System.err.println("No Value Entered - Nothing Added");
			return;
		} catch (IOException e) {
			System.err.println("Input Exception Caught");
			return;
		}
	}
	
	
	
	
	public void viewAll() {
	//	Set keys = cache.getAdvancedCache().getDataContainer().keySet();
		
		for(Object key : cache.getAdvancedCache().getDataContainer().keySet()) {
			System.out.println("key: "+key.toString()+" - value: "+cache.get(key.toString()));
		}
		cache.getAdvancedCache();
	}
	
	public void delete(BufferedReader in) {
		try {
			System.out.print("key: ");
			String key = in.readLine();
			if(key.equals("") || key.equals(null)) throw new NoInputException();
			cache.remove(key);
		} catch (Exception e) {
			System.err.println("Remove Failed");
		}
	}
	
	public void rpcTry() {
		List<Address> recipients = cache.getCacheManager().getMembers();
		
		RpcOptions options = null;
		ReplicableCommand rpc = new KeySetCommand(cache.getAdvancedCache().getDataContainer(), null);
		System.out.println(cache.getAdvancedCache().getRpcManager().invokeRemotely(recipients, rpc, options));
	}
	
	public void getMembers() {
		try {
			String regex = "[\\[\\]\\s]";
			Set<String> members = new HashSet<String>(Arrays.asList(manager.getClusterMembers().replaceAll(regex, "").split(",")));
			for(String s : members)
				System.out.println(s);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
