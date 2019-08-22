package com.guillaumevdn.gcore.lib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Triggered when the online GUsers finished loading due to the user board pullOnline() method
 */
public class GUsersPulledOnlineEvent extends Event {

	// base
	public GUsersPulledOnlineEvent(boolean async) {
		super(async);
	}

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	// context
	public static enum Context {

		JOIN,
		PROFILE_CHANGE,
		USER_OPERATOR,
		PULL_ONLINE

	}

}
