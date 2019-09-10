package com.guillaumevdn.gcore.integration.permission;

import org.bukkit.OfflinePlayer;

public interface PermissionUtils {

	boolean init() throws Throwable;
	boolean has(OfflinePlayer player, String permission);

}
