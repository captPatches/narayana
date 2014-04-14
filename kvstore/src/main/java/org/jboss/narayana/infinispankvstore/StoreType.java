package org.jboss.narayana.infinispankvstore;

public enum StoreType {
	INFINISPAN ("infinispankvstore.HotrodRemoteCacheKVStore"),
	MEMCACHED ("kvstore.MemcachedKVStore");
	
	String type;
	
	private StoreType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public StoreType[] getTypes() {
		return StoreType.values();
	}
}