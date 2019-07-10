package com.guillaumevdn.gcore.integration.economy;

import java.math.BigDecimal;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.Utils;

public class EconomyHandler {

	// base
	private static final BigDecimal maxDouble = new BigDecimal(Double.MAX_VALUE);
	private static final Map<String, String> PLUGINS = Utils.asMap("Vault", "VaultUtils");
	private EconomyUtils utils = null;

	// get
	public EconomyUtils getUtils() {
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
					EconomyUtils utils = (EconomyUtils) Class.forName("com.guillaumevdn.gcore.integration.economy." + PLUGINS.get(plugin)).newInstance();
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

	public double get(OfflinePlayer player) {
		if (utils == null) {
			GCore.inst().warning("Trying to get money for player " + player.getName() + " but no economy provided was found");
			return 0d;
		} else {
			return utils.get(player);
		}
	}

	public void give(OfflinePlayer player, BigDecimal amount) {
		if (utils == null) {
			GCore.inst().warning("Trying to give money " + amount.toPlainString() + " to player " + player.getName() + " but no economy provided was found");
		} else {
			while (amount.compareTo(BigDecimal.ZERO) > 0) {
				if (amount.compareTo(maxDouble) > 1) {
					utils.give(player, maxDouble.doubleValue());
					amount = amount.subtract(maxDouble);
				} else {
					utils.give(player, amount.doubleValue());
					amount = BigDecimal.ZERO;
				}
			}
		}
	}

	public void give(OfflinePlayer player, double amount) {
		if (utils == null) {
			GCore.inst().warning("Trying to give money " + amount + " to player " + player.getName() + " but no economy provided was found");
		} else {
			utils.give(player, amount);
		}
	}

	public void take(OfflinePlayer player, double amount) {
		if (utils == null) {
			GCore.inst().warning("Trying to take money " + amount + " from player " + player.getName() + " but no economy provided was found");
		} else {
			utils.take(player, amount);
		}
	}

	public void take(OfflinePlayer player, BigDecimal amount) {
		if (utils == null) {
			GCore.inst().warning("Trying to take money " + amount.toPlainString() + " from player " + player.getName() + " but no economy provided was found");
		} else {
			while (amount.compareTo(BigDecimal.ZERO) > 0) {
				if (amount.compareTo(maxDouble) > 1) {
					utils.take(player, maxDouble.doubleValue());
					amount = amount.subtract(maxDouble);
				} else {
					utils.take(player, amount.doubleValue());
					amount = BigDecimal.ZERO;
				}
			}
		}
	}

	public String format(double amount) {
		if (utils == null) {
			GCore.inst().warning("Trying to format money " + amount + " but no economy provided was found");
			return Utils.round(amount);
		} else {
			return utils.format(amount);
		}
	}

}
