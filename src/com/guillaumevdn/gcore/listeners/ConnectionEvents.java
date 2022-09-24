package com.guillaumevdn.gcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.statistic.Statistic;

/**
 * @author GuillaumeVDN
 */
public class ConnectionEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void event(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		WorkerGCore.inst().registerOfflinePlayer(player.getName(), player.getUniqueId());

		// init player NPCs
		if (Version.ATLEAST_1_9 && PluginUtils.isPluginEnabled("ProtocolLib") && NpcProtocols.inst() != null) {
			BoardUsersNPCs.inst().fetchValue(player.getUniqueId(), null, null, false, true);
		}

		// fetch and cache statistics
		Statistic.values().forEach(statistic -> BoardStatistics.inst().fetchValue(statistic, player.getUniqueId(), null, null, true, true));

	}

		/*final int entityId = 381326 + 8;
		final Location location = player.getEyeLocation();

		PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		spawnPacket.getIntegers().write(0, entityId);
		spawnPacket.getSpecificModifier(UUID.class).write(0, UUID.randomUUID());
		spawnPacket.getIntegers().write(1, 6);  // type = armor stand
		spawnPacket.getDoubles().write(0, location.getX());
		spawnPacket.getDoubles().write(1, location.getY());
		spawnPacket.getDoubles().write(2, location.getZ());
		spawnPacket.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
		spawnPacket.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
		spawnPacket.getBytes().write(2, (byte) 0);  // head yaw
		spawnPacket.getIntegers().write(2, 6);  // data ?

		final Map<Integer, Object> metadataMap = CollectionUtils.asMap(
				2, "§aTest",  // custom name
				3, true,  // is custom name visible
				5, true,  // has no gravity
				15, (byte) 0x01  // is small
				);

		WrappedDataWatcher metadata = createMetadata(metadataMap);
		PacketContainer metadataPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
		metadataPacket.getIntegers().write(0, entityId);
		metadataPacket.getWatchableCollectionModifier().write(0, metadata.getWatchableObjects());

		sendPacket(player, spawnPacket);
		sendPacket(player, metadataPacket);

	}

	public WrappedDataWatcher createMetadata(Map<Integer, Object> map) {
		WrappedDataWatcher data = new WrappedDataWatcher();
		if (Version.ATLEAST_1_13) {
			Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
			Serializer integerSerializer = WrappedDataWatcher.Registry.get(Integer.class);
			Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
			Serializer floatSerializer = WrappedDataWatcher.Registry.get(Float.class);
			for (Map.Entry<Integer, Object> entry : map.entrySet()) {
				if (entry.getValue() instanceof Byte) {
					data.setObject(new WrappedDataWatcherObject(entry.getKey(), byteSerializer), entry.getValue());
				} else if (entry.getValue() instanceof Integer) {
					data.setObject(new WrappedDataWatcherObject(entry.getKey(), integerSerializer), entry.getValue());
				} else if (entry.getValue() instanceof Boolean) {
					data.setObject(new WrappedDataWatcherObject(entry.getKey(), booleanSerializer), entry.getValue());
				} else if (entry.getValue() instanceof Float) {
					data.setObject(new WrappedDataWatcherObject(entry.getKey(), floatSerializer), entry.getValue());
				}
			}
		}
		else {
			for (Map.Entry<Integer, Object> entry : map.entrySet()) {
				data.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(entry.getKey(), WrappedDataWatcher.Registry.get(entry.getValue().getClass())), entry.getValue());
			}
		}
		return data;
	}

	private static void sendPacket(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}*/

}
