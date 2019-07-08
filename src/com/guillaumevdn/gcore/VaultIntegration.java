package com.guillaumevdn.gcore;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.lib.integration.PluginIntegration;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultIntegration extends PluginIntegration {

	// base
	public VaultIntegration(String pluginName) {
		super(pluginName);
	}

	// override
	private Economy economy;
	private Permission permission;

	@Override
	public void enable() {
		// get economy instance
		try {
			economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
		} catch (Throwable ignored) {
			GCore.inst().error("No economy manager found for Vault");
		}
		// get permission instance
		try {
			permission = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
		} catch (Throwable ignored) {
			GCore.inst().error("No permission manager found for Vault");
		}
		// register
		GCore.inst().setVaultIntegration(this);
	}

	@Override
	public void disable() {
		// unregister instance
		economy = null;
		permission = null;
		// unregister
		GCore.inst().setVaultIntegration(null);
	}
	
	// methods
	public boolean hasEconomy() {
		return economy != null;
	}
	
	public boolean hasPermissions() {
		return permission != null;
	}
	
	public void add(OfflinePlayer player, double amount) {
		economy.depositPlayer(player, amount);
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
		economy.withdrawPlayer(player, amount);
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
		return economy.getBalance(player);
	}

	public String format(double amount) {
		return economy.format(amount);
	}

	public boolean hasOfflinePermission(OfflinePlayer player, String permission) {
		return this.permission.playerHas(null, player, permission);
	}

}
