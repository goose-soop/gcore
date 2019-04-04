/**
 * Some parts of this code were found on the internet from an old plugin named "ZQuest"
 */

package be.guillaumevdn.gcore.lib.npc;

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

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.event.NpcAttackEvent;
import be.guillaumevdn.gcore.lib.event.NpcInteractEvent;
import be.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;

public class NpcPacketListener implements PacketListener {

	// base
	private int lastInteract = -1;

	// overriden
	@Override
	public Plugin getPlugin() {
		return GCore.inst();
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.newBuilder().priority(ListenerPriority.NORMAL).types(new PacketType[] { PacketType.Play.Client.USE_ENTITY }).gamePhase(GamePhase.PLAYING).options(new ListenerOptions[0]).build();
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.EMPTY_WHITELIST;
	}

	@Override
	public void onPacketReceiving(PacketEvent packetEvent) {
		PacketContainer packet = packetEvent.getPacket();
		Player player = packetEvent.getPlayer();
		if (packet.getType().equals((Object) PacketType.Play.Client.USE_ENTITY)) {
			int entityId = (int) packet.getIntegers().read(0);
			if (entityId < NpcProtocols.ENTITY_ID_BASE) {
				return;
			}
			// not a known npc
			int npcId = NpcProtocols.ENTITY_ID_BASE - entityId;
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
			if (lastInteract == entityId) {
				lastInteract = -1;
				return;
			}
			if (action.equals(NpcAction.INTERACT)) {
				lastInteract = entityId;
			}
			// event
			if (action.equals(NpcAction.INTERACT)) {
				Bukkit.getPluginManager().callEvent(new NpcInteractEvent(npc));
			} else if (action.equals(NpcAction.ATTACK)) {
				Bukkit.getPluginManager().callEvent(new NpcAttackEvent(npc));
			}
		}
	}

	@Override
	public void onPacketSending(PacketEvent packetEvent) {
	}

}
