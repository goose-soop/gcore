package com.guillaumevdn.gcore.lib.event;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

/**
 * @author GuillaumeVDN
 */
public class PlayerBrewPotionEvent extends Event {

	private BrewEvent event;
	private UUID player;
	private BrewingStand stand;
	private int slot;
	private ItemStack brew;
	private Potion brewPotion;

	public PlayerBrewPotionEvent(BrewEvent event, UUID player, BrewingStand stand, int slot, ItemStack brew) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.player = player;
		this.stand = stand;
		this.slot = slot;
		this.brew = brew;
		try {
			this.brewPotion = Potion.fromItemStack(brew);
		} catch (IllegalArgumentException ignored) {  // not a potion, happens somehow
			this.brewPotion = null;
		}
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

	public int getSlot() {
		return slot;
	}

	public ItemStack getBrew() {
		return brew;
	}

	public Potion getBrewPotion() {
		return brewPotion;
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
