/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.legacy_npc;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.guillaumevdn.gcore.GCore;

public class NPCPacketListener implements PacketListener {

	// ----- base
	private long lastInteract = 0L;

	// ----- overriden
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
			int entityId = packet.getIntegers().read(0);
			if (entityId < NpcProtocols.ENTITY_ID_BASE) {
				return;
			}
			// not a known npc
			int npcId = entityId - NpcProtocols.ENTITY_ID_BASE;
			NPC npc = NPCManager.inst() == null ? null : NPCManager.inst().getNpc(player, npcId);
			if (npc == null) {
				return;
			}
			// get action
			NPCInteraction interaction = null;
			try {
				Field field = packet.getEntityUseActions().getField(0);
				field.setAccessible(true);
				Object value = field.get(packet.getEntityUseActions().getTarget());
				String act = value.toString();
				if (act != null) {
					for (NPCInteraction npcAction : NPCInteraction.values()) {
						if (act.equalsIgnoreCase(npcAction.name())) {
							interaction = npcAction;
							break;
						}
					}
				}
			} catch (Throwable ignored) {}
			if (interaction == null) {
				return;
			}
			// last interact (already interacted)
			if (System.currentTimeMillis() - lastInteract < 50L) {
				return;
			}
			lastInteract = System.currentTimeMillis();
			// trigger event (resync because spigot wants to >:|)
			NPCInteraction interactionF = interaction; // :fbfDebilus:
			GCore.inst().operateSync(() -> Bukkit.getPluginManager().callEvent(new PlayerInteractedNPCEvent(player, npc, interactionF)));
		}
	}

	@Override
	public void onPacketSending(PacketEvent packetEvent) {
	}

}
