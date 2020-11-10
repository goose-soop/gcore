package com.guillaumevdn.gcore.integration.deluxechat;

import org.bukkit.event.EventPriority;

import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

import me.clip.deluxechat.events.DeluxeChatJSONEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerDeluxeChatJSONEvent {

	public static final void register(IntegrationDeluxeChat instance) {
		instance.registerEvent(DeluxeChatJSONEvent.class, EventPriority.LOWEST, true, og -> {
			if (og.getPlayer() != null) {
				PlayerChatEvent event = PlayerChatEvent.call(null, og.getJSONChatMessage(), CollectionUtils.asSet(og.getPlayer()));
				og.setJSONChatMessage(event.getMessage());
				og.setCancelled(event.isCancelled());
			}
		});
	}

}
