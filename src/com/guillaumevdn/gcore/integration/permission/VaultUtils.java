package com.guillaumevdn.gcore.integration.permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.permission.Permission;

public class VaultUtils implements PermissionUtils {

	// methods
	private Permission permissions;

	@Override
	public boolean init() throws Throwable {
		// get permission instance
		permissions = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
		return true;
	}

	// methods
	@Override
	public boolean has(OfflinePlayer player, String permission) {
		return permissions.playerHas(null, player, permission);
	}

}
