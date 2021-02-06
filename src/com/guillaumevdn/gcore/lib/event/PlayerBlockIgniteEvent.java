package com.guillaumevdn.gcore.lib.event;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockIgniteEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerBlockIgniteEvent extends Event implements Cancellable {

	private BlockIgniteEvent event;
	private Block onFire;

	public PlayerBlockIgniteEvent(BlockIgniteEvent event, Block onFire) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;
		this.onFire = onFire;
	}

	// get
	public Player getPlayer() {
		return event.getPlayer();
	}

	public BlockIgniteEvent getEvent() {
		return event;
	}

	public Block getOnFire() {
		return onFire;
	}

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	// cancellable
	private boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
