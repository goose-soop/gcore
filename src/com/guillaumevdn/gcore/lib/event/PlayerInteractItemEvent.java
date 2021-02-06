package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.player.PhysicalClickType;

/**
 * @author GuillaumeVDN
 */
public class PlayerInteractItemEvent extends Event implements Cancellable {

	private PlayerInteractEvent event;
	private ItemStack interacted;
	private PhysicalClickType clickType;

	public PlayerInteractItemEvent(PlayerInteractEvent event, ItemStack interacted, PhysicalClickType clickType) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.interacted = interacted;
		this.clickType = clickType;
	}

	// get
	public Player getPlayer() {
		return event.getPlayer();
	}

	public PlayerInteractEvent getEvent() {
		return event;
	}

	public ItemStack getInteracted() {
		return interacted;
	}

	public PhysicalClickType getClickType() {
		return clickType;
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
