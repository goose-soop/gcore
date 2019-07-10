package com.guillaumevdn.gcore.integration.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyUtils {

	public boolean init() throws Throwable;
	public double get(OfflinePlayer player);
	public void give(OfflinePlayer player, double amount);
	public void take(OfflinePlayer player, double amount);
	public String format(double amount);

}
