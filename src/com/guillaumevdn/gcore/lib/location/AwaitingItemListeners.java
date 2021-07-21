package com.guillaumevdn.gcore.lib.location;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class AwaitingItemListeners implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerChatEvent event) {
		if (event.isCommand()) {
			return;
		}
		Player player = event.getPlayer();
		if (WorkerGCore.inst().hasAwaitingItemCancelChat(player)) {
			String msg = event.getMessage();
			if (msg == null || StringUtils.unformat(msg).trim().toLowerCase().equals(TextGeneric.textCancel.parseLine())) {
				event.setCancelled(true);
				WorkerGCore.inst().consumeAwaitingItemCancelChat(player);
				Pair<Consumer<ItemStack>, Runnable> awaitingItem = WorkerGCore.inst().consumeAwaitingItems(player);
				if (awaitingItem != null) {
					if (awaitingItem.getB() != null) {
						GCore.inst().operateSync(() -> awaitingItem.getB().run());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Pair<Consumer<ItemStack>, Runnable> awaitingItem = WorkerGCore.inst().consumeAwaitingItems(player);
		if (awaitingItem != null) {
			WorkerGCore.inst().consumeAwaitingItemCancelChat(player);
			awaitingItem.getA().accept(event.getItemDrop().getItemStack().clone());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void event(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		WorkerGCore.inst().consumeAwaitingItems(player);
		WorkerGCore.inst().consumeAwaitingItemCancelChat(player);
	}

}
