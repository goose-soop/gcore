package com.guillaumevdn.gcore.lib.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.player.PlayerUtils;

/**
 * @author GuillaumeVDN
 */
public class CurrencyXP extends Currency {

	public CurrencyXP(String id) {
		super(id, null);
	}

	// ----- initialization
	@Override
	protected boolean initialize() throws Throwable {
		return true;
	}

	// ----- methods
	@Override
	protected boolean doGive(OfflinePlayer player, double amount) throws Throwable {
		Player online = PlayerUtils.getOnline(player);
		if (online == null) {
			return false;
		}
		online.giveExp((int) amount);
		return true;
	}

	@Override
	protected boolean doTake(OfflinePlayer player, double amount) throws Throwable {
		Player online = PlayerUtils.getOnline(player);
		if (online == null) {
			return false;
		}
		online.giveExp(- (int) amount);
		return true;
	}

	@Override
	protected double doGet(OfflinePlayer player) throws Throwable {
		Player online = PlayerUtils.getOnline(player);
		return online != null ? (double) online.getTotalExperience() : 0d;
	}

}
