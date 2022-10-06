package com.guillaumevdn.gcore.lib.economy;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class Currency {

	private final String id;
	private final String requiredPlugin;

	private String name;
	private boolean enabled = false;
	private double formatMultiplier = 1d;
	private int formatDecimalPrecision = 2;
	private String formatSingle = null;
	private String formatMultiple = null;
	private ItemStack icon = null;

	public Currency(String id, String requiredPlugin) {
		this.id = id.toUpperCase();
		this.requiredPlugin = requiredPlugin;
		this.name = id;
	}

	public final String getId() {
		return id;
	}

	public final String getRequiredPlugin() {
		return requiredPlugin;
	}

	public final String getName() {
		return name;
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final double getFormatMultiplier() {
		return formatMultiplier;
	}

	public final int getFormatDecimalPrecision() {
		return formatDecimalPrecision;
	}

	public final String getFormatSingle() {
		return formatSingle;
	}

	public final String getFormatMultiple() {
		return formatMultiple;
	}

	public final ItemStack getIcon() {
		return icon;
	}

	// ----- obj

	@Override
	public String toString() {
		return getId();
	}

	// ----- enable

	public boolean enable() {
		// shouldn't enable
		if (requiredPlugin != null && !PluginUtils.isPluginEnabled(requiredPlugin)) {
			return (enabled = false);
		}
		// enable
		try {
			// initialize inner
			if (!initialize()) {
				return (enabled = false);
			}

			// config
			final String path = "currencies." + getId();
			formatSingle = ConfigGCore.baseConfig.readMandatoryString(path + ".format_single");
			formatMultiple = ConfigGCore.baseConfig.readMandatoryString(path + ".format_multiple");
			formatDecimalPrecision = ConfigGCore.baseConfig.readMandatoryInteger(path + ".format_decimal_precision");
			formatMultiplier = ConfigGCore.baseConfig.readDouble(path + ".format_multiplier", 1d);
			icon = ConfigGCore.baseConfig.readMandatoryItemStack(path + ".icon");
			name = ConfigGCore.baseConfig.readString(path + ".name", id);

			// done
			GCore.inst().getMainLogger().info("Enabled currency " + id);
			return (enabled = true);
		}
		// error
		catch (Throwable exception) {
			if (exception instanceof ClassNotFoundException) {
				GCore.inst().getMainLogger().warning("Couldn't enable currency " + id + " (not found)");
			} else {
				GCore.inst().getMainLogger().error("Couldn't enable currency " + id, exception);
			}
			return (enabled = false);
		}
	}

	protected abstract boolean initialize() throws Throwable;

	// ----- methods

	public String format(double amount) {
		if (formatMultiplier != 1d) {
			amount = amount * formatMultiplier;
		}
		return (amount > 1d ? formatMultiple : formatSingle).replace("{amount}", StringUtils.formatNumber(amount, formatDecimalPrecision));
		//return (amount > 1d ? formatMultiple : formatSingle).replace("{amount}", NumberUtils.roundString(amount, formatDecimalPrecision));
	}

	public double get(OfflinePlayer player) {
		if (!isEnabled()) {
			GCore.inst().getMainLogger().warning("Tried to get currency " + id + " for " + player.getName() + " but currency isn't enabled");
			return 0d;
		}
		try {
			return doGet(player);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't get currency " + id + " for " + player.getName(), exception);
			return 0d;
		}

	}

	public boolean ensureHas(OfflinePlayer player, double amount, boolean notify) {
		final double balance = get(player);
		if (balance >= amount) {
			return true;
		}
		if (notify) {
			TextGeneric.messageMustHaveCurrency
			.replace("{balance}", () -> format(balance))
			.replace("{money}", () -> format(amount))
			.send(player);
		}
		return false;
	}

	public boolean ensureHasAndTake(OfflinePlayer player, double amount, boolean notify) {
		return ensureHas(player, amount, notify) && take(player, amount);
	}

	public boolean give(OfflinePlayer player, double amount) {
		if (!isEnabled()) {
			GCore.inst().getMainLogger().warning("Tried to give currency " + id + " to " + player.getName() + " but currency isn't enabled");
			return false;
		}
		try {
			return doGive(player, amount);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't give currency " + id + " to " + player.getName(), exception);
			return false;
		}
	}

	public boolean take(OfflinePlayer player, double amount) {
		if (!isEnabled()) {
			GCore.inst().getMainLogger().warning("Tried to take currency " + id + " from " + player.getName() + " but currency isn't enabled");
			return false;
		}
		try {
			return doTake(player, amount);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't take currency " + id + " from " + player.getName(), exception);
			return false;
		}
	}

	public boolean set(OfflinePlayer player, double value) {
		if (!isEnabled()) {
			GCore.inst().getMainLogger().warning("Tried to set currency " + id + " from " + player.getName() + " but currency isn't enabled");
			return false;
		}
		try {
			double bal = doGet(player);
			if (value > bal) {
				return doGive(player, value - bal);
			} else if (value < bal) {
				return doTake(player, bal - value);
			}
			return true;
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't set currency " + id + " from " + player.getName(), exception);
			return false;
		}
	}

	// ----- abstract

	protected abstract double doGet(OfflinePlayer player) throws Throwable;
	protected abstract boolean doGive(OfflinePlayer player, double amount) throws Throwable;
	protected abstract boolean doTake(OfflinePlayer player, double amount) throws Throwable;

	// ----- registration

	private static final Map<String, Currency> registered = new HashMap<>(1);

	public static Collection<Currency> values() {
		return Collections.unmodifiableCollection(registered.values());
	}

	public static Currency safeValueOf(String id) {
		return registered.get(id.toUpperCase());
	}

	public static Currency valueOf(String id) throws IllegalArgumentException {
		Currency value = registered.get(id.toUpperCase());
		if (value != null) return value;
		throw new IllegalArgumentException("there's no currency with id " + id);
	}

	public static Currency register(Currency currency) {
		registered.put(currency.getId(), currency);
		return currency;
	}

	// ----- values

	public static final Currency VAULT = register(new CurrencyVault("VAULT"));
	public static final Currency PLAYER_POINTS = register(new CurrencyPlayerPoints("PLAYER_POINTS"));
	public static final Currency TOKEN_ENCHANT = register(new CurrencyTokenEnchant("TOKEN_ENCHANT"));
	public static final Currency XP_LEVEL = register(new CurrencyXPLevel("XP_LEVEL"));
	public static final Currency XP = register(new CurrencyXP("XP"));

}
