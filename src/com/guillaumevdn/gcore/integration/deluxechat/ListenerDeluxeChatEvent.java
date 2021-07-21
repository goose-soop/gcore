package com.guillaumevdn.gcore.integration.deluxechat;

import org.bukkit.event.EventPriority;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;

import me.clip.deluxechat.events.DeluxeChatEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerDeluxeChatEvent {

	public static final void register(IntegrationDeluxeChat instance) {
		instance.registerEvent(DeluxeChatEvent.class, EventPriority.LOWEST, true, og -> {
			if (og.getPlayer() != null) {
				PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getChatMessage(), og.getRecipients());
				og.setChatMessage(event.getMessage());
				og.setCancelled(event.isCancelled());
				//System.out.println("DeluxeChatEvent, message '" + og.getChatMessage() + "'");
				if (og.getRecipients().isEmpty()) {
					og.setCancelled(true);
				}
			}
		});
	}

}
