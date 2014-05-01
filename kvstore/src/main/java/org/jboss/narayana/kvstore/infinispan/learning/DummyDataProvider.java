package org.jboss.narayana.kvstore.infinispan.learning;

/**
 * Provides Dummy Data for testing Infinispan Caches
 * 
 * @author patches
 * 
 */
public class DummyDataProvider {

	private final int dataLength;
	
	private String[] gotKeys = { "ned", "jamie", "dany", "viper" };
	private String[] gotValues = { "Eddard Stark", "Jamie Lannister",
			"Danereys Targarayan", "Oberion Martell" };

	private String[] boatKeys = { "class", "loa", "sailArea", "Beam" };
	private String[] boatValues = { "class name: 49er",
			"length over all: 4.9m", "sail area (square meters): 54",
			"beam: 2.7m" };
	
	public DummyDataProvider() {
		dataLength = gotKeys.length;
	}

	public String[] gotKeys() {
		return gotKeys;
	}

	public String[] gotValues() {
		return gotValues;
	}
	
	public String gotKey(int i) {
		if(i>gotKeys.length || i<0) return null;
		return gotKeys[i];
	}
	
	public String gotValue(int i) {
		if(i>gotValues.length || i<0) return null;
		return gotValues[i];
	}
	

	public String[] boatKeys() {
		return boatKeys;
	}

	public String[] boatValues() {
		return boatValues;
	}
	
	public String boatKey(int i) {
		if(i>boatKeys.length || i<0) return null;
		return boatKeys[i];
	}
	
	public String boatValue(int i) {
		if(i>boatValues.length || i<0) return null;
		return boatValues[i];
	}
	
	public int length() {
		return dataLength;
	}

}
