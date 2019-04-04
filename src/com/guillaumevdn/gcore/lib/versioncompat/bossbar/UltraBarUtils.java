package com.guillaumevdn.gcore.lib.versioncompat.bossbar;

import java.util.Collection;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import me.ryandw11.ultrabar.api.UltraBarAPI;

class UltraBarUtils {

	static void sendBossbar(Collection<Player> players, String text, int seconds, float progress) {
		new UltraBarAPI().sendBossBar(players, text, BarColor.YELLOW, BarStyle.SOLID, seconds, progress);
	}

}
