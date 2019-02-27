package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.player.PlayerEvent;

public class PlayerFireBlockEvent extends PlayerEvent implements Cancellable {

	// base
	private Block block, fireBlock;
	private IgniteCause cause;

	public PlayerFireBlockEvent(Player player, Block block, Block fireBlock, IgniteCause cause) {
		super(player);
		this.block = block;
		this.fireBlock = fireBlock;
		this.cause = cause;
	}

	public Block getBlock() {
		return block;
	}

	public Block getFireBlock() {
		return fireBlock;
	}

	public IgniteCause getCause() {
		return cause;
	}

	// cancellable
	private boolean cancelled = false;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
