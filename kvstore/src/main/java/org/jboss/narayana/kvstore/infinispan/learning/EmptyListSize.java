package org.jboss.narayana.kvstore.infinispan.learning;

import java.util.LinkedList;

/**
 * Purely Here to elminate the cause of a NullPointerException
 * Being thrown by ClusterViewListener
 * @author patches
 *
 */
public class EmptyListSize {

	public static void main(String[] args) {
		
		// Guess its something to do with trying to call OldList
		// before the cluster view changes?
		
		LinkedList<String> list = new LinkedList<String>();
		int aNum = list.size();
		int bNum = -5;
		
		System.out.println(bNum + aNum);
		
	}

}
