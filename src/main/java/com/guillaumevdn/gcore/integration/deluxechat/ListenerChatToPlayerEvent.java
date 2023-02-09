package com.guillaumevdn.gcore.integration.deluxechat;

import org.bukkit.event.EventPriority;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

import me.clip.deluxechat.events.ChatToPlayerEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerChatToPlayerEvent {

	public static final void register(IntegrationDeluxeChat instance) {
		instance.registerEvent(ChatToPlayerEvent.class, EventPriority.LOWEST, true, og -> {
			if (og.getPlayer() != null) {
				PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getChatMessage(), CollectionUtils.asSet(og.getRecipient()));
				og.setChatMessage(event.isCancelled() ? "" : event.getMessage());
				//Bukkit.getLogger().info("ChatToPlayerEvent, message '" + og.getChatMessage());
			}
		});
	}

}
