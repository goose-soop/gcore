package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerFishEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerItemFishEvent extends Event implements Cancellable {

	private PlayerFishEvent event;
	private Item caught;

	public PlayerItemFishEvent(PlayerFishEvent event, Item caught) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.caught = caught;
	}

	// get
	public Player getPlayer() {
		return event.getPlayer();
	}

	public PlayerFishEvent getEvent() {
		return event;
	}

	public Item getCaught() {
		return caught;
	}

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	// cancellable
	private boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
