package com.guillaumevdn.gcore.lib.economy;

import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class CurrencyTokenEnchant extends Currency {

	private ReflectionObject plugin;

	public CurrencyTokenEnchant(String id) {
		super(id, "TokenEnchant");
	}

	// ----- initialization
	@Override
	protected boolean initialize() throws Throwable {
		plugin = ReflectionObject.of(PluginUtils.getPlugin("PlayerPoints"));
		return true;
	}

	// ----- methods
	@Override
	protected boolean doGive(OfflinePlayer player, double amount) throws Throwable {
		plugin.invokeMethod("addTokens", player, (int) amount).get();
		return true;
	}

	@Override
	protected boolean doTake(OfflinePlayer player, double amount) throws Throwable {
		plugin.invokeMethod("removeTokens", player, (int) amount).get();
		return true;
	}

	@Override
	protected double doGet(OfflinePlayer player) throws Throwable {
		return plugin.invokeMethod("getTokens", player).get(Integer.class).intValue();
	}

}
