package be.pyrrh4.pyrcore.convert.v6;

public class Pre6QuestCreatorQuestJournalEntry {

	// base
	private String id, title, detail;

	public Pre6QuestCreatorQuestJournalEntry(String id, String title, String detail) {
		this.id = id;
		this.title = title;
		this.detail = detail;
	}

	// methods
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
