package com.guillaumevdn.gcore.lib.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerKillEntityEvent extends PlayerEvent {

	private EntityDeathEvent event;
	private Entity killed;

	public PlayerKillEntityEvent(EntityDeathEvent event, Player player, Entity killed) {
		super(player);
		this.event = event;
		this.killed = killed;
	}

	// get
	public EntityDeathEvent getEvent() {
		return event;
	}

	public Entity getKilled() {
		return killed;
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
