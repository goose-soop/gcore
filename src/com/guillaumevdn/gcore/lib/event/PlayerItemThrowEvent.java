package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Potion, exp bottles, ...
 * @author GuillaumeVDN
 */
public class PlayerItemThrowEvent extends Event implements Cancellable {

	private Player player;
	private ItemStack item;
	private Location location;

	public PlayerItemThrowEvent(Player player, ItemStack item, Location location) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.item = item;
		this.location = location;
	}

	// get
	public Player getPlayer() {
		return player;
	}
	
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
