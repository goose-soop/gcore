package com.guillaumevdn.gcore.integration.permission;

import org.bukkit.OfflinePlayer;

public interface PermissionUtils {

	public boolean init() throws Throwable;
	public boolean has(OfflinePlayer player, String permission);

}
