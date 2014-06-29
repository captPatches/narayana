package org.jboss.narayana.mapstore.infinispan;

import java.io.IOException;
import java.io.SyncFailedException;
import java.util.Map.Entry;
import java.util.Set;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.ObjectStoreAPI;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

public abstract class InfinispanDirectStore implements ObjectStoreAPI {

	public class CacheEntry {
		
		private String typeName;
		private OutputObjectState txData;
		
		public CacheEntry(String typeName, OutputObjectState txData) {
			this.typeName = typeName;
			this.txData = txData;
		}
		
		public String getTypeName() {
			return this.typeName;
		}
		
		public OutputObjectState getTxData() {
			return this.txData;
		}
	}
	
	private final DefaultCacheManager manager;
	private final AdvancedCache<Uid, CacheEntry> cache;
	
	public InfinispanDirectStore() {
		try {
			manager = setManager();
			cache = setCache(manager).getAdvancedCache();
		} catch (Exception e) {
			throw new RuntimeException("Invalid Infinispan Config");
		}
	}
	
	@Override
	public boolean commit_state(Uid u, String tn) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Current implementation is a lot inefficient, but object store is optimised for
	 * writes.
	 */
	@Override
	public InputObjectState read_committed(Uid u, String tn)
			throws ObjectStoreException {

		if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("InfinispanDirect.read_committed("+u+", "+ tn +")");
        }
		
		try {
			if(cache.containsKey(u)) {
				CacheEntry entry = cache.get(u);	
				return new InputObjectState(u, entry.getTypeName(), entry.getTxData().buffer());
			} throw new Exception();
		} catch (Exception e) {
			throw new ObjectStoreException("Read Failed");
		}
	}

	@Override
	public InputObjectState read_uncommitted(Uid u, String tn)
			throws ObjectStoreException {
		throw new ObjectStoreException(tsLogger.i18NLogger.get_method_not_implemented());
	}

	@Override
	public boolean remove_uncommitted(Uid u, String tn)
			throws ObjectStoreException {
		 if (tsLogger.logger.isTraceEnabled()) {
             tsLogger.logger.trace("LogStore.remove_uncommitted(" + u + ", " + tn + ")");
         }
		return false;
	}

	@Override
	public boolean write_uncommitted(Uid u, String tn, OutputObjectState buff)
			throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fullCommitNeeded() {
		return false;
	}

	@Override
	public boolean remove_committed(Uid u, String tn)
			throws ObjectStoreException {
		cache.remove(u);
		return true;
	}

	@Override
	public boolean write_committed(Uid u, String tn, OutputObjectState buff)
			throws ObjectStoreException {
		 
		if(tsLogger.logger.isTraceEnabled()) {
	            tsLogger.logger.trace("KVObjectStore.write_committed("+u+", "+ tn +")");
	    }
		
		try {
			CacheEntry entry = new CacheEntry(tn, buff);
			cache.withFlags(Flag.SKIP_CACHE_LOAD).put(u, entry);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void sync() throws SyncFailedException, ObjectStoreException {
		// Null opp in this implementation, the cache is pre-configured as Sync
	}

	@Override
	public String getStoreName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void start() {
		if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("InfinispanDirectStore.start()");
        }
		Set<Uid> keys = cache.keySet();
		
		if(keys == null || keys.size() == 0) {
			return;
		}
		
		// TODO
		/* Do magic */
	}

	@Override
	public void stop() {
		if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("InfinispanDirectStore.stop()");
        }
		manager.stop();
	}

	@Override
	public boolean allObjUids(String tn, InputObjectState buff, int matchState)
			throws ObjectStoreException {
		if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("InfinispanDirectStore.allObjUids("+ tn +", "+ matchState +")");
        }
		
		boolean result = true;
		Set<Uid> keys = cache.keySet();
		
		OutputObjectState buffer = new OutputObjectState();
		
		try {
			if((keys == null || keys.size() > 0)
					&& (matchState == StateStatus.OS_UNKNOWN || matchState == StateStatus.OS_COMMITTED)) {
				for(Uid key: keys) {
					UidHelper.packInto(key, buffer);
				}
			}
			UidHelper.packInto(Uid.nullUid(), buffer);
		} catch (IOException e) {
			throw new ObjectStoreException(e);
		}
		
		buff.setBuffer(buffer.buffer());
		
		return result;
	}

	@Override
	public boolean allObjUids(String s, InputObjectState buff)
			throws ObjectStoreException {
		
		return allObjUids(s, buff, StateStatus.OS_UNKNOWN);
		
	}

	/**
	 * This is pretty slow tbh...
	 */
	@Override
	public boolean allTypes(InputObjectState buff) throws ObjectStoreException {
		
		if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("FileSystemStore.allTypes(" + buff + ")");
        }
		boolean result = true;
		Set<Entry<Uid, CacheEntry>> entries = cache.entrySet();
		
		if(entries == null || entries.size() == 0) {
			return true;
		}
		
		OutputObjectState store = new OutputObjectState();
		
		for(Entry entry: entries) {
			CacheEntry cacheEntry = (CacheEntry) entry.getValue();
			try {
				store.packString(cacheEntry.getTypeName());
				result = allTypes(store, cacheEntry.getTypeName());						
			} catch (IOException e) {
				throw new ObjectStoreException("Types Unavailble");
			}
		}
		try {
			store.packString("");
		} catch (IOException e) {
			throw new ObjectStoreException("Types Unavailble");
		}
		buff.setBuffer(store.buffer());
		return result;
	}

	protected boolean allTypes (OutputObjectState foundTypes, String root) throws ObjectStoreException {
		
		if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("FileSystemStore.allTypes(" + foundTypes + ", " + root + ")");
        }
		
		boolean result = true;
		Set<Entry<Uid, CacheEntry>> entries = cache.entrySet();
		
		if(entries.size() > 0) {
		
			for(Entry<Uid,CacheEntry> entry: entries) {
				try {
					foundTypes.packString(root + "");
				}
				catch (Exception e) {
					throw new ObjectStoreException("Types Unavailble");
				}
			}
		}
		return result;		
	}
	
	@Override
	public int currentState(Uid u, String tn) throws ObjectStoreException {
		if(tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("InfinispanDirectStore.currentState("+ u +", "+ tn +")");
        }
        if( cache.containsKey(u)) {
            return StateStatus.OS_COMMITTED;
        } else {
            return StateStatus.OS_UNKNOWN;
        }
	}

	@Override
	public boolean hide_state(Uid u, String tn) throws ObjectStoreException {
		throw new ObjectStoreException("method not implemented");
	}

	@Override
	public boolean reveal_state(Uid u, String tn) throws ObjectStoreException {
		throw new ObjectStoreException("method not implemented");
	}

	@Override
	public boolean isType(Uid u, String tn, int st) throws ObjectStoreException {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected abstract Cache<Uid, CacheEntry> setCache(DefaultCacheManager manager) throws Exception;
	protected abstract DefaultCacheManager setManager() throws Exception;
}
