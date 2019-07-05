package com.guillaumevdn.gcore.lib.npc.behavior;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BCondition;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent.AttemptContext;

public class RunningBehavior {

	// behavior id
	private String behaviorId;
	private String eventId;
	private List<String> currentProcess;
	private State state = State.WAITING;
	private int currentIndex = -1;
	private int ticksToWait = 0;

	public RunningBehavior(String behaviorId, String eventId, List<String> currentProcess) {
		this.behaviorId = behaviorId;
		this.eventId = eventId;
		this.currentProcess = currentProcess;
	}

	// get
	public State getState() {
		return state;
	}

	public String getBehaviorId() {
		return behaviorId;
	}

	public Behavior getBehavior() throws UnknownReferenceException {
		return getBehavior(behaviorId);
	}

	public Behavior getBehavior(String behaviorId) throws UnknownReferenceException {
		Behavior behavior = GCore.inst().getNpcManager().getBehavior(behaviorId);
		if (behavior == null) {
			state = State.DONE;
			throw new UnknownReferenceException("Couldn't find behavior with id " + behaviorId + ", stopping running behavior " + behaviorId);
		}
		return behavior;
	}

	public BAction getBehaviorAction(String actionId) throws UnknownReferenceException {
		BAction action = getBehavior().getActions().getElement(actionId);
		if (action == null) {
			state = State.DONE;
			throw new UnknownReferenceException("Couldn't find behavior action with id " + actionId + ", stopping running behavior " + behaviorId);
		}
		return action;
	}

	public BCondition getBehaviorCondition(String conditionId) throws UnknownReferenceException {
		BCondition condition = getBehavior().getConditions().getElement(conditionId);
		if (condition == null) {
			state = State.DONE;
			throw new UnknownReferenceException("Couldn't find behavior condition with id " + conditionId + ", stopping running behavior " + behaviorId);
		}
		return condition;
	}

	public String getEventId() {
		return eventId;
	}

	public List<String> getCurrentProcess() {
		return Collections.unmodifiableList(currentProcess);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	// methods
	/**
	 * Start the behavior
	 * @return true if the behavior was started and will progress soon (at the next behavior task, 1 tick delay) or false if the behavior wasn't in a waiting state
	 */
	public boolean start() {
		// invalid state
		if (!state.equals(State.WAITING)) {
			return false;
		}
		// start
		currentIndex = 0;
		state = State.RUNNING;
		return true;
	}

	/**
	 * Progress the behavior
	 * @param player the player
	 * @param npc the npc
	 * @return the result for the last progress call made by this method, or the direct result of this method call
	 */
	public ProgressResult progress(Player player, Npc npc) {
		// invalid state
		if (!state.equals(State.RUNNING)) {
			return ProgressResult.INVALID_STATE;
		}
		// wait
		if (ticksToWait > 0) {
			--ticksToWait;
			return ProgressResult.WAITING_TICKS;
		}
		// done
		if (currentIndex >= currentProcess.size()) {
			state = State.DONE;
			return ProgressResult.DONE;
		}
		// get next process instruction
		String next = currentProcess.get(currentIndex++);
		try {
			// decode and run instruction
			String[] params = next.split(" ");
			if (params[0].equalsIgnoreCase("ACTION")) {
				getBehaviorAction(params[1]).run(player, npc);
			} else if (params[0].equalsIgnoreCase("CONDITION")) {
				getBehaviorAction(params[getBehaviorCondition(params[1]).check(player, npc) ? 2 : 3]).run(player, npc);
			} else if (params[0].equalsIgnoreCase("BEHAVIOR")) {
				getBehavior(params[1]).runAttempt(AttemptContext.CALL, player, npc, null);
			} else if (params[0].equalsIgnoreCase("WAIT")) {
				ticksToWait = Integer.parseInt(params[1]);
			}
			// keep progressing
			return progress(player, npc);
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("An error occured while progressing npc behavior " + behaviorId + " (index " + currentIndex + ", instruction '" + next + "') (player " + player.getName() + ", npc " + npc.getId() + ")");
			return ProgressResult.ERROR;
		}
	}

	// state
	public static enum State {
		WAITING, RUNNING, DONE;
	}

	// progress result
	public static enum ProgressResult {

		// values
		WAITING_TICKS(true, false),
		PROGRESSED(true, false),
		DONE(true, true),
		INVALID_STATE(false, false),
		ERROR(true, true);

		// base
		private boolean mustSave, mustRemove;

		private ProgressResult(boolean mustSave, boolean mustRemove) {
			this.mustSave = mustSave;
			this.mustRemove = mustRemove;
		}

		// get
		public boolean getMustSave() {
			return mustSave;
		}

		public boolean getMustRemove() {
			return mustRemove;
		}

	}

}
