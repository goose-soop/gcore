package com.guillaumevdn.gcore;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.lib.integration.PluginIntegration;

import net.milkbowl.vault.economy.Economy;

public class VaultIntegration extends PluginIntegration {

	// base
	public VaultIntegration(String pluginName) {
		super(pluginName);
	}

	// override
	private Economy vault;

	@Override
	public void enable() {
		// get economy instance
		vault = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
		// register
		GCore.inst().setVaultIntegration(this);
	}

	@Override
	public void disable() {
		// unregister instance
		vault = null;
		// unregister
		GCore.inst().setVaultIntegration(null);
	}

	// methods
	public void add(OfflinePlayer player, double amount) {
		vault.depositPlayer(player, amount);
	}

	private static BigDecimal maxDouble = new BigDecimal(Double.MAX_VALUE);
	public void add(OfflinePlayer player, BigDecimal amount) {
		while (amount.compareTo(BigDecimal.ZERO) > 0) {
			if (amount.compareTo(maxDouble) > 1) {
				add(player, maxDouble.doubleValue());
				amount = amount.subtract(maxDouble);
			} else {
				add(player, amount.doubleValue());
				amount = BigDecimal.ZERO;
			}
		}
	}

	public void take(OfflinePlayer player, double amount) {
		vault.withdrawPlayer(player, amount);
	}

	public void take(OfflinePlayer player, BigDecimal amount) {
		while (amount.compareTo(BigDecimal.ZERO) > 0) {
			if (amount.compareTo(maxDouble) > 1) {
				take(player, maxDouble.doubleValue());
				amount = amount.subtract(maxDouble);
			} else {
				take(player, amount.doubleValue());
				amount = BigDecimal.ZERO;
			}
		}
	}

	public double get(OfflinePlayer player) {
		return vault.getBalance(player);
	}

	public String format(double amount) {
		return vault.format(amount);
	}

}
