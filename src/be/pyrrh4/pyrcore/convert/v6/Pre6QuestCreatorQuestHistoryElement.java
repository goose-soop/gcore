package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.UUID;

public class Pre6QuestCreatorQuestHistoryElement {

	private UUID uuid;
	private String modelId;
	private ArrayList<Pre6QuestCreatorFollowedObjectData> followedPath;
	private long started, lastUpdate, ended;
	private String startCause;
	private String stopCause;
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getModelId() {
		return modelId;
	}
	
	public ArrayList<Pre6QuestCreatorFollowedObjectData> getFollowedPath() {
		return followedPath;
	}
	
	public long getStarted() {
		return started;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public long getEnded() {
		return ended;
	}
	
	public String getStartCause() {
		return startCause;
	}
	
	public String getStopCause() {
		return stopCause;
	}

}
