/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.versioncompat.npc;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.guillaumevdn.gcore.lib.npc.NpcAnimation;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class NpcProtocols {

	// static base
	/** Can be null if ProtocolLib isn't installed or if there's no support for this Minecraft version */
	public static final NpcProtocols INSTANCE = Utils.createNPCProtocols();
	public static final int ENTITY_ID_BASE = 381326;

	public void init() {
	}

	// data
	public abstract void sendMetadata(Player player, WrappedDataWatcher data, int p2);
	public abstract WrappedDataWatcher createMetadata(Map<Integer, Object> map);
	public abstract Map<Integer, Object> getDefaultHumanEntityMetadata();
	public abstract Object createPlayerInfo(Object gameProfile, GameMode gameMode, int entityId, String name);

	// misc
	public abstract void sendInventory(Player player, int entityId, ItemStack... items);

	public void sendAnimation(Player player, int entityId, NpcAnimation animation) {
		// create packet
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION);
		packet.getModifier().writeDefaults();
		packet.getIntegers().write(0, entityId);
		packet.getBytes().write(0, animation.getAnimationData());
		// send packet
		sendPacket(player, packet);
	}

	// position
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

	public abstract void relativeMove(Player player, int entityId, Location previous, Location location, boolean onGround);
	public abstract void teleport(Player player, int entityId, Location location);
	public abstract void remove(Player player, int entityId);
	public abstract WrappedDataWatcher spawn(Player player, int entityId, String name, Location location, String skinData, String skinSignature);

	// text
	public final Object createNMSText(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		Class<?> c = MinecraftReflection.getMinecraftClass("ChatComponentText");
		try {
			Constructor<?> ct = c.getDeclaredConstructor(String.class);
			return ct.newInstance(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// send packet
	public final void sendPacket(Player player, PacketContainer packet) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
