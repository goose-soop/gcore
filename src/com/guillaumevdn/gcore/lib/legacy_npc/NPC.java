package com.guillaumevdn.gcore.lib.legacy_npc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.tuple.Pair;

public class NPC {

	private final Player player;
	private final int id;

	private String name;
	private String skinData, skinSignature;
	private Location location;
	private double targetDistance;
	private List<NPCStatus> status = new ArrayList<>();
	private ItemStack[] items = new ItemStack[6];
	private boolean spawned = false;

	public NPC(final Player player, final int id, String name, String skinData, String skinSignature, Location location, double targetDistance, List<NPCStatus> status, ItemStack[] items) {
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

	// ----- get
	public ElementNPC getData() {
		return NPCManager.inst().getNpcConfig(id);
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

	public List<ItemStack> getItems() {
		return CollectionUtils.asList(items);
	}

	public List<NPCStatus> getStatus() {
		return status;
	}

	public boolean isSpawned() {
		return spawned;
	}

	// ----- set
	public void setTargetDistance(double targetDistance) {
		this.targetDistance = targetDistance;
	}

	// ----- methods
	/** @return the update result */
	public UpdateResult update() {
		// dead player or invalid world/location, must despawn
		if (player.isDead()) {
			return despawn() ? UpdateResult.DESPAWNED : UpdateResult.NONE; // eventually despawn
		}
		Double distance = player.getWorld().equals(location.getWorld()) ? location.distance(player.getLocation()) : null;
		if (distance == null || distance > 50d) {
			return despawn() ? UpdateResult.DESPAWNED : UpdateResult.NONE; // eventually despawn
		}
		// eventually spawn
		boolean justSpawned = false;
		if (spawn()) {
			justSpawned = true;
		}
		// eventually target player if not in any navigator
		if (targetDistance > 0d && distance <= targetDistance && NPCManager.inst().getNavigators(this).isEmpty()) {
			target(player.getEyeLocation());
			return justSpawned ? UpdateResult.SPAWNED : UpdateResult.TARGETED_PLAYER;
		}
		// success
		return justSpawned ? UpdateResult.SPAWNED : UpdateResult.NONE;
	}

	public static enum UpdateResult {
		SPAWNED, DESPAWNED, TARGETED_PLAYER, NONE
	}

	// ----- methods : spawn/despawn
	/** @return true if it was spawned, false if it was already spawned */
	public boolean spawn() {
		// already spawned
		if (spawned) {
			return false;
		}
		// spawn
		try {
			NpcProtocols.inst().spawn(player, getEntityId(), name, location, skinData, skinSignature);
			spawned = true;
			// update status and equipment
			updateStatus();
			updateEquipment();
			// success
			return true;
		} catch (Throwable exception) {
			if (!(exception instanceof IllegalArgumentException)) { // java.lang.IllegalArgumentException: No serializer found for class java.lang.Byte :confusedwat:
				exception.printStackTrace();
			}
			return false;
		}
	}

	/** @return true if it was despawned, false if it was already despawned */
	public boolean despawn() {
		// not spawned
		if (!spawned) {
			return false;
		}
		// despawn
		NpcProtocols.inst().remove(player, getEntityId());
		spawned = false;
		// success
		return true;
	}

	// ----- methods : location
	// ----- https://wiki.vg/Protocol#Player_Look
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
		return Pair.of((float) yaw, (float) pitch);
	}

	public void target(double yaw, double pitch) {
		// not spawned
		if (!spawned) {
			return;
		}
		// target
		location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), (float) yaw, (float) pitch);
		NpcProtocols.inst().sendTarget(player, getEntityId(), yaw, pitch);
	}

	public void move(Location location, boolean onGround) {
		// not in the same world
		if (!location.getWorld().equals(this.location.getWorld())) {
			return;
		}
		// too far away, teleport
		final Location previous = this.location;
		if (previous.distance(location) > 8d) {
			teleport(location);
			return;
		}
		// set location
		this.location = location;
		// update player
		if (spawned) {// already spawned
			NpcProtocols.inst().relativeMove(player, getEntityId(), previous, location, onGround);
		} else {// spawn
			spawn();
		}
	}

	public void teleport(Location location) {
		// set location
		this.location = location;
		// respawn if not spawned, if it's another world or if we're in 1.15 (teleport seems to bug, see #566)
		if (!spawned || !location.getWorld().equals(this.location.getWorld()) || Version.ATLEAST_1_15) {
			spawned = false;
			spawn();
		} else { // otherwise just teleport
			NpcProtocols.inst().teleport(player, getEntityId(), location);
		}
	}

	// ----- methods : status
	private void updateStatus() {
		// update status
		Map<Integer, Object> map = CollectionUtils.asMap(0, NPCStatus.getMasked(status.toArray(new NPCStatus[status.size()])));
		NpcProtocols.inst().sendMetadata(player, NpcProtocols.inst().createMetadata(map), getEntityId());
	}

	public void setStatus(NPCStatus... status) {
		setStatus(CollectionUtils.asList(status));
	}

	public void setStatus(Collection<NPCStatus> status) {
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

	// ----- methods : equipment
	/**
	 * @param slot the item slot (0 to 5)
	 * @param item the item stack
	 */
	public void setItem(int slot, ItemStack item) {
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
	public void setItems(ItemStack[] items) {
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
			ItemStack item = items[i];
			array[i] = Mat.isVoid(item) ? null : item;
		}
		// update inventory
		try {
			NpcProtocols.inst().sendInventory(player, getEntityId(), array);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
	}

	// ----- methods : misc
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

	public void animate(NPCAnimation animation) {
		NpcProtocols.inst().sendAnimation(player, getEntityId(), animation);
	}

	// ----- equals
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
		if (!(obj instanceof NPC)) return false;
		NPC other = (NPC) obj;
		return other.id == id && other.player.getUniqueId().equals(player.getUniqueId());
	}

}
