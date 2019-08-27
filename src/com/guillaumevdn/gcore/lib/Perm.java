package com.guillaumevdn.gcore.lib;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.UserInfo;

public class Perm {

	// base
	private Perm parent;
	private String name;

	public Perm(Perm parent, String name) {
		this(parent, name, true);
	}

	public Perm(Perm parent, String name, boolean log) {
		this.parent = parent;
		this.name = name;
		if (log && !name.isEmpty()) {
			//GCore.inst().debug("Loaded permission " + name);
		}
	}

	// get
	public Perm getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	// methods
	public boolean has(Object permissible) {
		if (permissible instanceof CommandSender) {
			return hasSender((CommandSender) permissible);
		} else if (permissible instanceof OfflinePlayer) {
			return hasPlayer((OfflinePlayer) permissible);
		} else if (permissible instanceof UserInfo) {
			return hasPlayer(((UserInfo) permissible).toOfflinePlayer());
		}
		return false;
	}

	private boolean hasSender(CommandSender sender) {
		return sender != null && (sender.isOp() || sender.hasPermission(name) || (parent == null ? false : parent.hasSender(sender)));
	}

	private boolean hasPlayer(OfflinePlayer player) {
		return player != null && (player.isOnline() ? hasSender(player.getPlayer()) : (player.isOp() || GCore.inst().getPermissionHandler().has(player, name) || (parent == null ? false : parent.hasPlayer(player))));
	}

}
