package com.guillaumevdn.gcore.lib.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerSpawnedMobEvent extends PlayerEvent {

	// base
	private LivingEntity mob;

	public PlayerSpawnedMobEvent(Player player, LivingEntity mob) {
		super(player);
		this.mob = mob;
	}

	public LivingEntity getMob() {
		return mob;
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
