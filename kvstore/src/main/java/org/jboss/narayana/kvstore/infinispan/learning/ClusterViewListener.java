package org.jboss.narayana.kvstore.infinispan.learning;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

@Listener
public class ClusterViewListener {

	@ViewChanged
	public void viewChanged(ViewChangedEvent event) {
		
		int oldSize;
		
		try {
			oldSize = event.getOldMembers().size();
		} catch (Exception e) {
			oldSize = 0;
		}
		int newSize = event.getNewMembers().size();
		
		if(oldSize > newSize) System.out.println("Lost A Member");
		else if(oldSize < newSize) System.out.println("Gained a Member");
		else System.out.println("Something Went  Wrong");
		
	}
	
}
