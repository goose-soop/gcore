package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerKillEntityEvent extends Event {

	private Player player;
	private EntityDeathEvent event;
	private Entity killed;

	public PlayerKillEntityEvent(EntityDeathEvent event, Player player, Entity killed) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.event = event;
		this.killed = killed;
	}

	// get
	public Player getPlayer() {
		return player;
	}
	
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
