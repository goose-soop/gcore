package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Potion, exp bottles, ...
 * @author GuillaumeVDN
 */
public class PlayerItemThrowEvent extends PlayerEvent implements Cancellable {

	private ItemStack item;
	private Location location;

	public PlayerItemThrowEvent(Player player, ItemStack item, Location location) {
		super(player);
		this.item = item;
		this.location = location;
	}

	// get
	public ItemStack getItem() {
		return item;
	}

	public Location getLocation() {
		return location;
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

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
