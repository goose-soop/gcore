package com.guillaumevdn.gcore.lib.chat;

import java.util.HashSet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class VanillaChatListeners implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(AsyncPlayerChatEvent og) {
		if (og.getPlayer() != null) {
			PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getMessage(), og.getRecipients());
			og.setMessage(event.getMessage());
			og.setCancelled(event.isCancelled());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerCommandPreprocessEvent og) {
		if (og.getPlayer() != null) {
			int slashCount = StringUtils.countLeadingChar(og.getMessage(), '/');
			String command = og.getMessage().substring(slashCount).trim();
			if (slashCount == 0) {
				slashCount = 1;
			}
			PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), slashCount, command.trim(), new HashSet<>());
			if (event.getChangesToOG() > 0) {
				og.setMessage(StringUtils.repeatString("/", event.getCommandSlashCount()) + event.getMessage().trim());
				og.setCancelled(event.isCancelled());
			}
		}
	}

}
