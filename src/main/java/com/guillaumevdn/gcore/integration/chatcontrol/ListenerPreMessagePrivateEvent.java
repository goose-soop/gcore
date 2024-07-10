package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.event.Listener;
//import org.mineacademy.chatcontrol.api.event.PrePrivateMessageEvent;

/**
 * @author GuillaumeVDN
 */
public final class ListenerPreMessagePrivateEvent implements Listener {

    // le plugin est obfusqué d'une manière sus et gradle n'arrive pas à le compiler, tant pis pour lui, pas le temps de
    // faire ça

    /*
     * @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true) public void event(PrePrivateMessageEvent og) {
     * Player player = ObjectUtils.castOrNull(og.getSender(), Player.class); if (player != null) { PlayerChatEvent event =
     * PlayerChatEvent.call(player, og.getMessage(), CollectionUtils.asSet(og.getRecipient()));
     * og.setMessage(event.getMessage()); og.setCancelled(event.isCancelled()); if (event.getRecipients().isEmpty()) {
     * og.setCancelled(true); } } }
     */

}
