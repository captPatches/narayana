package org.jboss.narayana.kvstore.infinispan;

import java.util.Set;

/**
 * Interface for directly manipulating the keyCache the distributed object store relies upon to
 * to find out what the keys in the object store are.
 * 
 * @author patches
 *
 */
public interface KeyCacheAPI {

	public boolean addKey(String key);
	
	public boolean removeKey(String key);
	
	public Set<String> allKeys();
	
	public boolean contains(String key);
}
