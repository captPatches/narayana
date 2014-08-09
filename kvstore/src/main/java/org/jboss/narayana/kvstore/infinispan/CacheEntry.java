package org.jboss.narayana.kvstore.infinispan;

import java.io.Serializable;

public class CacheEntry implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1181404079252863239L;
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
