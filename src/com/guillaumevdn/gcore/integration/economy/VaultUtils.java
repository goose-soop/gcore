package com.guillaumevdn.gcore.integration.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;

public class VaultUtils implements EconomyUtils {

	// methods
	private Economy economy;

	@Override
	public boolean init() throws Throwable {
		// get economy instance
		economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
		return true;
	}

	// methods
	@Override
	public void give(OfflinePlayer player, double amount) {
		economy.depositPlayer(player, amount);
	}

	@Override
	public void take(OfflinePlayer player, double amount) {
		economy.withdrawPlayer(player, amount);
	}

	@Override
	public double get(OfflinePlayer player) {
		return economy.getBalance(player);
	}

	@Override
	public String format(double amount) {
		return economy.format(amount);
	}

}
