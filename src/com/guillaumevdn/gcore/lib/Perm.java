package com.guillaumevdn.gcore.lib;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.guillaumevdn.gcore.GCore;

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
	public boolean has(CommandSender sender) {
		return sender.isOp() || sender.hasPermission(name) || (parent == null ? false : parent.has(sender));
	}

	public boolean hasOffline(OfflinePlayer player) {
		if (player == null) return false;
		if (player.isOnline()) return has((CommandSender) player);
		if (player.isOp()) return true;
		if (GCore.inst().getVaultIntegration() == null || !GCore.inst().getVaultIntegration().hasPermissions()) return false;
		return GCore.inst().getVaultIntegration().hasOfflinePermission(player, name);
	}

}
