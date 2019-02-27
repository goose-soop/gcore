package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCowMilkEvent extends PlayerEvent implements Cancellable {

	// base
	private Cow cow;

	public PlayerCowMilkEvent(Player player, Cow cow) {
		super(player);
		this.cow = cow;
	}

	public Cow getCow() {
		return cow;
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

	// handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
