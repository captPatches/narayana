package org.jboss.narayana.infinispankvstore;

public enum StoreType {
	
	// Each instance includes info about whether the Adapator is needed	
	INFINISPAN ("infinispankvstore.HotrodRemoteCacheKVStore", "org.jboss.narayana.", true),
	MEMCACHED ("kvstore.MemcachedKVStore", "org.jboss.narayana.", true),
	ACTIONSTORE ("ActionStore", "com.arjuna.ats.internal.arjuna.objectstore.", false);
	
	// Object Store String split for legacy testing reasons
	private String type;
	private String location;
	private boolean adaptorNeeded;
	
	private StoreType(String type, String location, boolean adaptorNeeded) {
		this.type = type;
		this.location = location;
		this.adaptorNeeded = adaptorNeeded;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLocation() {
		return location;
	}
	
	public boolean adaptorNeeded() {
		return adaptorNeeded;
	}
	
	public StoreType[] getTypes() {
		return StoreType.values();
	}
}