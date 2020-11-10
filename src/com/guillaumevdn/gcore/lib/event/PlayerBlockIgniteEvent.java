package com.guillaumevdn.gcore.lib.event;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerBlockIgniteEvent extends PlayerEvent implements Cancellable {

	private BlockIgniteEvent event;
	private Block onFire;

	public PlayerBlockIgniteEvent(BlockIgniteEvent event, Block onFire) {
		super(event.getPlayer());
		this.event = event;
		this.onFire = onFire;
	}

	// get
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
