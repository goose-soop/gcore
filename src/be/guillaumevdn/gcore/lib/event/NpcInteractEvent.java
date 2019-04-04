package be.guillaumevdn.gcore.lib.event;

import org.bukkit.event.HandlerList;

import be.guillaumevdn.gcore.lib.npc.Npc;

public class NpcInteractEvent extends NpcEvent {

	// base
	public NpcInteractEvent(Npc npc) {
		super(npc);
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
