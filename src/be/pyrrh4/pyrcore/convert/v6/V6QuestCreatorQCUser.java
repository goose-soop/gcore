package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class V6QuestCreatorQCUser {

	private List<V6QuestCreatorQuestHistoryElement> questHistory;
	private Map<String, Long> questCooldownEnd;
	private Map<String, String> variables;
	private List<V6QuestCreatorJournalEntry> journal;
	private boolean journalToggle;

	public V6QuestCreatorQCUser(Pre6QuestCreatorUser user) {
		this.questHistory = new ArrayList<V6QuestCreatorQuestHistoryElement>();
		for (Pre6QuestCreatorQuestHistoryElement element : user.getQuestHistory()) {
			questHistory.add(new V6QuestCreatorQuestHistoryElement(element));
		}
		this.questCooldownEnd = user.getQuestCooldownEnd();
		this.variables = user.getVariables();
		this.journal = new ArrayList<V6QuestCreatorJournalEntry>();
		for (Pre6QuestCreatorQuestJournalEntry entry : user.getQuestJournal()) {
			journal.add(new V6QuestCreatorJournalEntry(entry));
		}
		this.journalToggle = user.isQuestJournalToggle();
	}

}
