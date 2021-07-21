package com.guillaumevdn.gcore.lib.compatibility.bossbar;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionMethod;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;

/**
 * @author GuillaumeVDN
 * Original code : https://github.com/confuser/BarAPI
 */
public final class FakeDragon {

	private Bossbar bossbar;
	private Player player;
	private transient ReflectionObject dragon = null;
	private transient int id = -1;

	public FakeDragon(Bossbar bossbar, Player player) {
		this.bossbar = bossbar;
		this.player = player;
	}

	// ----- get
	public Bossbar getBossbar() {
		return bossbar;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		Location loc = getPlayer().getLocation();
		return new Location(loc.getWorld(), loc.getX(), -10d, loc.getZ(), loc.getPitch(), loc.getYaw());
	}

	// ----- packets
	public void sendSpawn() throws Throwable {
		Location loc = getLocation();
		ReflectionObject world = ReflectionObject.of(getPlayer().getWorld()).invokeMethod("getHandle");
		dragon = Reflection.newNmsInstance("EntityEnderDragon", new Object[] { world.get() });
		dragon.invokeMethod("setLocation", loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		dragon.invokeMethod("setInvisible", true);
		dragon.invokeMethod("setCustomName", getBossbar().getTitle());
		dragon.invokeMethod("setHealth", (float) (getBossbar().getProgress() * 200f));
		id = (int) dragon.invokeMethod("getId").get();
		Reflection.sendNmsPacket(getPlayer(), "PacketPlayOutSpawnEntityLiving", dragon.justGet());
		sendMetadata(getWatcher());
	}

	public void sendDestroy() throws Throwable {
		Reflection.sendNmsPacket(getPlayer(), "PacketPlayOutEntityDestroy", new int[] { id });
	}

	void sendDestroySafe() {
		try {
			sendDestroy();
		} catch (Throwable ignored) {}
	}

	public void sendTeleport(Location location) throws Throwable {
		List<Object> params = CollectionUtils.asList(id,
				location.getBlockX() * 32,
				location.getBlockY() * 32,
				location.getBlockZ() * 32,
				(byte) ((int) location.getYaw() * 256 / 360),
				(byte) ((int) location.getPitch() * 256 / 360)
				);
		if (Version.ATLEAST_1_8) {
			params.add(false);
		}
		Reflection.sendNmsPacket(getPlayer(), "PacketPlayOutEntityTeleport", params.toArray());
	}

	private void sendMetadata(Object watcher) throws Throwable {
		Reflection.sendNmsPacket(getPlayer(), "PacketPlayOutEntityMetadata", id, watcher, true);
	}

	private Object getWatcher() throws Throwable {
		ReflectionObject watcher = Reflection.newNmsInstance("DataWatcher", dragon.justGet());
		ReflectionMethod method = Reflection.getMethod(watcher.get().getClass(), "a", CollectionUtils.asList(int.class, Object.class));
		method.invoke(watcher.justGet(), Version.ATLEAST_1_8 ? 5 : 0, (byte) 0x20);
		method.invoke(watcher.justGet(), 6, new Float(getBossbar().getProgress()));
		method.invoke(watcher.justGet(), 7, new Integer(0));
		method.invoke(watcher.justGet(), 8, new Byte((byte) 1));
		method.invoke(watcher.justGet(), 10, getBossbar().getTitle());
		method.invoke(watcher.justGet(), 11, new Byte((byte) 1));
		return watcher.justGet();
	}

}
