package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class PlayerCraftedItemsEvent extends Event {

	private InventoryClickEvent event;
	private Map<Integer, ItemStack> cost, crafted;

	public PlayerCraftedItemsEvent(InventoryClickEvent event, Map<Integer, ItemStack> cost, Map<Integer, ItemStack> crafted) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.cost = cost;
		this.crafted = crafted;
	}

	// get
	public Player getPlayer() {
		return (Player) event.getWhoClicked();
	}

	public InventoryClickEvent getEvent() {
		return event;
	}

	public Map<Integer, ItemStack> getCost() {
		return cost;
	}

	public Map<Integer, ItemStack> getCrafted() {
		return crafted;
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
