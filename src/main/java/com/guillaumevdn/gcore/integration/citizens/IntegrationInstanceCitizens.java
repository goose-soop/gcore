package com.guillaumevdn.gcore.integration.citizens;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.integration.citizens.event.NPCBothClickEvent;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeAreaInside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeAreaOutside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeCylinderInside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeCylinderOutside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeSingle;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeSphereInside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeSphereOutside;
import com.guillaumevdn.gcore.integration.citizens.position.PositionTypeCitizensNPCRelativeWorld;
import com.guillaumevdn.gcore.lib.chat.PlayerChatEvent;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstanceCitizens extends IntegrationInstance implements Listener {

	public IntegrationInstanceCitizens(Integration integration) {
		super(integration);
		registerSerializer(NPC.class, value -> "" + value.getId(), string -> CitizensAPI.getNPCRegistry().getById(NumberUtils.integerOrNull(string)));
	}

	// ----- activation
	private Map<String, Class<? extends PositionType>> types = CollectionUtils.asMap("CITIZENS_NPC_RELATIVE_AREA_INSIDE",
			PositionTypeCitizensNPCRelativeAreaInside.class, "CITIZENS_NPC_RELATIVE_AREA_OUTSIDE", PositionTypeCitizensNPCRelativeAreaOutside.class,
			"CITIZENS_NPC_RELATIVE_CYLINDER_INSIDE", PositionTypeCitizensNPCRelativeCylinderInside.class, "CITIZENS_NPC_RELATIVE_CYLINDER_OUTSIDE",
			PositionTypeCitizensNPCRelativeCylinderOutside.class, "CITIZENS_NPC_RELATIVE_SPHERE_INSIDE", PositionTypeCitizensNPCRelativeSphereInside.class,
			"CITIZENS_NPC_RELATIVE_SPHERE_OUTSIDE", PositionTypeCitizensNPCRelativeSphereOutside.class, "CITIZENS_NPC_RELATIVE_SINGLE",
			PositionTypeCitizensNPCRelativeSingle.class, "CITIZENS_NPC_RELATIVE_WORLD", PositionTypeCitizensNPCRelativeWorld.class);

	@Override
	public boolean activate() {
		types.forEach((id, typeClass) -> {
			try {
				PositionTypes.inst().register(Reflection.newInstance(typeClass, id).get());
			} catch (Throwable error) {
				error.printStackTrace();
			}
		});
		Bukkit.getPluginManager().registerEvents(this, getIntegration().getPlugin());
		return true;
	}

	@Override
	public void deactivate() {
		types.keySet().forEach(id -> PositionTypes.inst().unregister(id));
		HandlerList.unregisterAll(this);
	}

	// ----- utils
	public static Location getNPCLocation(NPC npc) {
		if (npc == null) {
			return null;
		}
		if (npc.getEntity() == null) {
			if (PluginUtils.isPluginEnabled("Citizens")) {
				return null; // #1886, citizens is sometimes disabled and "getStoredLocation" needs it to be enabled to work
			}
			return npc.getStoredLocation();
		}
		return npc.getEntity().getLocation();
	}

	// ----- await npc selection
	private static Set<UUID> awaitingNPCsCancelChat = new HashSet<>(1);
	private static Map<UUID, Pair<Consumer<NPC>, Runnable>> awaitingNPCs = new HashMap<>(1);

	public static void awaitNPC(Player player, Text message, Consumer<NPC> onSelect, Runnable onCancel) {
		// cancel current
		Pair<Consumer<String>, Runnable> currentChat = WorkerGCore.inst().consumeAwaitingChat(player);
		Pair<Consumer<NPC>, Runnable> currentNPC = awaitingNPCs.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null)
			currentChat.getB().run();
		if (currentNPC != null && currentNPC.getB() != null)
			currentNPC.getB().run();
		// ask
		if (message != null) {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingNPCs.put(player.getUniqueId(), Pair.of(onSelect, onCancel));
		awaitingNPCsCancelChat.add(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerChatEvent event) {
		if (event.isCommand()) {
			return;
		}
		Player player = event.getPlayer();
		Pair<Consumer<NPC>, Runnable> awaitingNPC = awaitingNPCs.get(player.getUniqueId());
		if (awaitingNPC != null) {
			String msg = event.getMessage();
			if (msg == null || StringUtils.unformat(msg).trim().toLowerCase().equals(TextGeneric.textCancel.parseLine())) {
				event.setCancelled(true);
				awaitingNPCsCancelChat.remove(player.getUniqueId());
				awaitingNPCs.remove(player.getUniqueId());
				if (awaitingNPC != null) {
					if (awaitingNPC.getB() != null) {
						GCore.inst().operateSync(() -> awaitingNPC.getB().run());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(NPCRightClickEvent event) {
		NPCBothClickEvent.callFrom(event);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(NPCLeftClickEvent event) {
		NPCBothClickEvent.callFrom(event);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(NPCBothClickEvent event) {
		Player player = event.getClicker();
		Pair<Consumer<NPC>, Runnable> awaitingNPC = awaitingNPCs.remove(player.getUniqueId());
		if (awaitingNPC != null) {
			awaitingNPCsCancelChat.remove(player.getUniqueId());
			event.setCancelled(true);
			awaitingNPC.getA().accept(event.getNPC());
		}
	}

	@EventHandler
	public void event(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		awaitingNPCs.remove(player.getUniqueId());
		awaitingNPCsCancelChat.remove(player.getUniqueId());
	}

}
