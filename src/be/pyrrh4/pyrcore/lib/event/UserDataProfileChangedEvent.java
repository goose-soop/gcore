package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import be.pyrrh4.pyrcore.data.PCUser;

/**
 * Triggered when the data profile of an user was changed
 */
public class UserDataProfileChangedEvent extends Event {

	// base
	private PCUser user;
	private String previousProfile;

	public UserDataProfileChangedEvent(PCUser user, String previousProfile) {
		this.user = user;
		this.previousProfile = previousProfile;
	}

	public PCUser getUser() {
		return user;
	}

	public String getPreviousProfile() {
		return previousProfile;
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
