package com.guillaumevdn.gcore.lib.legacy_npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionEnum;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.tuple.Pair;

public final class NpcProtocols {

	public static final int ENTITY_ID_BASE = 381326;

	private static NpcProtocols instance = null;

	public static NpcProtocols inst() {
		return instance;
	}

	private final ReflectionEnum slotEnum;
	private final ReflectionEnum playerInfoActionEnum;
	private final Map<Integer, Object> slots;

	public NpcProtocols() throws Throwable {
		slotEnum = Reflection.getNmsEnum((Version.REMAPPED ? "world.entity." : "") + "EnumItemSlot");
		if (Version.ATLEAST_1_19_3) {
			playerInfoActionEnum = Reflection.getNmsEnum((Version.REMAPPED ? "network.protocol.game." : "") + "ClientboundPlayerInfoUpdatePacket$a");
		} else {
			playerInfoActionEnum = Reflection.getNmsEnum((Version.REMAPPED ? "network.protocol.game." : "") + "PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
		}
		slots = CollectionUtils.asMap(0, slotEnum.valueOf("MAINHAND").get(), 1, slotEnum.valueOf("OFFHAND").get(), 2, slotEnum.valueOf("FEET").get(), 3,
				slotEnum.valueOf("LEGS").get(), 4, slotEnum.valueOf("CHEST").get(), 5, slotEnum.valueOf("HEAD").get());
		/*
		 * slots = CollectionUtils.asMap( 0, Version.ATLEAST_1_14 ? slotEnum.valueOf("OFFHAND").get() :
		 * slotEnum.valueOf("MAINHAND").get(), 1, Version.ATLEAST_1_14 ? slotEnum.valueOf("MAINHAND").get() :
		 * slotEnum.valueOf("OFFHAND").get(), 2, slotEnum.valueOf("FEET").get(), 3, slotEnum.valueOf("LEGS").get(), 4,
		 * slotEnum.valueOf("CHEST").get(), 5, slotEnum.valueOf("HEAD").get() );
		 */
		instance = this;
	}

	// ----- data
	public static void sendMetadata(Player player, Map<Integer, Object> metadata, int entityId) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
		packet.getIntegers().write(0, entityId);
		if (Version.ATLEAST_1_19_3)
			packet.getDataValueCollectionModifier().write(0, createMetadataCollection(metadata));
		else
			packet.getWatchableCollectionModifier().write(0, createMetadata(metadata).getWatchableObjects());
		sendPacket(player, packet);
	}

	private static Serializer byteSerializer = null;
	private static Serializer integerSerializer = null;
	private static Serializer booleanSerializer = null;
	private static Serializer floatSerializer = null;

	public static synchronized WrappedDataWatcher createMetadata(Map<Integer, Object> map) {
		WrappedDataWatcher data = new WrappedDataWatcher();
		if (Version.ATLEAST_1_13) {
			if (byteSerializer == null) {
				byteSerializer = Registry.get(Byte.class);
				integerSerializer = Registry.get(Integer.class);
				booleanSerializer = Registry.get(Boolean.class);
				floatSerializer = Registry.get(Float.class);
			}
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
		} else {
			for (Map.Entry<Integer, Object> entry : map.entrySet()) {
				data.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(entry.getKey(), WrappedDataWatcher.Registry.get(entry.getValue().getClass())),
						entry.getValue());
			}
		}
		return data;
	}

	public static synchronized List<WrappedDataValue> createMetadataCollection(Map<Integer, Object> map) {
		if (byteSerializer == null) {
			byteSerializer = Registry.get(Byte.class);
			integerSerializer = Registry.get(Integer.class);
			booleanSerializer = Registry.get(Boolean.class);
			floatSerializer = Registry.get(Float.class);
		}

		final List<WrappedDataValue> list = new ArrayList<>();
		for (final Entry<Integer, Object> entry : map.entrySet()) {
			if (ObjectUtils.instanceOf(entry.getValue(), Byte.class))
				list.add(new WrappedDataValue(entry.getKey(), byteSerializer, entry.getValue()));
			else if (ObjectUtils.instanceOf(entry.getValue(), Integer.class))
				list.add(new WrappedDataValue(entry.getKey(), integerSerializer, entry.getValue()));
			else if (ObjectUtils.instanceOf(entry.getValue(), Boolean.class))
				list.add(new WrappedDataValue(entry.getKey(), booleanSerializer, entry.getValue()));
			else if (ObjectUtils.instanceOf(entry.getValue(), Float.class))
				list.add(new WrappedDataValue(entry.getKey(), floatSerializer, entry.getValue()));
		}
		return list;
	}

	// ----- https://wiki.vg/Entity_metadata#Player
	public Map<Integer, Object> getDefaultHumanEntityMetadata() {
		if (Version.MC_1_9_R2.isCurrent()) {
			return CollectionUtils.asMap(12, (byte) 127 // displayed skin part
			);
		} else if (Version.ATLEAST_1_10 && !Version.ATLEAST_1_14) {
			return CollectionUtils.asMap(13, (byte) 127 // displayed skin part
			);
		} else if (Version.MC_1_14_R1.isCurrent()) {
			return CollectionUtils.asMap(15, (byte) 127 // displayed skin part
			);
		} else if (Version.ATLEAST_1_17) {
			return CollectionUtils.asMap(17, (byte) 127 // displayed skin part
			);
		} else if (Version.ATLEAST_1_15) {
			return CollectionUtils.asMap(16, (byte) 127 // displayed skin part
			);
		}
		return null;
	}

	public Object createPlayerInfo(UUID uuid, Object gameProfile, GameMode gameMode, int entityId, String name) throws Throwable {
		ReflectionObject gm = Reflection.invokeNmsMethod((Version.REMAPPED ? "world.level." : "") + "EnumGamemode", Version.ATLEAST_1_18 ? "a" : "getById",
				null, gameMode.getValue());
		if (Version.REMAPPED) {
			if (Version.ATLEAST_1_19_3)
				return Reflection.newNmsInstance("network.protocol.game.ClientboundPlayerInfoUpdatePacket$b", uuid, gameProfile, false, 0, gm.get(),
						Compat.createChatComponentIfNonEmptyOrNull(name), null /* chat session, can be null apparently */).get();
			else if (Version.ATLEAST_1_19) {
				return Reflection.newNmsInstance("network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData", gameProfile, entityId, gm.get(),
						createNMSText(name), null /* public key, can be null apparently */).get();
			} else {
				return Reflection
						.newNmsInstance("network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData", gameProfile, entityId, gm.get(), createNMSText(name))
						.get();
			}
		} else {
			return Reflection.newNmsInstance("PacketPlayOutPlayerInfo$PlayerInfoData", null, gameProfile, entityId, gm.get(), createNMSText(name)).get();
		}
	}

	// ----- misc
	public void sendInventory(Player player, int entityId, ItemStack... items) throws Throwable {
		if (items.length != 6) {
			return;
		}
		if (Version.ATLEAST_1_16) {
			List list = new ArrayList<>();
			for (int i = 0; i < items.length; ++i) {
				list.add(Reflection.newInstance("com.mojang.datafixers.util.Pair", slots.get(i),
						Reflection.invokeCraftbukkitMethod("inventory.CraftItemStack", "asNMSCopy", null, items[i]).get()).get());
			}
			Reflection.sendNmsPacket(player,
					Reflection.newNmsInstance((Version.REMAPPED ? "network.protocol.game." : "") + "PacketPlayOutEntityEquipment", entityId, list).get());
		} else {
			for (int i = 0; i < items.length; ++i) {
				PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
				packet.getModifier().writeDefaults();
				packet.getIntegers().write(0, entityId);
				Class enumClass = slotEnum.getEnumClass();
				packet.getSpecificModifier(enumClass).write(0, slots.get(i));
				packet.getItemModifier().write(0, items[i]);
				sendPacket(player, packet);
			}
		}
	}

	public void sendAnimation(Player player, int entityId, NPCAnimation animation) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION);
		packet.getModifier().writeDefaults();
		packet.getIntegers().write(0, entityId);
		packet.getBytes().write(0, animation.getAnimationData());
		// send packet
		sendPacket(player, packet);
	}

	// ----- position
	public final void sendTarget(Player player, int entityId, double yaw, double pitch) {
		// create look packet
		byte yawAngle = (byte) (yaw * 256d / 360d);
		byte pitchAngle = (byte) (pitch * 256d / 360d);
		PacketContainer lookPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
		lookPacket.getIntegers().write(0, entityId);
		lookPacket.getBytes().write(0, yawAngle);
		lookPacket.getBytes().write(1, pitchAngle);
		// create rotation packet
		PacketContainer rotationPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotationPacket.getIntegers().write(0, entityId);
		rotationPacket.getBytes().write(0, yawAngle);
		// send packets
		sendPacket(player, lookPacket);
		sendPacket(player, rotationPacket);
	}

	public void relativeMove(Player player, int entityId, Location previous, Location location, boolean onGround) {
		if (Version.ATLEAST_1_15) {
			// create packet
			boolean changeLook = Math.abs(previous.getY() - location.getY()) < 0.5d;
			PacketContainer packet = ProtocolLibrary.getProtocolManager()
					.createPacket(changeLook ? PacketType.Play.Server.REL_ENTITY_MOVE_LOOK : PacketType.Play.Server.REL_ENTITY_MOVE);
			// write position
			packet.getIntegers().write(0, entityId);
			packet.getShorts().write(0, (short) ((location.getX() * 32.0 - previous.getX() * 32.0) * 128.0));
			packet.getShorts().write(1, (short) ((location.getY() * 32.0 - previous.getY() * 32.0) * 128.0));
			packet.getShorts().write(2, (short) ((location.getZ() * 32.0 - previous.getZ() * 32.0) * 128.0));
			// write look eventually
			PacketContainer rotationPacket = null;
			if (changeLook) {
				Pair<Float, Float> look = NPC.getTargetLook(previous.clone().add(0d, 1d, 0d), location.clone().add(0d, 1d, 0d));
				byte yawAngle = (byte) (look.getA() / 360d * 256d);
				packet.getBytes().write(0, yawAngle);
				packet.getBytes().write(1, (byte) 0);
				rotationPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
				rotationPacket.getIntegers().write(0, entityId);
				rotationPacket.getBytes().write(0, yawAngle);
			}
			// write on ground
			packet.getBooleans().write(0, onGround);
			// send packets
			sendPacket(player, packet);
			if (rotationPacket != null) {
				sendPacket(player, rotationPacket);
			}
		} else if (Version.ATLEAST_1_14) {
			// create packet
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
			packet.getIntegers().write(0, entityId);
			packet.getShorts().write(0, (short) ((location.getX() * 32.0 - previous.getX() * 32.0) * 128.0));
			packet.getShorts().write(1, (short) ((location.getY() * 32.0 - previous.getY() * 32.0) * 128.0));
			packet.getShorts().write(2, (short) ((location.getZ() * 32.0 - previous.getZ() * 32.0) * 128.0));
			packet.getBooleans().write(0, onGround);
			// send packet
			sendPacket(player, packet);
			// send look packet eventually, if the y difference is less than 0.5 block
			if (Math.abs(previous.getY() - location.getY()) < 0.5d) {
				Pair<Float, Float> look = NPC.getTargetLook(previous.clone().add(0d, 1d, 0d), location.clone().add(0d, 1d, 0d));
				sendTarget(player, entityId, look.getA(), 0);
			}
		} else {
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
				Pair<Float, Float> look = NPC.getTargetLook(previous.clone().add(0d, 1d, 0d), location.clone().add(0d, 1d, 0d));
				sendTarget(player, entityId, look.getA(), (byte) 0);
			}
		}
	}

	public void teleport(Player player, int entityId, Location location) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet.getIntegers().write(0, entityId);
		packet.getDoubles().write(0, location.getX());
		packet.getDoubles().write(1, location.getY());
		packet.getDoubles().write(2, location.getZ());
		packet.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
		packet.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
		sendPacket(player, packet);
	}

	public void remove(Player player, int entityId) {
		if (Version.ATLEAST_1_17_1) {
			try {
				ReflectionObject packet = Reflection.newNmsInstance("network.protocol.game.PacketPlayOutEntityDestroy", new int[] { entityId });
				Reflection.sendNmsPacket(player, packet.justGet());
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
		} else {
			PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
			if (Version.ATLEAST_1_17) {
				packet.getIntegers().write(0, entityId);
			} else {
				packet.getIntegerArrays().write(0, new int[] { entityId });
			}
			sendPacket(player, packet);
		}
	}

	public void spawn(Player player, int entityId, String name, Location location, String skinData, String skinSignature) throws Throwable {
		Class infoActionEnum = playerInfoActionEnum.getEnumClass();
		if (Version.ATLEAST_1_15) {
			// create game profile
			final WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), name.length() <= 16 ? name : name.substring(0, 16));

			// skin
			if (skinData != null && skinSignature != null) {
				gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skinData, skinSignature));
			}

			// create spawn packet
			PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager()
					.createPacket(Version.ATLEAST_1_20_2 ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.NAMED_ENTITY_SPAWN);
			spawnPacket.getIntegers().write(0, entityId);
			if (Version.ATLEAST_1_20_2)
				spawnPacket.getEntityTypeModifier().write(0, EntityType.PLAYER);
			spawnPacket.getDoubles().write(0, location.getX());
			spawnPacket.getDoubles().write(1, location.getY());
			spawnPacket.getDoubles().write(2, location.getZ());
			spawnPacket.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
			spawnPacket.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
			spawnPacket.getSpecificModifier(UUID.class).write(0, gameProfile.getUUID());

			// create metadata packet
			PacketContainer metadataPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
			metadataPacket.getIntegers().write(0, entityId);
			if (Version.ATLEAST_1_19_3) {
				metadataPacket.getDataValueCollectionModifier().write(0, createMetadataCollection(getDefaultHumanEntityMetadata()));
			} else {
				metadataPacket.getWatchableCollectionModifier().write(0, createMetadata(getDefaultHumanEntityMetadata()).getWatchableObjects());
			}

			// create info packet
			final PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
			final Object pre119PlayerInfo;
			if (Version.ATLEAST_1_20_2) {
				pre119PlayerInfo = null;
				infoPacket.getSpecificModifier(EnumSet.class).write(0, EnumSet.of(playerInfoActionEnum.valueOf("ADD_PLAYER").get()));

				Object playerGameMode = Reflection.invokeNmsMethod("world.level.EnumGamemode", "a", null, 0).get();
				Object playerInfo = Reflection.newNmsInstance("network.protocol.game.ClientboundPlayerInfoUpdatePacket$b", gameProfile.getUUID(),
						gameProfile.getHandle(), false, 0, playerGameMode, WrappedChatComponent.fromText(" ").getHandle(), null).get();

				infoPacket.getModifier().write(1, Arrays.asList(playerInfo));
			} else if (Version.ATLEAST_1_19_3) {
				pre119PlayerInfo = null;
				infoPacket.getSpecificModifier(EnumSet.class).write(0, EnumSet.of(playerInfoActionEnum.valueOf("ADD_PLAYER").get()));
				infoPacket.getPlayerInfoDataLists().write(1, Collections
						.singletonList(new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(" "))));
			} else {
				pre119PlayerInfo = createPlayerInfo(gameProfile.getUUID(), gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ");
				infoPacket.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("ADD_PLAYER").get());
				infoPacket.getSpecificModifier(List.class).write(0, Arrays.asList(pre119PlayerInfo));
			}

			// send packets
			sendPacket(player, infoPacket);
			sendPacket(player, spawnPacket);
			sendPacket(player, metadataPacket);

			// remove info later
			new BukkitRunnable() {

				@Override
				public void run() {
					try {
						final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
						if (Version.ATLEAST_1_19_3) {
							infoPacket.getSpecificModifier(List.class).write(0, Arrays.asList(gameProfile.getUUID()));
						} else {
							packet.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("REMOVE_PLAYER").get());
							packet.getSpecificModifier(List.class).write(0, Arrays.asList(pre119PlayerInfo));
						}

						sendPacket(player, packet);
					} catch (Throwable exception) {
						exception.printStackTrace();
					}
				}

			}.runTaskLater(GCore.inst(), 5L);

			// update look
			sendTarget(player, entityId, location.getYaw(), location.getPitch());
		} else if (Version.ATLEAST_1_14) {
			// create profile and metadata
			WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), (name.length() < 16) ? name : name.substring(0, 16));
			WrappedDataWatcher metadata = createMetadata(getDefaultHumanEntityMetadata());
			if (skinData != null && skinSignature != null) {
				gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skinData, skinSignature));
			}

			// send info packet
			PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
			infoPacket.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("ADD_PLAYER").get());
			infoPacket.getSpecificModifier(List.class).write(0,
					Arrays.asList(createPlayerInfo(gameProfile.getUUID(), gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ")));
			sendPacket(player, infoPacket);

			// send spawn packet
			PacketContainer spawnPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
			spawnPacket.getIntegers().write(0, entityId);
			spawnPacket.getDoubles().write(0, location.getX());
			spawnPacket.getDoubles().write(1, location.getY());
			spawnPacket.getDoubles().write(2, location.getZ());
			spawnPacket.getBytes().write(0, (byte) (location.getYaw() * 256f / 360f));
			spawnPacket.getBytes().write(1, (byte) (location.getPitch() * 256f / 360f));
			spawnPacket.getDataWatcherModifier().write(0, metadata);
			spawnPacket.getSpecificModifier(UUID.class).write(0, gameProfile.getUUID());
			sendPacket(player, spawnPacket);

			// more info
			new BukkitRunnable() {

				@Override
				public void run() {
					try {
						// create packet
						PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
						Object playerInfo = createPlayerInfo(gameProfile.getUUID(), gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ");
						packet.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("REMOVE_PLAYER").get());
						packet.getSpecificModifier(List.class).write(0, Arrays.asList(playerInfo));
						// send packet
						sendPacket(player, packet);
					} catch (Throwable exception) {
						exception.printStackTrace();
					}
				}

			}.runTaskLater(GCore.inst(), 20L);

			// update look
			sendTarget(player, entityId, location.getYaw(), location.getPitch());
		} else {
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
			infoPacket.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("ADD_PLAYER").get());
			infoPacket.getSpecificModifier(List.class).write(0,
					Arrays.asList(createPlayerInfo(gameProfile.getUUID(), gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ")));
			// send packets
			sendPacket(player, infoPacket);
			sendPacket(player, spawnPacket);
			// remove player later
			new BukkitRunnable() {

				@Override
				public void run() {
					try {
						// create packet
						PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
						Object playerInfo;
						playerInfo = createPlayerInfo(gameProfile.getUUID(), gameProfile.getHandle(), GameMode.SURVIVAL, 0, " ");
						packet.getSpecificModifier(infoActionEnum).write(0, playerInfoActionEnum.valueOf("REMOVE_PLAYER").get());
						packet.getSpecificModifier(List.class).write(0, Arrays.asList(playerInfo));
						// send packet
						sendPacket(player, packet);
					} catch (Throwable exception) {
						exception.printStackTrace();
					}
				}

			}.runTaskLater(GCore.inst(), 20L);
			// update look
			sendTarget(player, entityId, location.getYaw(), location.getPitch());
		}
	}

	// ----- text
	public Object createNMSText(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		return Compat.createChatComponent(string).get();
	}

	// ----- send packet
	public static void sendPacket(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
