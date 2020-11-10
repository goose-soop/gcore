package com.guillaumevdn.gcore.lib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.Permission;

/**
 * @author GuillaumeVDN
 */
public abstract class UsageRestriction {

	private final boolean playerOnly;
	private final Permission permission;

	public UsageRestriction(boolean playerOnly, Permission permission) {
		this.playerOnly = playerOnly;
		this.permission = permission;
	}

	// get
	public final boolean isPlayerOnly() {
		return playerOnly;
	}

	public final Permission getPermission() {
		return permission;
	}

	public final boolean canUse(CommandSender sender) {
		return isCorrectSender(sender) && hasPermission(sender);
	}

	public boolean hasPermission(CommandSender sender) {
		return permission == null || permission.has(sender);
	}

	public boolean isCorrectSender(CommandSender sender) {
		return !playerOnly || ObjectUtils.instanceOf(sender, Player.class);
	}

	// do
	public final boolean validateUse(CommandSender sender) {
		if (!isCorrectSender(sender)) {
			TextGeneric.messageCommandNotPlayer.send(sender);
			return false;
		}
		if (!hasPermission(sender)) {
			TextGeneric.messageNoPermission.send(sender);
			return false;
		}
		return true;
	}

}
