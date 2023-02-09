package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.util.CompatPlayerChatEvent;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerCompatPlayerChatEvent implements Listener {

	// ----- on doit faire ceux-là à la main vu que monsieur obfusque ses plugins jusqu'à l'os

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(CompatPlayerChatEvent og) {
		if (og.getPlayer() != null) {
			PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getMessage(), og.getRecipients());
			og.setMessage(event.getMessage());
			og.setCancelled(event.isCancelled());
			if (og.getRecipients().isEmpty()) {
				og.setCancelled(true);
			}
		}
	}

}
