package be.pyrrh4.pyrcore.lib.versioncompat.bossbar;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import net.md_5.bungee.api.chat.TextComponent;

class BossBarAPIUtils {

	static void sendBossbar(Collection<Player> players, String text, int seconds, float progress) {
		BossBarAPI.addBar(players, // The receiver of the BossBar
				new TextComponent(text), // Displayed message
				BossBarAPI.Color.BLUE, // Color of the bar
				BossBarAPI.Style.NOTCHED_20, // Bar style
				progress, // Progress (0.0 - 1.0)
				seconds * 20, // Timeout
				1); // Timeout-interval
	}

}
