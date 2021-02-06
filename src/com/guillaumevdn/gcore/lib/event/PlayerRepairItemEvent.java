package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class PlayerRepairItemEvent extends Event {

	private InventoryClickEvent event;
	private ItemStack cost1, cost2;
	private ItemStack item;

	public PlayerRepairItemEvent(InventoryClickEvent event, ItemStack cost1, ItemStack cost2, ItemStack item) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.cost1 = cost1;
		this.cost2 = cost2;
		this.item = item;
	}

	// get
	public Player getPlayer() {
		return (Player) event.getWhoClicked();
	}

	public InventoryClickEvent getEvent() {
		return event;
	}

	public ItemStack getRepairedItem() {
		return item;
	}

	public ItemStack getCost1() {
		return cost1;
	}

	public ItemStack getCost2() {
		return cost2;
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
