package be.guillaumevdn.gcore.lib.versioncompat.bossbar;

import java.util.Collection;

import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.lib.util.Utils;

public class BossBarCompat {

	/**
	 * @param player the players that'll receive the bar
	 * @param text the text to display
	 * @param seconds the seconds that the bar will be shown
	 * @param progress the progression (between 0 and 1)
	 */
	public static void sendBossbar(Collection<Player> players, String text, int seconds, float progress) {
		// UltraBar
		if (Utils.isPluginEnabled("UltraBar")) {
			UltraBarUtils.sendBossbar(players, text, seconds, progress);
		}
		// BossBarAPI
		else if (Utils.isPluginEnabled("BossBarAPI")) {
			BossBarAPIUtils.sendBossbar(players, text, seconds, progress);
		}
		// BarAPI
		else if (Utils.isPluginEnabled("BarAPI")) {
			BarAPIUtils.sendBossbar(players, text, seconds, progress);
		}
	}

}
