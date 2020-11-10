package com.guillaumevdn.gcore.lib.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerSpawnedEntityEvent extends PlayerEvent {

	private LivingEntity entity;

	public PlayerSpawnedEntityEvent(Player player, LivingEntity entity) {
		super(player);
		this.entity = entity;
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
