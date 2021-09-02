package com.guillaumevdn.gcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.guillaumevdn.gcore.command.GcoreItemReadClick;

/**
 * @author GuillaumeVDN
 */
public class InventoryEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void event(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (GcoreItemReadClick.TOGGLED.contains(player)) {
			event.setCancelled(true);
			GcoreItemReadClick.logItem(player, event.getCurrentItem());
		}
	}

}
