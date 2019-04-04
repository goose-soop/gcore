/**
 * Some parts of this code were found on the internet from an old plugin named "ZQuest"
 */

package be.guillaumevdn.gcore.lib.versioncompat.npc;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import be.guillaumevdn.gcore.lib.util.Utils;

public abstract class NpcProtocols {

	// static base
	/** Can be null if ProtocolLib isn't installed */
	public static final NpcProtocols INSTANCE = Utils.createNPCProtocols();
	public static final int ENTITY_ID_BASE = 694000;

	public void init() {
	}

	// data
	public abstract void sendMetadata(Player player, WrappedDataWatcher data, int p2);
	public abstract WrappedDataWatcher createMetadata(Map<Integer, Object> map);
	public abstract Map<Integer, Object> getDefaultHumanEntityMetadata();
	public abstract Object createPlayerInfo(Object gameProfile, GameMode gameMode, int entityId, String name);
	public abstract Object getNMSGameMode(GameMode gameMode);

	// inventory
	public abstract void sendInventory(Player player, int entityId, ItemStack... items);

	// position
	public abstract void sendTarget(Player player, int entityId, double yaw, double pitch);
	public abstract void relativeMove(Player player, int entityId, Location location, Location location2);
	public abstract void teleport(Player player, int entityId, Location location);
	public abstract void remove(Player player, int entityId);
	public abstract WrappedDataWatcher spawn(Player player, int entityId, String name, Location location, UUID skinData);

	// text
	public Object createNMStext(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		Class<?> c = (Class<?>) MinecraftReflection.getMinecraftClass("ChatComponentText");
		try {
			Constructor<?> ct = c.getDeclaredConstructor(String.class);
			return ct.newInstance(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
