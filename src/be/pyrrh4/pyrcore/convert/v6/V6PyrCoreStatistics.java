package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;
import java.util.Map;

public class V6PyrCoreStatistics {

	private Map<String, Map<String, Integer>> stats = new HashMap<String, Map<String, Integer>>();
	
	public void set(String pcUser, String stat, int value) {
		if (!stats.containsKey(stat)) {
			stats.put(stat, new HashMap<String, Integer>());
		}
		stats.get(stat).put(pcUser, value);
	}

}
