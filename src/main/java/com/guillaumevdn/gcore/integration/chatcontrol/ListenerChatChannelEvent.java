package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.event.Listener;
//import org.mineacademy.chatcontrol.api.event.ChatChannelEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerChatChannelEvent implements Listener
{
    // le plugin est obfusqué d'une manière sus et gradle n'arrive pas à le compiler, tant pis pour lui, pas le temps de faire ça

    /*@EventHandler
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
    }*/
}
