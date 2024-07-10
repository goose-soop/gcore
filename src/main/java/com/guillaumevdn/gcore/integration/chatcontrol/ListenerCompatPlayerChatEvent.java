package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.event.Listener;

/**
 * @author GuillaumeVDN
 */
public final class ListenerCompatPlayerChatEvent implements Listener {

    // le plugin est obfusqué d'une manière sus et gradle n'arrive pas à le compiler, tant pis pour lui, pas le temps de
    // faire ça

    /*
     * @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true) public void event(CompatPlayerChatEvent og) {
     * if (og.getPlayer() != null) { PlayerChatEvent event = PlayerChatEvent.call(og.getPlayer(), og.getMessage(),
     * og.getRecipients()); og.setMessage(event.getMessage()); og.setCancelled(event.isCancelled()); if
     * (og.getRecipients().isEmpty()) { og.setCancelled(true); } } }
     */

}
