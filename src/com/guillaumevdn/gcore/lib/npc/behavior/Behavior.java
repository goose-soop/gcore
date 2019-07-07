package com.guillaumevdn.gcore.lib.npc.behavior;

import java.io.File;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.GUserNpcData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent.AttemptContext;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.list.LPBAction;
import com.guillaumevdn.gcore.lib.parseable.list.LPBCondition;
import com.guillaumevdn.gcore.lib.parseable.list.LPBEvent;
import com.guillaumevdn.gcore.lib.parseable.list.LPStringList;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;

/**
 * Represents the configuration and data of a behavior
 */
public class Behavior extends ContainerParseable {

	// base
	private File file;
	private LPBEvent events = new LPBEvent("events", this, true, 0, EditorGUI.ICON_TRIGGER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIOREVENTSLORE.getLines());
	private LPStringList processes = new LPStringList("processes", this, true, 1, EditorGUI.ICON_BRANCH, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORPROCESSESLORE.getLines());
	private LPBAction actions = new LPBAction("actions", this, true, 2, EditorGUI.ICON_ACTION, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORACTIONSLORE.getLines());
	private LPBCondition conditions = new LPBCondition("conditions", this, true, 3, EditorGUI.ICON_CONDITION, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORCONDITIONSLORE.getLines());
	private PPInteger maxConcurrentInstances = new PPInteger("max_concurrent_instances", this, "1", -1, Integer.MAX_VALUE, false, 4, EditorGUI.ICON_NUMBER, GLocale.GUI_GCORE_EDITOR_NPC_BEHAVIORMAXCONCURRENTINSTANCESLORE.getLines());

	public Behavior(String id, File file, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "npc behavior", mandatory, editorSlot, editorIcon, editorDescription);
		this.file = file;
	}

	// get
	public File getFile() {
		return file;
	}

	public LPBEvent getEvents() {
		return events;
	}

	public LPStringList getProcesses() {
		return processes;
	}

	public LPBAction getActions() {
		return actions;
	}

	public LPBCondition getConditions() {
		return conditions;
	}

	public PPInteger getMaxConcurrentInstances() {
		return maxConcurrentInstances;
	}

	public Integer getMaxConcurrentInstances(Player parser) {
		return maxConcurrentInstances.getParsedValue(parser);
	}

	// methods
	/**
	 * Attempt to run this behavior
	 * @param context the context in which the attempt is being made
	 * @param player the player
	 * @param npc the npc
	 * @param event the event to check (can be null depending on the context)
	 * @return a running behavior if the attempt was successful, or null if unsuccessful (no trigger found or maximum concurrent instances reached)
	 */
	public RunningBehavior runAttempt(AttemptContext context, Player player, Npc npc, Event event) {
		// check events
		for (BEvent bEvent : events.getElements().values()) {
			// triggered an event
			if (bEvent.triggerAttempt(context, player, npc, event)) {
				String processId = bEvent.getProcess(player);
				PPStringList process = processes.getElement(processId);
				List<String> currentProcess = process == null ? null : process.getParsedValue(player);
				// log error or run behavior
				if (currentProcess == null ) {
					GCore.inst().error("Triggered event " + bEvent.getId() + " for npc behavior " + getId() + " (player " + player.getName() + ") but couldn't find/parse associated process " + processId);
				} else {
					return run(player, npc, bEvent.getId(), currentProcess);
				}
			}
		}
		// none
		return null;
	}

	/**
	 * Run the behavior without event checks
	 * @param eventId the behavior event id that triggered this
	 * @param currentProcess the first process to start the behavior with
	 * @param player the player
	 * @param npc the npc
	 * @return a running behavior, or null if the maximum concurrent instances have been reached
	 */
	public RunningBehavior run(Player player, Npc npc, String eventId, List<String> currentProcess) {
		// max concurrent instances
		GUser user = GUser.get(player);
		GUserNpcData userNpc = user.getUserNpcData(npc.getId());
		int max = getMaxConcurrentInstances(player);
		if (max == 0) return null;
		if (max > 0) {
			int count = 0;
			for (RunningBehavior behavior : userNpc.getRunningBehaviors()) {
				if (behavior.getBehaviorId().equals(getId())) {
					++count;
				}
			}
			if (count >= max) {
				return null;
			}
		}
		// register behavior
		RunningBehavior behavior = new RunningBehavior(getId(), eventId, currentProcess);
		userNpc.getRunningBehaviors().add(behavior);
		user.updateNpc(npc.getId(), userNpc);
		// start it and return it
		behavior.start();
		return behavior;
	}

}
