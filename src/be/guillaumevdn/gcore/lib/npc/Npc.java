package be.guillaumevdn.gcore.lib.npc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import be.guillaumevdn.gcore.lib.event.NpcDespawnEvent;
import be.guillaumevdn.gcore.lib.event.NpcMoveEvent;
import be.guillaumevdn.gcore.lib.event.NpcSpawnEvent;
import be.guillaumevdn.gcore.lib.event.NpcTeleportEvent;
import be.guillaumevdn.gcore.lib.gui.ItemData;
import be.guillaumevdn.gcore.lib.npc.navigation.Navigator;
import be.guillaumevdn.gcore.lib.util.Utils;
import be.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;

public class Npc {

	// base
	private final Player player;
	private final int id;
	private String name;
	private UUID skin;
	private Location location;
	private double targetDistance;
	private Set<NpcStatus> status = new HashSet<NpcStatus>();
	private ItemData[] items = new ItemData[6];
	private boolean spawned = false;
	private Navigator navigator = null;

	public Npc(final Player player, final int id, String name, UUID skin, Location location, double targetDistance, Set<NpcStatus> status, ItemData[] items) {
		this.player = player;
		this.id = id;
		this.name = name;
		this.skin = skin;
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

	public UUID getSkin() {
		return skin;
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

	public Navigator getNavigator() {
		return navigator;
	}

	// set
	public void setTargetDistance(double targetDistance) {
		this.targetDistance = targetDistance;
	}

	// methods
	/** @return the update result */
	public UpdateResult update() {
		// invalid world/location, must despawn
		Double distance = null;
		if (!spawned) {
			distance = Utils.distance(location, player.getLocation());
			if (distance < 0d || distance > 50d) {
				return despawn() ? UpdateResult.DESPAWNED : UpdateResult.NONE;// eventually despawn
			}
		}
		// eventually spawn
		boolean justSpawned = false;
		if (spawn()) {
			justSpawned = true;
		}
		// eventually target player
		if (targetDistance > 0d && (distance == null ? distance = Utils.distance(location, player.getLocation()) : distance) <= targetDistance) {
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
		NpcProtocols.INSTANCE.spawn(player, getEntityId(), name, location, skin);
		spawned = true;
		// update status and equipment
		updateStatus();
		updateEquipment();
		// event
		Bukkit.getPluginManager().callEvent(new NpcSpawnEvent(this));
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
		Bukkit.getPluginManager().callEvent(new NpcDespawnEvent(this));
		// success
		return true;
	}

	// methods : location
	public void target(Location target) {
		target(getLocalAngle(new Vector(target.getX(), 0d, location.getZ()), target.toVector()), location.getPitch());
	}

	public void target(double yaw, double pitch) {
		// not spawned
		if (!spawned) {
			return;
		}
		// target
		NpcProtocols.INSTANCE.sendTarget(player, getEntityId(), yaw, pitch);
	}

	public void move(Location location) {
		// not in the same world
		if (!location.getWorld().equals(this.location.getWorld())) {
			return;
		}
		// too far away, teleport
		Location previous = this.location;
		if (Utils.distance(previous, location) > 8d) {
			teleport(location);
			return;
		}
		// set location
		this.location = location;
		// update player
		if (spawned) {// already spawned
			NpcProtocols.INSTANCE.relativeMove(player, getEntityId(), previous, location);
		} else {// spawn
			spawn();
		}
		// event
		Bukkit.getPluginManager().callEvent(new NpcMoveEvent(this, previous));
	}

	public void teleport(Location location) {
		// not in the same world
		if (!location.getWorld().equals(this.location.getWorld())) {
			return;
		}
		// set location
		Location previous = this.location;
		this.location = location;
		// update player
		if (spawned) {// already spawned
			NpcProtocols.INSTANCE.teleport(player, getEntityId(), location);
		} else {// spawn
			spawn();
		}
		// event
		Bukkit.getPluginManager().callEvent(new NpcTeleportEvent(this, previous));
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

	public void changeSkin(UUID skin) {
		this.skin = skin;
		// update player
		despawn();
		spawn();
	}

	// utils
	private float getLocalAngle(Vector vector, Vector vector2) {
		float n = (float) Math.toDegrees(Math.atan2(vector2.getZ() - vector.getZ(), vector2.getX() - vector.getX())) - 90.0f;
		return n < 0.0f ? n + 360.0f : n;
	}

}
