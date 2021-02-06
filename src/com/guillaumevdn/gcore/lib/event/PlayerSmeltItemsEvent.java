package com.guillaumevdn.gcore.lib.event;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.InventoryState;

/**
 * @author GuillaumeVDN
 */
public class PlayerSmeltItemsEvent extends Event {

	private FurnaceSmeltEvent event;
	private UUID player;
	private Furnace furnace;
	private InventoryState inventoryBefore, inventoryAfter;
	private Map<Integer, ItemStack> cost, smelt;

	public PlayerSmeltItemsEvent(FurnaceSmeltEvent event, UUID player, Furnace furnace, InventoryState inventoryBefore, InventoryState inventoryAfter, Map<Integer, ItemStack> cost, Map<Integer, ItemStack> smelt) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.player = player;
		this.furnace = furnace;
		this.inventoryBefore = inventoryBefore;
		this.inventoryAfter = inventoryAfter;
		this.cost = cost;
		this.smelt = smelt;
	}

	// get
	public FurnaceSmeltEvent getEvent() {
		return event;
	}

	public UUID getPlayer() {
		return player;
	}

	public Player getPlayerOnline() {
		return Bukkit.getPlayer(player);
	}

	public Furnace getFurnace() {
		return furnace;
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

	public Map<Integer, ItemStack> getSmelt() {
		return smelt;
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
