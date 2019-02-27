package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class V6QuestCreatorQuestHistoryElement {

	private UUID uuid;
	private String modelId;
	private List<V6QuestCreatorFollowedObjectData> followedPath;
	private long started, lastUpdate, ended;
	private String startCause;
	private String stopCause;

	public V6QuestCreatorQuestHistoryElement(Pre6QuestCreatorQuestHistoryElement element) {
		this.uuid = element.getUniqueId();
		this.modelId = element.getModelId();
		this.followedPath = new ArrayList<V6QuestCreatorFollowedObjectData>();
		for (Pre6QuestCreatorFollowedObjectData object : element.getFollowedPath()) {
			followedPath.add(new V6QuestCreatorFollowedObjectData(object));
		}
		this.started = element.getStarted();
		this.lastUpdate = element.getLastUpdate();
		this.ended = element.getEnded();
		this.startCause = element.getStartCause();
		this.stopCause = element.getStopCause();
	}

}
