package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.event.ChatChannelEvent;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class ListenerChatChannelEvent implements Listener {

	// on doit faire ceux-là à la main vu que monsieur obfusque ses plugins jusqu'à l'os

	@EventHandler
	public void event(ChatChannelEvent og) {
		Player player = ObjectUtils.castOrNull(og.getSender(), Player.class);
		if (player != null) {
			PlayerChatEvent event = PlayerChatEvent.call(player, og.getMessage(), og.getRecipients());
			og.setMessage(event.getMessage());
			og.setCancelled(event.isCancelled());
			if (og.getRecipients().isEmpty()) {
				og.setCancelled(true);
			}
		}
	}

}
