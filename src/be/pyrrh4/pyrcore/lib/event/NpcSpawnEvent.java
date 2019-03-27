package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.event.HandlerList;

import be.pyrrh4.pyrcore.lib.npc.Npc;

public class NpcSpawnEvent extends NpcEvent {

	// base
	public NpcSpawnEvent(Npc npc) {
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
