package com.guillaumevdn.gcore.lib.chat;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class AwaitingChatListeners implements Listener {

	@EventHandler(priority = EventPriority.LOW /* because awaiting chat location cancel chat listener is LOWEST */, ignoreCancelled = true)
	public void event(PlayerChatEvent event) {
		if (event.isCommand()) {
			return;
		}
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Pair<Consumer<String>, Runnable> awaitingChat = WorkerGCore.inst().consumeAwaitingChat(player);
		if (awaitingChat != null) {
			event.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					String msg = event.getMessage();
					if (msg == null || StringUtils.unformat(msg).trim().toLowerCase().equals(TextGeneric.textCancel.parseLine())) {
						if (awaitingChat.getB() != null) {
							awaitingChat.getB().run();
						}
					} else {
						awaitingChat.getA().accept(msg);
					}
				}
			}.runTask(GCore.inst());
		}
	}

	@EventHandler
	public void event(PlayerQuitEvent event) {
		WorkerGCore.inst().consumeAwaitingChat(event.getPlayer());
	}

}
