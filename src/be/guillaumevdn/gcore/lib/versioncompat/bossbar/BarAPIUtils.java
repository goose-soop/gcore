package be.guillaumevdn.gcore.lib.versioncompat.bossbar;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.util.Utils;
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
		}.runTaskLater(GCore.inst(), Utils.getSecondsInTicks(seconds));
	}

}
