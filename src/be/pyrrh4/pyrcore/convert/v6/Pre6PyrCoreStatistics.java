package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;
import java.util.UUID;

public class Pre6PyrCoreStatistics {

	private UUID uuid;
	private HashMap<String, Integer> stats = new HashMap<String, Integer>();

	public UUID getUniqueId() {
		return uuid;
	}

	public HashMap<String, Integer> getStats() {
		return stats;
	}

}
