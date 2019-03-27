package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.event.HandlerList;

import be.pyrrh4.pyrcore.lib.npc.Npc;
import be.pyrrh4.pyrcore.lib.npc.NpcAction;

public class NpcInteractEvent extends NpcEvent {

	// base
	private NpcAction action;

	public NpcInteractEvent(Npc npc, NpcAction action) {
		super(npc);
		this.action = action;
	}

	// get
	public NpcAction getAction() {
		return action;
	}

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
