package com.guillaumevdn.gcore.integration.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyUtils {

	boolean init() throws Throwable;
	double get(OfflinePlayer player);
	void give(OfflinePlayer player, double amount);
	void take(OfflinePlayer player, double amount);
	String format(double amount);

}
