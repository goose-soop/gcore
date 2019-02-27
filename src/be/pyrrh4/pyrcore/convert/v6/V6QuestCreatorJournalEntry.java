package be.pyrrh4.pyrcore.convert.v6;

public class V6QuestCreatorJournalEntry {

	private String id, title, detail;

	public V6QuestCreatorJournalEntry(Pre6QuestCreatorQuestJournalEntry entry) {
		this.id = entry.getId();
		this.title = entry.getTitle();
		this.detail = entry.getDetail();
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDetail() {
		return detail;
	}

}
