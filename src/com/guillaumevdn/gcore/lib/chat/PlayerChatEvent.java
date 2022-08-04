package com.guillaumevdn.gcore.lib.chat;

import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * Represents a chat event, compatible with vanilla and other integrated chat plugins
 * TODO : integrate more chat plugins
 * @author GuillaumeVDN
 */
public class PlayerChatEvent extends Event implements Cancellable {

	private Player sender;
	private int commandSlashCount;
	private String message;
	private Set<? extends CommandSender> recipients;
	private boolean cancelled = false;
	private int changesToOG = 0;

	private PlayerChatEvent(Player sender, int commandSlashCount, String message, Set<? extends CommandSender> recipients) {
		super(!Bukkit.isPrimaryThread());
		this.sender = sender;
		this.commandSlashCount = commandSlashCount;
		this.message = message;
		this.recipients = recipients;
	}

	// ----- get
	public Player getPlayer() {
		return sender;
	}

	public boolean isCommand() {
		return commandSlashCount > 0;
	}

	public int getCommandSlashCount() {
		return commandSlashCount;
	}

	public int getChangesToOG() {
		return changesToOG;
	}

	/**
	 * If this is a command, there'll be no / in front
	 */
	public String getMessage() {
		return message;
	}

	public Set<? extends CommandSender> getRecipients() {
		return recipients;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public boolean match(String configString, boolean startsWith) {
		if (configString == null) return false;
		int configSlashCount = StringUtils.countLeadingChar(configString, '/');
		if (configSlashCount != commandSlashCount) {
			return false;
		}
		configString = configString.substring(configSlashCount);
		return startsWith ? configString.equalsIgnoreCase(message) : message.toLowerCase().startsWith(configString.toLowerCase());
	}

	// ----- set
	public void setMessage(String message) {
		if (!Objects.equals(message, this.message)) {
			++this.changesToOG;
		}
		this.message = message;
	}

	public void setCommandSlashCount(int commandSlashCount) {
		if (!isCommand()) {
			throw new IllegalStateException("can't modify slash count of a non-command chat event");
		}
		if (commandSlashCount <= 0) {
			throw new IllegalArgumentException("invalid command slash count " + commandSlashCount);
		}
		if (commandSlashCount != this.commandSlashCount) {
			++this.changesToOG;
		}
		this.commandSlashCount = commandSlashCount;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		if (cancelled != this.cancelled) {
			++this.changesToOG;
		}
		this.cancelled = cancelled;
	}

	// ----- handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	// ----- static
	public static PlayerChatEvent call(Player sender, String message, Set<? extends CommandSender> recipients) {
		return call(sender, 0, message, recipients);
	}

	public static PlayerChatEvent call(Player sender, int commandSlashCount, String message, Set<? extends CommandSender> recipients) {
		PlayerChatEvent event = new PlayerChatEvent(sender, commandSlashCount, message, recipients);
		Bukkit.getPluginManager().callEvent(event);
		return event;
	}

}
