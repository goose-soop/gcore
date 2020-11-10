package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.PlayerInventoryState;

/**
 * @author GuillaumeVDN
 */
public class PlayerCraftedItemsEvent extends PlayerEvent {

	private CraftItemEvent event;
	private PlayerInventoryState inventoryBefore, inventoryAfter;
	private Map<Integer, ItemStack> cost, crafted;

	public PlayerCraftedItemsEvent(CraftItemEvent event, PlayerInventoryState inventoryBefore, PlayerInventoryState inventoryAfter, Map<Integer, ItemStack> cost, Map<Integer, ItemStack> crafted) {
		super((Player) event.getWhoClicked());
		this.inventoryBefore = inventoryBefore;
		this.inventoryAfter = inventoryAfter;
		this.event = event;
		this.cost = cost;
		this.crafted = crafted;
	}

	// get
	public CraftItemEvent getEvent() {
		return event;
	}

	public PlayerInventoryState getInventoryBefore() {
		return inventoryBefore;
	}

	public PlayerInventoryState getInventoryAfter() {
		return inventoryAfter;
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
