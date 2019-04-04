/**
 * Some parts of this code were found on the internet from an old plugin named "ZQuest"
 */

package be.guillaumevdn.gcore.lib.versioncompat.npc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.lib.npc.SkinData;
import be.guillaumevdn.gcore.lib.util.Utils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class NpcProtocols1_8 extends NpcProtocols {

	// data
	@Override
	public void sendMetadata(Player player, WrappedDataWatcher data, int entityId) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getIntegers().write(0, entityId);
		packet.getWatchableCollectionModifier().write(0, data.getWatchableObjects());
		// send packet
		sendPacket(player, packet);
	}

	@Override
	public WrappedDataWatcher createMetadata(Map<Integer, Object> map) {
		// create data
		WrappedDataWatcher data = new WrappedDataWatcher();
		for (Map.Entry<Integer, Object> entry : map.entrySet()) {
			data.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(entry.getKey(), WrappedDataWatcher.Registry.get(entry.getValue().getClass())), entry.getValue());
		}
		// return
		return data;
	}

	@Override
	public Map<Integer, Object> getDefaultHumanEntityMetadata() {
		return Utils.asMap(
				0, (byte) 0,
				1, 0,
				2, "",
				3, false,
				4, false,
				5, (byte) 0,
				6, 1f,
				7, 0,
				8, false,
				9, 0,
				10, 0.0f,
				11, 0,
				12, (byte) 127,
				13, (byte) 0
				);
	}

	@Override
	public Object createPlayerInfo(Object gameProfile, GameMode gameMode, int entityId, String name) {
		Object nmsGameMode = getNMSGameMode(GameMode.SURVIVAL);
		try {
			Constructor declaredConstructor = PlayerInfoData.class.getDeclaredConstructor(PlayerInfoData.class, GameProfile.class, Integer.TYPE, nmsGameMode.getClass(), IChatBaseComponent.class);
			declaredConstructor.setAccessible(true);
			return declaredConstructor.newInstance(null, gameProfile, entityId, nmsGameMode, createNMStext(name));
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getNMSGameMode(GameMode gameMode) {
		try {
			Method method = EnumGamemode.class.getMethod("getById", Integer.TYPE);
			method.setAccessible(true);
			return method.invoke(null, gameMode.getValue());
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// inventory
	// https://web.archive.org/web/20141121181315/https://wiki.vg/Protocol#Entity_Equipment
	@Override
	public void sendInventory(Player player, int entityId, ItemStack... items) {
		if (items.length != 6) {
			return;
		}
		// for every item
		for (int i = 0; i < items.length; ++i) {
			if (i == 1) continue;// 1 = second hand
			// create packet
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
			packet.getModifier().writeDefaults();
			packet.getIntegers().write(0, entityId);
			packet.getShorts().write(0, i == 0 ? 0 : (short) (i - 1)/* 1-4 for equipment but in our array it's 2-5*/);
			packet.getItemModifier().write(0, items[i]);
			// send packet
			sendPacket(player, packet);
		}
	}

	// location
	@Override
	public void sendTarget(Player player, int entityId, double yaw, double pitch) {
		// create look packet
		PacketContainer lookPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
		lookPacket.getIntegers().write(0, entityId);
		lookPacket.getBytes().write(0, (byte) (yaw * 256.0 / 360.0));
		// create rotation packet
		PacketContainer rotationPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotationPacket.getIntegers().write(0, entityId);
		rotationPacket.getBytes().write(0, (byte) (pitch * 256.0 / 360.0));
		// send packets
		sendPacket(player, lookPacket);
		sendPacket(player, rotationPacket);
	}

	@Override
	public void relativeMove(Player player, int entityId, Location previous, Location location) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
		packet.getIntegers().write(0, entityId);
		packet.getIntegers().write(1, (int) ((location.getX() * 32.0 - previous.getX() * 32.0) * 128.0));
		packet.getIntegers().write(2, (int) ((location.getY() * 32.0 - previous.getY() * 32.0) * 128.0));
		packet.getIntegers().write(3, (int) ((location.getZ() * 32.0 - previous.getZ() * 32.0) * 128.0));
		packet.getBooleans().write(0, true);
		// send packet
		sendPacket(player, packet);
	}

	@Override
	public void teleport(Player player, int entityId, Location location) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet.getIntegers().write(0, entityId);
		packet.getDoubles().write(0, location.getX());
		packet.getDoubles().write(1, location.getY());
		packet.getDoubles().write(2, location.getZ());
		packet.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
		packet.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
		// send packet
		sendPacket(player, packet);
	}

	// spawn/despawn
	@Override
	public void remove(Player player, int entityId) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().write(0, new int[] { entityId });
		// send packet
		sendPacket(player, packet);
	}

	@Override
	public WrappedDataWatcher spawn(final Player player, int entityId, String name, Location location, UUID skin) {
		// create game profile
		final WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), (name.length() < 16) ? name : name.substring(0, 16));
		// skin
		SkinData skinData = skin != null && GCore.inst().getData().getNpcSkins() != null ? GCore.inst().getData().getNpcSkins().get(skin) : null;
		if (skinData != null) {
			gameProfile.getProperties().putAll(skinData.getSkinData());
		}
		// create metadata
		WrappedDataWatcher metadata = createMetadata(getDefaultHumanEntityMetadata());
		// create spawn packet
		PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		spawnPacket.getIntegers().write(0, entityId);
		spawnPacket.getDoubles().write(0, location.getX());
		spawnPacket.getDoubles().write(1, location.getY());
		spawnPacket.getDoubles().write(2, location.getZ());
		spawnPacket.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
		spawnPacket.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
		spawnPacket.getDataWatcherModifier().write(0, metadata);
		spawnPacket.getSpecificModifier(UUID.class).write(0, gameProfile.getUUID());
		// create info packet
		PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		infoPacket.getSpecificModifier(EnumPlayerInfoAction.class).write(0, EnumPlayerInfoAction.ADD_PLAYER);
		infoPacket.getSpecificModifier(List.class).write(0, Arrays.asList(createPlayerInfo(gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ")));
		// send packets
		sendPacket(player, infoPacket);
		sendPacket(player, spawnPacket);
		// more info
		new BukkitRunnable() {
			@Override
			public void run() {
				// create packet
				PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
				Object playerInfo = createPlayerInfo(gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ");
				packet.getSpecificModifier(EnumPlayerInfoAction.class).write(0, EnumPlayerInfoAction.REMOVE_PLAYER);
				packet.getSpecificModifier(List.class).write(0, Arrays.asList(playerInfo));
				// send packet
				sendPacket(player, packet);
			}
		}.runTaskLater(GCore.inst(), skin == null ? 5L : 40L);
		// update look
		sendTarget(player, entityId, location.getYaw(), location.getPitch());
		// success
		return metadata;
	}

	// send packet
	private static void sendPacket(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
