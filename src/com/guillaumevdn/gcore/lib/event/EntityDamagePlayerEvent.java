package com.guillaumevdn.gcore.lib.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author GuillaumeVDN
 */
public class EntityDamagePlayerEvent extends Event implements Cancellable {

	private EntityDamageByEntityEvent event;
	private Entity damager;
	private Player damaged;

	public EntityDamagePlayerEvent(EntityDamageByEntityEvent event, Entity damager, Player damaged) {
		this.event = event;
		this.damager = damager;
		this.damaged = damaged;
	}

	// get
	public EntityDamageByEntityEvent getEvent() {
		return event;
	}

	public Entity getDamager() {
		return damager;
	}

	public Player getDamaged() {
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
