package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.event.PrePrivateMessageEvent;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class ListenerPreMessagePrivateEvent implements Listener {

	// ----- on doit faire ceux-là à la main vu que monsieur obfusque ses plugins jusqu'à l'os

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PrePrivateMessageEvent og) {
		Player player = ObjectUtils.castOrNull(og.getSender(), Player.class);
		if (player != null) {
			PlayerChatEvent event = PlayerChatEvent.call(player, og.getMessage(), CollectionUtils.asSet(og.getRecipient()));
			og.setMessage(event.getMessage());
			og.setCancelled(event.isCancelled());
			if (event.getRecipients().isEmpty()) {
				og.setCancelled(true);
			}
		}
	}

}
