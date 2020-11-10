package com.guillaumevdn.gcore.lib.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import com.guillaumevdn.gcore.lib.gui.InventoryState;

/**
 * @author GuillaumeVDN
 */
public class PlayerBrewPotionsEvent extends Event {

	private BrewEvent event;
	private UUID player;
	private BrewingStand stand;
	private InventoryState inventoryBefore, inventoryAfter;
	private Map<Integer, ItemStack> cost, brew;
	private Map<Integer, Potion> brewPotions = new HashMap<>();

	public PlayerBrewPotionsEvent(BrewEvent event, UUID player, BrewingStand stand, InventoryState inventoryBefore, InventoryState inventoryAfter, Map<Integer, ItemStack> cost, Map<Integer, ItemStack> brew) {
		this.event = event;
		this.player = player;
		this.stand = stand;
		this.inventoryBefore = inventoryBefore;
		this.inventoryAfter = inventoryAfter;
		this.cost = cost;
		this.brew = brew;
		brew.forEach((slot, item) -> {
			try {
				brewPotions.put(slot, Potion.fromItemStack(item));
			} catch (IllegalArgumentException ignored) {}  // not a potion, happens somehow
		});
	}

	// get
	public BrewEvent getEvent() {
		return event;
	}

	public UUID getPlayer() {
		return player;
	}

	public Player getPlayerOnline() {
		return Bukkit.getPlayer(player);
	}

	public BrewingStand getStand() {
		return stand;
	}

	public InventoryState getInventoryBefore() {
		return inventoryBefore;
	}

	public InventoryState getInventoryAfter() {
		return inventoryAfter;
	}

	public Map<Integer, ItemStack> getCost() {
		return cost;
	}

	public Map<Integer, ItemStack> getBrew() {
		return brew;
	}

	public Map<Integer, Potion> getBrewPotions() {
		return brewPotions;
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
