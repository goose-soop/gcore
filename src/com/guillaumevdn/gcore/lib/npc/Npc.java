package com.guillaumevdn.gcore.lib.npc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.event.NpcDespawnEvent;
import com.guillaumevdn.gcore.lib.event.NpcMoveEvent;
import com.guillaumevdn.gcore.lib.event.NpcSpawnEvent;
import com.guillaumevdn.gcore.lib.event.NpcTeleportEvent;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.util.Handler;
import com.guillaumevdn.gcore.lib.util.Pair;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;

public class Npc {

	// base
	private final Player player;
	private final int id;
	private String name;
	private String skinData, skinSignature;
	private Location location;
	private double targetDistance;
	private Set<NpcStatus> status = new HashSet<NpcStatus>();
	private ItemData[] items = new ItemData[6];
	private boolean spawned = false;

	public Npc(final Player player, final int id, String name, String skinData, String skinSignature, Location location, double targetDistance, Set<NpcStatus> status, ItemData[] items) {
		this.player = player;
		this.id = id;
		this.name = name;
		this.skinData = skinData;
		this.skinSignature = skinSignature;
		this.location = location;
		this.targetDistance = targetDistance;
		if (status != null) this.status.addAll(status);
		if (items != null) {
			for (int i = 0; i < 6; ++i) {
				if (items.length >= i) {
					this.items[i] = items[i];
				}
			}
		}
	}

	// get
	public NpcData getData() {
		return GCore.inst().getNpcManager().getNpcData(id);
	}

	public Player getPlayer() {
		return player;
	}

	public int getId() {
		return id;
	}

	public int getEntityId() {
		return NpcProtocols.ENTITY_ID_BASE + id;
	}

	public String getName() {
		return name;
	}

	public String getSkinData() {
		return skinData;
	}

	public String getSkinSignature() {
		return skinSignature;
	}

	public Location getLocation() {
		return location;
	}

	public double getTargetDistance() {
		return targetDistance;
	}

	public List<ItemData> getItems() {
		return Collections.unmodifiableList(Utils.asList(items));
	}

	public Set<NpcStatus> getStatus() {
		return Collections.unmodifiableSet(status);
	}

	public boolean isSpawned() {
		return spawned;
	}

	// set
	public void setTargetDistance(double targetDistance) {
		this.targetDistance = targetDistance;
	}

	// methods
	/** @return the update result */
	public UpdateResult update() {
		// invalid world/location, must despawn
		Double distance = Utils.distance(location, player.getLocation());
		if (distance < 0d || distance > 50d) {
			return despawn() ? UpdateResult.DESPAWNED : UpdateResult.NONE;// eventually despawn
		}
		// eventually spawn
		boolean justSpawned = false;
		if (spawn()) {
			justSpawned = true;
		}
		// eventually target player if not in any navigator
		if (targetDistance > 0d && distance <= targetDistance && GCore.inst().getNpcManager().getNavigators(this).isEmpty()) {
			target(player.getEyeLocation());
			return justSpawned ? UpdateResult.SPAWNED : UpdateResult.TARGETED_PLAYER;
		}
		// success
		return justSpawned ? UpdateResult.SPAWNED : UpdateResult.NONE;
	}

	public static enum UpdateResult {
		SPAWNED, DESPAWNED, TARGETED_PLAYER, NONE;
	}

	// methods : spawn/despawn
	/** @return true if it was spawned, false if it was already spawned */
	public boolean spawn() {
		// already spawned
		if (spawned) {
			return false;
		}
		// spawn
		NpcProtocols.INSTANCE.spawn(player, getEntityId(), name, location, skinData, skinSignature);
		spawned = true;
		// update status and equipment
		updateStatus();
		updateEquipment();
		// event
		new Handler() {
			@Override
			public void execute() {
				Bukkit.getPluginManager().callEvent(new NpcSpawnEvent(Npc.this));
			}
		}.runSync();
		// success
		return true;
	}

	/** @return true if it was despawned, false if it was already despawned */
	public boolean despawn() {
		// not spawned
		if (!spawned) {
			return false;
		}
		// despawn
		NpcProtocols.INSTANCE.remove(player, getEntityId());
		spawned = false;
		// event
		new Handler() {
			@Override
			public void execute() {
				Bukkit.getPluginManager().callEvent(new NpcDespawnEvent(Npc.this));
			}
		}.runSync();
		// success
		return true;
	}

	// methods : location
	// https://wiki.vg/Protocol#Player_Look
	public void target(Location target) {
		Pair<Float, Float> look = getTargetLook(location.clone().add(0d, 1.8d, 0d), target);
		target(look.getA(), look.getB());
	}

	public static Pair<Float, Float> getTargetLook(Location location, Location target) {
		double x0 = location.getX(), y0 = location.getY(), z0 = location.getZ();
		double x = target.getX(), y = target.getY(), z = target.getZ();
		double dx = x - x0;
		double dy = y - y0;
		double dz = z - z0;
		double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
		double yaw = -Math.atan2(dx, dz) / Math.PI * 180d;
		if (yaw < 0) yaw = 360d + yaw;
		double pitch = -Math.asin(dy / r) / Math.PI * 180d;
		return new Pair<Float, Float>((float) yaw, (float) pitch);
	}

	public void target(float yaw, float pitch) {
		// not spawned
		if (!spawned) {
			return;
		}
		// target
		location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), yaw, pitch);
		NpcProtocols.INSTANCE.sendTarget(player, getEntityId(), yaw, pitch);
	}

	public void move(Location location, boolean onGround) {
		// not in the same world
		if (!location.getWorld().equals(this.location.getWorld())) {
			return;
		}
		// too far away, teleport
		final Location previous = this.location;
		if (Utils.distance(previous, location) > 8d) {
			teleport(location);
			return;
		}
		// set location
		this.location = location;
		// update player
		if (spawned) {// already spawned
			NpcProtocols.INSTANCE.relativeMove(player, getEntityId(), previous, location, onGround);
		} else {// spawn
			spawn();
		}
		// event
		new Handler() {
			@Override
			public void execute() {
				Bukkit.getPluginManager().callEvent(new NpcMoveEvent(Npc.this, previous));
			}
		}.runSync();
	}

	public void teleport(Location location) {
		// not in the same world
		if (!location.getWorld().equals(this.location.getWorld())) {
			return;
		}
		// set location
		final Location previous = this.location;
		this.location = location;
		// update player
		if (spawned) {// already spawned
			NpcProtocols.INSTANCE.teleport(player, getEntityId(), location);
		} else {// spawn
			spawn();
		}
		// event
		new Handler() {
			@Override
			public void execute() {
				Bukkit.getPluginManager().callEvent(new NpcTeleportEvent(Npc.this, previous));
			}
		}.runSync();
	}

	// methods : status
	private void updateStatus() {
		// update status
		Map<Integer, Object> map = Utils.asMap(0, NpcStatus.getMasked(status.toArray(new NpcStatus[status.size()])));
		NpcProtocols.INSTANCE.sendMetadata(player, NpcProtocols.INSTANCE.createMetadata(map), getEntityId());
	}

	public void setStatus(NpcStatus... status) {
		setStatus(Utils.asList(status));
	}

	public void setStatus(Collection<NpcStatus> status) {
		// replace status
		this.status.clear();
		this.status.addAll(status);
		// update player
		if (spawned) {// already spawned
			updateStatus();
		} else {// spawn
			spawn();
		}
	}

	// methods : equipment
	/**
	 * @param slot the item slot (0 to 5)
	 * @param item the item stack
	 */
	public void setItem(int slot, ItemData item) {
		// set item
		this.items[slot] = item;
		// refresh player
		if (spawned) {// already spawned
			updateEquipment();
		} else {// spawn
			spawn();
		}
	}

	/**
	 * @param items an array of items (size 6)
	 */
	public void setItems(ItemData[] items) {
		// set item
		for (int i = 0; i < 6; ++i) {
			this.items[i] = items[i];
		}
		// refresh player
		if (spawned) {// already spawned
			updateEquipment();
		} else {// spawn
			spawn();
		}
	}

	private void updateEquipment() {
		// get items
		ItemStack[] array = new ItemStack[6];
		for (int i = 0; i < 6; ++i) {
			ItemData item = items[i];
			array[i] = item != null && !item.getType().isAir() ? item.getItemStack() : null;
		}
		// update inventory
		NpcProtocols.INSTANCE.sendInventory(player, getEntityId(), array);
	}

	// methods : misc
	public void rename(String name) {
		this.name = name;
		// update player
		despawn();
		spawn();
	}

	public void changeSkin(String skinData, String skinSignature) {
		this.skinData = skinData;
		this.skinSignature = skinSignature;
		// update player
		despawn();
		spawn();
	}

	public void animate(NpcAnimation animation) {
		NpcProtocols.INSTANCE.sendAnimation(player, getEntityId(), animation);
	}

	// equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + player.getUniqueId().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Npc)) return false;
		Npc other = (Npc) obj;
		return other.id == id && other.player.getUniqueId().equals(player.getUniqueId());
	}

}
