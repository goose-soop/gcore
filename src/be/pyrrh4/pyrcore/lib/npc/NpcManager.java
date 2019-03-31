package be.pyrrh4.pyrcore.lib.npc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class NpcManager implements Listener {

	// base
	private BukkitTask updateTask = null;
	private NpcPacketListener packetListener = null;
	private Map<Player, Map<Integer, Npc>> npcs = new HashMap<Player, Map<Integer, Npc>>();

	// methods
	public Npc getNpc(Player player, int id) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		return playerNpcs != null ? playerNpcs.get(id) : null;
	}

	public Collection<Npc> getNpcs(Player player) {
		return Collections.unmodifiableCollection(npcs.get(player).values());
	}

	public void addNpc(Player player, Npc npc) {
		if (!npcs.containsKey(player)) {
			Map<Integer, Npc> playerNpcs = npcs.get(player);
			if (playerNpcs == null) npcs.put(player, playerNpcs = new HashMap<Integer, Npc>());
			playerNpcs.put(npc.getId(), npc);
			npc.spawn();
		}
	}

	public void removeNpc(Player player, Npc npc) {
		removeNpcs(player, Utils.asList(npc));
	}

	public void removeNpcs(Player player) {
		Map<Integer, Npc> playerNpcs = npcs.remove(player);
		if (playerNpcs != null) {
			removeNpcs(player, Utils.asList(playerNpcs.values()));
		}
	}

	public void removeNpcs(Player player, Collection<Npc> toDespawn) {
		Map<Integer, Npc> playerNpcs = npcs.get(player);
		if (playerNpcs != null) {
			for (Npc npc : toDespawn) {
				if (playerNpcs.remove(npc.getId()) != null) {
					npc.despawn();
				}
			}
			if (playerNpcs.isEmpty()) {
				npcs.remove(player);
			}
		}
	}

	// methods : enable/disable
	public void enable() {
		// start update task
		long delay = PyrCore.inst().getConfiguration().getLong("npc_update_delay", 5L);
		updateTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : npcs.keySet()) {
					for (Npc npc : npcs.get(player).values()) {
						npc.update();
					}
				}
			}
		}.runTaskTimer(PyrCore.inst(), 300L, delay);
		// listeners
		Bukkit.getPluginManager().registerEvents(this, PyrCore.inst());
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NpcPacketListener());
	}

	public void disable() {
		// stop update task
		if (updateTask != null) {
			updateTask.cancel();
			updateTask = null;
		}
		// despawn npcs
		for (Player player : npcs.keySet()) {
			for (Npc npc : npcs.get(player).values()) {
				npc.despawn();
			}
		}
		npcs.clear();
		// listeners
		HandlerList.unregisterAll(this);
		ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
		packetListener = null;
	}

	// events
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerQuitEvent event) {
		// despawn and remove npcs
		removeNpcs(event.getPlayer());
	}

}
