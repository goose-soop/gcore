package be.guillaumevdn.gcore.lib.event;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerEvent;

import be.guillaumevdn.gcore.lib.material.Mat;

public class PlayerBlockDropEvent extends PlayerEvent implements Cancellable {

	// base
	private Item item;
	private BlockBreakEvent event;
	private Mat type;

	public PlayerBlockDropEvent(Player player, Item item, BlockBreakEvent event, Mat type) {
		super(player);
		this.item = item;
		this.event = event;
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public BlockBreakEvent getBreakEvent() {
		return event;
	}

	public Mat getType() {
		return type;
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
