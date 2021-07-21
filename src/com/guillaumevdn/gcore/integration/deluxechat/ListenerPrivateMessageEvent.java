package com.guillaumevdn.gcore.integration.deluxechat;

import org.bukkit.event.EventPriority;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

import me.clip.deluxechat.events.PrivateMessageEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerPrivateMessageEvent {

	public static final void register(IntegrationDeluxeChat instance) {
		instance.registerEvent(PrivateMessageEvent.class, EventPriority.LOWEST, true, og -> {
			if (og.getSender() != null) {
				PlayerChatEvent event = PlayerChatEvent.call(og.getSender(), og.getMessage(), CollectionUtils.asSet(og.getRecipient()));
				og.setMessage(event.getMessage());
				og.setCancelled(event.isCancelled());
				if (event.getRecipients().isEmpty()) {
					og.setCancelled(true);
				}
			}
		});
	}

}
