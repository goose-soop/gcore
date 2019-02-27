package be.pyrrh4.pyrcore.convert.v6;

public class V6QuestCreatorFollowedObjectData {

	private String branchId;
	private String objectId;
	private String additionalValue;
	private long when;

	public V6QuestCreatorFollowedObjectData(Pre6QuestCreatorFollowedObjectData object) {
		this.branchId = object.getBranchId();
		this.objectId = object.getObjectId();
		this.additionalValue = object.getAdditionalValue();
		this.when = object.getWhen();
	}

	public String getBranchId() {
		return branchId;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getAdditionalValue() {
		return additionalValue;
	}

	public long getWhen() {
		return when;
	}

}
