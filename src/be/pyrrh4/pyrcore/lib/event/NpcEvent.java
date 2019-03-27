package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import be.pyrrh4.pyrcore.lib.npc.Npc;

public class NpcEvent extends PlayerEvent {

	// base
	private Npc npc;

	public NpcEvent(Npc npc) {
		super(npc.getPlayer());
	}

	// get
	public Npc getNpc() {
		return npc;
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
