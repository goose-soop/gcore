package com.guillaumevdn.gcore.lib.economy;

import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class CurrencyPlayerPoints extends Currency {

	private ReflectionObject api;

	public CurrencyPlayerPoints(String id) {
		super(id, "PlayerPoints");
	}

	// ----- initialization
	@Override
	protected boolean initialize() throws Throwable {
		api = ReflectionObject.of(PluginUtils.getPlugin("PlayerPoints")).invokeMethod("getAPI");
		return api.justGet() != null;
	}

	// ----- methods
	@Override
	protected boolean doGive(OfflinePlayer player, double amount) throws Throwable {
		return api.invokeMethod("give", player.getUniqueId(), (int) amount).get();
	}

	@Override
	protected boolean doTake(OfflinePlayer player, double amount) throws Throwable {
		return api.invokeMethod("take", player.getUniqueId(), (int) amount).get();
	}

	@Override
	protected double doGet(OfflinePlayer player) throws Throwable {
		return api.invokeMethod("look", player.getUniqueId()).get(Integer.class).intValue();
	}

}
