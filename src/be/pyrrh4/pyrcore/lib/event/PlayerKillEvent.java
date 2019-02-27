package be.pyrrh4.pyrcore.lib.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKillEvent extends PlayerEvent {

	// base
	private Player killed;

	public PlayerKillEvent(Player player, Player killed) {
		super(player);
		this.killed = killed;
	}

	// get
	public Player getKilled() {
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
