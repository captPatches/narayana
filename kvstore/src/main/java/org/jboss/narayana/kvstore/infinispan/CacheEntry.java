package org.jboss.narayana.kvstore.infinispan;

public class CacheEntry {
	
	private final String typeName;
	private final byte[] txData;
	
	public CacheEntry(String typeName, byte[] txData) {
		this.typeName = typeName;
		this.txData = txData;
	}
	
	public String getType() {
		return typeName;
	}
	
	public byte[] getTxData() {
		return txData;
	}
	
}
