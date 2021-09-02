package com.guillaumevdn.gcore.lib.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 */
public class CurrencyVault extends Currency {

	private ReflectionObject economy;

	public CurrencyVault(String id) {
		super(id, "Vault");
	}

	// ----- initialization
	@Override
	protected boolean initialize() throws Throwable {
		Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
		RegisteredServiceProvider reg = Bukkit.getServicesManager().getRegistration(economyClass);
		economy = reg == null ? null : ReflectionObject.of(reg).invokeMethod("getProvider");
		return economy != null && economy.justGet() != null;
	}

	// ----- methods
	@Override
	protected boolean doGive(OfflinePlayer player, double amount) throws Throwable {
		ReflectionObject response = economy.invokeMethod("depositPlayer", player, amount);
		return response != null && response.invokeMethod("transactionSuccess").get(boolean.class);
	}

	@Override
	protected boolean doTake(OfflinePlayer player, double amount) throws Throwable {
		ReflectionObject response = economy.invokeMethod("withdrawPlayer", player, amount);
		return response != null && response.invokeMethod("transactionSuccess").get(boolean.class);
	}

	@Override
	protected double doGet(OfflinePlayer player) throws Throwable {
		ReflectionObject result = economy.invokeMethod("getBalance", player);
		return result.get(double.class);
	}

}
