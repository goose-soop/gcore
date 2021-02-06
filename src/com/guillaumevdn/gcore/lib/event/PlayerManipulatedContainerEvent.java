package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.InventoryState;

/**
 * @author GuillaumeVDN
 */
public class PlayerManipulatedContainerEvent extends Event {

	private InventoryClickEvent event;
	private InventoryState containerBefore, containerAfter;
	private Map<Integer, ItemStack> removed, added;

	public PlayerManipulatedContainerEvent(InventoryClickEvent event, InventoryState containerBefore, InventoryState containerAfter, Map<Integer, ItemStack> removed, Map<Integer, ItemStack> added) {
		super(!Bukkit.isPrimaryThread());
		this.containerBefore = containerBefore;
		this.containerAfter = containerAfter;
		this.event = event;
		this.removed = removed;
		this.added = added;
	}

	// get
	public Player getPlayer() {
		return (Player) event.getWhoClicked();
	}

	public InventoryClickEvent getEvent() {
		return event;
	}

	public InventoryState getContainerBefore() {
		return containerBefore;
	}

	public InventoryState getContainerAfter() {
		return containerAfter;
	}

	public Map<Integer, ItemStack> getRemoved() {
		return removed;
	}

	public Map<Integer, ItemStack> getAdded() {
		return added;
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
