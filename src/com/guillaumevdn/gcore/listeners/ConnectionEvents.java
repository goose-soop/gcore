package com.guillaumevdn.gcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.statistic.Statistic;

/**
 * @author GuillaumeVDN
 */
public class ConnectionEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void event(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		WorkerGCore.inst().registerOfflinePlayer(player.getName(), player.getUniqueId());

		// init player NPCs
		if (Version.ATLEAST_1_9 && PluginUtils.isPluginEnabled("ProtocolLib") && NpcProtocols.inst() != null) {
			BoardUsersNPCs.inst().fetchValue(player.getUniqueId(), null, null, false, true);
		}

		// fetch and cache statistics
		Statistic.values().forEach(statistic -> BoardStatistics.inst().fetchValue(statistic, player.getUniqueId(), null, null, true, true));
	}

}
