package com.guillaumevdn.gcore.integration.permission;

import java.util.Map;

import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PermissionHandler {

	// base
	private static final Map<String, String> PLUGINS = Utils.asMap("Vault", "VaultUtils");
	private PermissionUtils utils = null;

	// get
	public PermissionUtils getUtils() {
		return utils;
	}

	public boolean isHandled() {
		return utils != null;
	}

	public boolean isPluginHandled(String plugin) {
		return PLUGINS.containsKey(plugin);
	}

	// methods
	public boolean init() {
		// check for handled plugin
		for (String plugin : PLUGINS.keySet()) {
			try {
				if (Utils.isPluginEnabled(plugin)) {
					PermissionUtils utils = (PermissionUtils) Class.forName("com.guillaumevdn.gcore.integration.permission." + PLUGINS.get(plugin)).newInstance();
					if (utils.init()) {
						this.utils = utils;
						break;
					}
				}
			} catch (Throwable ignored) {}
		}
		// return
		return utils != null;
	}

	public boolean has(OfflinePlayer player, String permission) {
		if (utils == null) {
			GCore.inst().warning("Trying to check permission " + permission + " for player " + player.getName() + " but no permission provided was found");
			return false;
		} else {
			return utils.has(player, permission);
		}
	}

}
