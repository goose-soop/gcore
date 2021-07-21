package com.guillaumevdn.gcore.lib.location;

import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class AwaitingLocationListeners implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerChatEvent event) {
		if (event.isCommand()) {
			return;
		}
		Player player = event.getPlayer();
		if (WorkerGCore.inst().hasAwaitingLocationCancelChat(player)) {
			String msg = event.getMessage();
			if (msg == null || StringUtils.unformat(msg).trim().toLowerCase().equals(TextGeneric.textCancel.parseLine())) {
				event.setCancelled(true);
				WorkerGCore.inst().consumeAwaitingLocationCancelChat(player);
				Pair<Consumer<Location>, Runnable> awaitingLocation = WorkerGCore.inst().consumeAwaitingLocations(player);
				if (awaitingLocation != null) {
					if (awaitingLocation.getB() != null) {
						GCore.inst().operateSync(() -> awaitingLocation.getB().run());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL /* normal because sneak cancel GUI delay is LOWEST*/, ignoreCancelled = true)
	public void event(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		Pair<Consumer<Location>, Runnable> awaitingLocation = WorkerGCore.inst().consumeAwaitingLocations(player);
		if (awaitingLocation != null) {
			WorkerGCore.inst().consumeAwaitingLocationCancelChat(player);
			event.setCancelled(true);
			awaitingLocation.getA().accept(player.getLocation().clone());
		}
	}

	@EventHandler
	public void event(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		WorkerGCore.inst().consumeAwaitingLocations(player);
		WorkerGCore.inst().consumeAwaitingLocationCancelChat(player);
	}

}
