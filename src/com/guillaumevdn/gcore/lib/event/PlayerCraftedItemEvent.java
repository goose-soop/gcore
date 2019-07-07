package com.guillaumevdn.gcore.lib.event;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerCraftedItemEvent extends PlayerEvent {

	// base
	private CraftItemEvent event;
	private Map<Integer, ItemStack> crafted;

	public PlayerCraftedItemEvent(CraftItemEvent event, Map<Integer, ItemStack> crafted) {
		super((Player) event.getWhoClicked());
		this.event = event;
		this.crafted = crafted;
	}

	public CraftItemEvent getCraftEvent() {
		return event;
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
