package be.pyrrh4.pyrcore.lib.versioncompat.bossbar;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.util.Utils;
import me.confuser.barapi.BarAPI;

class BarAPIUtils {

	static void sendBossbar(final Collection<Player> players, String text, int seconds, float progress) {
		// set bar
		for (Player player : players) {
			BarAPI.setMessage(player, text, progress * 100f);
		}
		// remove bar later
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : players) {
					if (player.isOnline()) {
						BarAPI.removeBar(player);
					}
				}
			}
		}.runTaskLater(PyrCore.inst(), Utils.getSecondsInTicks(seconds));
	}

}
