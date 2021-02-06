package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class PlayerTradedVillagerEvent extends Event {

	private Villager villager;
	private InventoryClickEvent event;
	private Map<Integer, ItemStack> given, received;

	public PlayerTradedVillagerEvent(Villager villager, InventoryClickEvent event, Map<Integer, ItemStack> given, Map<Integer, ItemStack> received) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.given = given;
		this.received = received;
		this.villager = villager;
	}

	// get
	public Player getPlayer() {
		return (Player) event.getWhoClicked();
	}

	public Villager getVillager() {
		return villager;
	}

	public InventoryClickEvent getEvent() {
		return event;
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
