package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.Location;
import org.bukkit.event.HandlerList;

import be.pyrrh4.pyrcore.lib.npc.Npc;

public class NpcMoveEvent extends NpcEvent {

	// base
	private Location previous;

	public NpcMoveEvent(Npc npc, Location previous) {
		super(npc);
		this.previous = previous;
	}

	// get
	public Location getPrevious() {
		return previous;
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
