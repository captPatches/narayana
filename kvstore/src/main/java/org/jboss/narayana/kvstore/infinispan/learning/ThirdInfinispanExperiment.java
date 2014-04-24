package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.Set;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
//import org.infinispan.configuration.cache.ConfigurationBuilder;


/**
 * Looking to try and run infinispan in client / server mode
 * Checking out aan ability to connect to remote cache with HotRod.
 * 
 * @author patches
 *
 */
public class ThirdInfinispanExperiment {

	public static void main(String[]args) {
		
		// Setup configuraation for talking to remote cache
		// ConfigurationBuilder confBuilder = new ConfigurationBuilder();
		// Configuration config = confBuilder.addServer().host("127.0.0.1").port(11222).build();
		
//		RemoteCacheManager manager = new RemoteCacheManager(config, true);
		RemoteCacheManager manager = new RemoteCacheManager(true);
		RemoteCache<String, String> rc = manager.getCache();
		
		// Attempt to put stuff in remote cache
		rc.put("ned", "Eddard Stark");
		rc.put("sam", "Samwise Tarly");
		
		//System.out.println(rc.get("ned"));
		
		// Attempt to read stuff from remote cache
		Set<String> keys = rc.keySet();
		for(String key : keys) System.out.println(rc.get(key));
		
		// Explicitly stop the cache
		rc.stop();
		//*/
	}
	
}

// "./usr/infinispan-server-6.0.2.Final/bin/standalone.sh"

