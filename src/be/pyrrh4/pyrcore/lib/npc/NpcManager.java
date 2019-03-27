package be.pyrrh4.pyrcore.lib.npc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.comphenix.protocol.ProtocolLibrary;

import be.pyrrh4.pyrcore.PyrCore;

public class NpcManager implements Listener {

	// base
	private NpcPacketListener packetListener = null;
	private Map<Player, Map<String, Npc>> npcs = new HashMap<Player, Map<String, Npc>>();

	// methods
	public Npc getNpc(Player player, int entityId) {
		Map<String, Npc> playerNpcs = npcs.get(player);
		if (playerNpcs != null) {
			for (Npc npc : playerNpcs.values()) {
				if (npc.getEntityId() == entityId) {
					return npc;
				}
			}
		}
		return null;
	}

	// methods : enable/disable
	public void enable() {
		// listeners
		Bukkit.getPluginManager().registerEvents(this, PyrCore.inst());
		ProtocolLibrary.getProtocolManager().addPacketListener(packetListener = new NpcPacketListener());
	}

	public void disable() {
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
	@EventHandler
	public void event(PlayerQuitEvent event) {
		// despawn npcs
		Player player = event.getPlayer();
		Map<String, Npc> playerNpcs = npcs.remove(player);
		if (playerNpcs != null) {
			for (Npc npc : playerNpcs.values()) {
				npc.despawn();
			}
		}
	}

}
