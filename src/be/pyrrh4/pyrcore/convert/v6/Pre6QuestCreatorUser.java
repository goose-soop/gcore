package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Pre6QuestCreatorUser {

	private LinkedList<Pre6QuestCreatorQuestHistoryElement> questHistory = new LinkedList<Pre6QuestCreatorQuestHistoryElement>();
	private HashMap<String, Long> questCooldownEnd = new HashMap<String, Long>();
	private HashMap<String, String> variables = new HashMap<String, String>();
	private ArrayList<Pre6QuestCreatorQuestJournalEntry> questJournal = new ArrayList<Pre6QuestCreatorQuestJournalEntry>();
	private boolean questJournalToggle = false;
	
	public LinkedList<Pre6QuestCreatorQuestHistoryElement> getQuestHistory() {
		return questHistory;
	}
	
	public HashMap<String, Long> getQuestCooldownEnd() {
		return questCooldownEnd;
	}
	
	public HashMap<String, String> getVariables() {
		return variables;
	}
	
	public ArrayList<Pre6QuestCreatorQuestJournalEntry> getQuestJournal() {
		return questJournal;
	}
	
	public boolean isQuestJournalToggle() {
		return questJournalToggle;
	}

}
