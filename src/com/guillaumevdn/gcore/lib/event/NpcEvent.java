package com.guillaumevdn.gcore.lib.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.guillaumevdn.gcore.lib.npc.Npc;

public class NpcEvent extends PlayerEvent {

	// base
	private Npc npc;

	public NpcEvent(Npc npc) {
		super(npc.getPlayer());
		this.npc = npc;
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
