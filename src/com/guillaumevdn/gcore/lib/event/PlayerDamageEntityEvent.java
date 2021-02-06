package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerDamageEntityEvent extends Event implements Cancellable {

	private EntityDamageByEntityEvent event;
	private Player damager;
	private Entity damaged;

	public PlayerDamageEntityEvent(EntityDamageByEntityEvent event, Player damager, Entity damaged) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.damager = damager;
		this.damaged = damaged;
	}

	// get
	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	public Player getDamager() {
		return damager;
	}

	public Entity getDamaged() {
		return damaged;
	}

	// cancellable
	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		event.setCancelled(cancelled);
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
