package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.PlayerInventoryState;

/**
 * @author GuillaumeVDN
 */
public class PlayerTradedVillagerEvent extends PlayerEvent {

	private Villager villager;
	private InventoryClickEvent event;
	private PlayerInventoryState inventoryBefore, inventoryAfter;
	private Map<Integer, ItemStack> given, received;

	public PlayerTradedVillagerEvent(Villager villager, InventoryClickEvent event, PlayerInventoryState inventoryBefore, PlayerInventoryState inventoryAfter, Map<Integer, ItemStack> given, Map<Integer, ItemStack> received) {
		super((Player) event.getWhoClicked());
		this.inventoryBefore = inventoryBefore;
		this.inventoryAfter = inventoryAfter;
		this.given = given;
		this.received = received;
	}

	// get
	public Villager getVillager() {
		return villager;
	}

	public InventoryClickEvent getEvent() {
		return event;
	}

	public PlayerInventoryState getInventoryBefore() {
		return inventoryBefore;
	}

	public PlayerInventoryState getInventoryAfter() {
		return inventoryAfter;
	}

	public Map<Integer, ItemStack> getGiven() {
		return given;
	}

	public Map<Integer, ItemStack> getReceived() {
		return received;
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
