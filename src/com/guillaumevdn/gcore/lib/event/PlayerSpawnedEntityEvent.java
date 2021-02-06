package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author GuillaumeVDN
 */
public class PlayerSpawnedEntityEvent extends Event {

	private Player player;
	private LivingEntity entity;

	public PlayerSpawnedEntityEvent(Player player, LivingEntity entity) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.entity = entity;
	}
	
	public Player getPlayer() {
		return player;
	}

	public LivingEntity getEntity() {
		return entity;
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
