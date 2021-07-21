package com.guillaumevdn.gcore.integration.deluxechat;

/**
 * @author GuillaumeVDN
 */
public final class ListenerDeluxeChatJSONEvent {

	public static final void register(IntegrationDeluxeChat instance) {
		// - actually since this is only when the player receives a message, I don't think it's relevant to use in my events
		/*instance.registerEvent(DeluxeChatJSONEvent.class, EventPriority.LOWEST, true, og -> {
			if (og.getPlayer() != null) {
				PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getJSONChatMessage(), CollectionUtils.asSet(og.getPlayer()));
				og.setJSONChatMessage(event.getMessage());
				og.setCancelled(event.isCancelled());
			}
		});*/
	}

}
