/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.npc;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.event.NpcAttackEvent;
import com.guillaumevdn.gcore.lib.event.NpcInteractEvent;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;

public class NpcPacketListener implements PacketListener {

	// base
	private long lastInteract = 0L;

	// overriden
	@Override
	public Plugin getPlugin() {
		return GCore.inst();
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.newBuilder()
				.gamePhase(GamePhase.PLAYING)
				.types(new PacketType[] { PacketType.Play.Client.USE_ENTITY })
				.options(new ListenerOptions[0])
				.priority(ListenerPriority.NORMAL)
				.build();
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.EMPTY_WHITELIST;
	}

	@Override
	public void onPacketReceiving(PacketEvent packetEvent) {
		PacketContainer packet = packetEvent.getPacket();
		Player player = packetEvent.getPlayer();
		if (packet.getType().equals(PacketType.Play.Client.USE_ENTITY)) {
			int entityId = (int) packet.getIntegers().read(0);
			if (entityId < NpcProtocols.ENTITY_ID_BASE) {
				return;
			}
			// not a known npc
			int npcId = entityId - NpcProtocols.ENTITY_ID_BASE;
			Npc npc = null;
			if (GCore.inst().getNpcManager() == null || (npc = GCore.inst().getNpcManager().getNpc(player, npcId)) == null) {
				return;
			}
			// get action
			NpcAction action = null;
			try {
				Field field = packet.getEntityUseActions().getField(0);
				field.setAccessible(true);
				Object value = field.get(packet.getEntityUseActions().getTarget());
				String act = value.toString();
				if (act != null) {
					for (NpcAction npcAction : NpcAction.values()) {
						if (act.equalsIgnoreCase(npcAction.name())) {
							action = npcAction;
							break;
						}
					}
				}
			} catch (Throwable ignored) {}
			if (action == null) {
				return;
			}
			// last interact (already interacted)
			if (System.currentTimeMillis() - lastInteract < 50L) {
				return;
			}
			lastInteract = System.currentTimeMillis();
			// trigger sync event
			final NpcAction finalAction = action;
			final Npc finalNpc = npc;
			new BukkitRunnable() {
				@Override
				public  void run() {
					if (finalAction.equals(NpcAction.INTERACT)) {
						Bukkit.getPluginManager().callEvent(new NpcInteractEvent(finalNpc));
					} else if (finalAction.equals(NpcAction.ATTACK)) {
						Bukkit.getPluginManager().callEvent(new NpcAttackEvent(finalNpc));
					}
				}
			}.runTask(GCore.inst());
		}
	}

	@Override
	public void onPacketSending(PacketEvent packetEvent) {
	}

}
