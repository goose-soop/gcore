/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.versioncompat.npc;

import java.lang.reflect.Constructor;
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
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.util.Pair;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_13_R2.EnumGamemode;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class NpcProtocols1_13_1 extends NpcProtocols {

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
				5, false,
				6, (byte) 0,
				7, 1f,
				8, 0,
				9, false,
				10, 0,
				11, 0f,
				12, 0,
				13, (byte) 127,
				14, (byte) 0
				);
	}

	@Override
	public Object createPlayerInfo(Object gameProfile, GameMode gameMode, int entityId, String name) {
		EnumGamemode nmsGameMode = EnumGamemode.getById(gameMode.getValue());
		try {
			Constructor declaredConstructor = PlayerInfoData.class.getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, Integer.TYPE, nmsGameMode.getClass(), IChatBaseComponent.class);
			declaredConstructor.setAccessible(true);
			return declaredConstructor.newInstance(null, gameProfile, entityId, nmsGameMode, createNMSText(name));
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// inventory
	private static final Map<Integer, EnumItemSlot> slots = Utils.asMap(
			0, EnumItemSlot.OFFHAND,
			1, EnumItemSlot.MAINHAND,
			2, EnumItemSlot.FEET,
			3, EnumItemSlot.LEGS,
			4, EnumItemSlot.CHEST,
			5, EnumItemSlot.HEAD
			);

	@Override
	public void sendInventory(Player player, int entityId, ItemStack... items) {
		if (items.length != 6) {
			return;
		}
		// for every item
		for (int i = 0; i < items.length; ++i) {
			// create packet
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
			packet.getModifier().writeDefaults();
			packet.getIntegers().write(0, entityId);
			packet.getSpecificModifier(EnumItemSlot.class).write(0, slots.get(i));
			packet.getItemModifier().write(0, items[i]);
			// send packet
			sendPacket(player, packet);
		}
	}

	// location
	@Override
	public void relativeMove(Player player, int entityId, Location previous, Location location, boolean onGround) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
		packet.getIntegers().write(0, entityId);
		packet.getIntegers().write(1, (int) ((location.getX() * 32.0 - previous.getX() * 32.0) * 128.0));
		packet.getIntegers().write(2, (int) ((location.getY() * 32.0 - previous.getY() * 32.0) * 128.0));
		packet.getIntegers().write(3, (int) ((location.getZ() * 32.0 - previous.getZ() * 32.0) * 128.0));
		packet.getBooleans().write(0, onGround);
		// send packet
		sendPacket(player, packet);
		// send look packet eventually, if the y difference is less than 0.5 block
		if (Math.abs(previous.getY() - location.getY()) < 0.5d) {
			Pair<Float, Float> look = Npc.getTargetLook(previous.clone().add(0d, 1d, 0d), location.clone().add(0d, 1d, 0d));
			sendTarget(player, entityId, look.getA(), look.getB());
		}
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
	public WrappedDataWatcher spawn(final Player player, final int entityId, String name, Location location, String skinData, String skinSignature) {
		// create game profile
		final WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), (name.length() < 16) ? name : name.substring(0, 16));
		// skin
		if (skinData != null && skinSignature != null) {
			gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skinData, skinSignature));
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
		}.runTaskLater(GCore.inst(), 40L);
		// update look
		sendTarget(player, entityId, location.getYaw(), location.getPitch());
		// success
		return metadata;
	}

}
