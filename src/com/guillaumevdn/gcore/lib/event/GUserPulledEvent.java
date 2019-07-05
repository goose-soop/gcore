package com.guillaumevdn.gcore.lib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.guillaumevdn.gcore.data.GUser;

/**
 * Triggered when a GUser finished loading
 */
public class GUserPulledEvent extends Event {

	// base
	private GUser user;

	public GUserPulledEvent(GUser user) {
		this.user = user;
	}

	// get
	public GUser getUser() {
		return user;
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
