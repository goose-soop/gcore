package be.pyrrh4.pyrcore.lib.npc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import be.pyrrh4.pyrcore.lib.util.Utils;

public class NPC {

	// base
	private String id;
	private int entityId;
	private String name;
	private Location location;
	private double targetDistance;
	private Set<Player> allowed = new HashSet<Player>();
	private Set<Player> created = new HashSet<Player>();

	public NPC(String id, int entityId, String name, Location location, double targetDistance) {
		this.id = id;
		this.entityId = entityId;
		this.name = name;
		this.location = location;
		this.targetDistance = targetDistance;
	}

	// get
	public String getId() {
		return id;
	}

	public int getEntityId() {
		return entityId;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public double getTargetDistance() {
		return targetDistance;
	}

	public Set<Player> getAllowed() {
		return Collections.unmodifiableSet(allowed);
	}

	public Set<Player> getCreated() {
		return Collections.unmodifiableSet(created);
	}

	// set
	// TODO

	// methods
	public void refresh() {
		for (Player player : Utils.getOnlinePlayers()) {
			// not supposed to see it
			if (!allowed.contains(player)) {
				despawn(player);// eventually despawn
				continue;
			}
			// invalid world/location
			double distance = Utils.distance(location, player.getLocation());
			if (distance < 0d || distance > 50d) {
				despawn(player);// eventually despawn
				continue;
			}
			// eventually spawn
			despawn(player);
			// eventually target player
			if (targetDistance > 0d && distance <= targetDistance) {
				target(location, player);
			}
		}
	}

	private void spawn(Player player) {
		// not created
		if (!created.contains(player)) {
			return;
		}
		// despawn

	}

	private void despawn(Player player) {
		// already created
		if (created.contains(player)) {
			return;
		}
		// spawn

	}

	private void target(Location target, Player player) {
		target(getLocalAngle(new Vector(target.getX(), 0.0, location.getZ()), target.toVector()), location.getPitch(), player);
	}

	private void target(double yaw, double pitch, Player player) {

		Ana.getNPC().sendLookNPC(player, entityId, yaw, pitch);
	}

	// utils
	private float getLocalAngle(Vector vector, Vector vector2) {
		float n = (float) Math.toDegrees(Math.atan2(vector2.getZ() - vector.getZ(), vector2.getX() - vector.getX())) - 90.0f;
		return n < 0.0f ? n + 360.0f : n;
	}

}
