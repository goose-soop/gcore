package com.guillaumevdn.gcore.lib.location.position.type.cylinder;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.location.LocationUtils;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;

/**
 * @author GuillaumeVDN
 */
public class PositionCylinderInside implements Position {

	private Location center;
	private double radius;
	private boolean allowGPS;

	public PositionCylinderInside(Location center, double radius) {
		this(center, radius, true);
	}

	public PositionCylinderInside(Location center, double radius, boolean allowGPS) {
		this.center = center.clone();
		center.setY(1d);
		this.radius = radius;
		this.allowGPS = allowGPS;
	}

	// ----- methods
	@Override
	public boolean match(Location loc) {
		if (loc == null) {
			return false;
		}
		if (!loc.getWorld().equals(center.getWorld())) {
			return false;
		}
		loc = loc.clone();
		loc.setY(1d);
		return loc.distance(center) <= radius;
	}

	@Override
	public World getWorld() {
		return center.getWorld();
	}

	@Override
	public boolean canFindRandom() {
		return true;
	}

	@Override
	public Location findRandom() {
		return LocationUtils.findRandomInCylinder(center, 0d, radius);
	}

	@Override
	public int findSafeRandomMaxY() {
		return 255;
	}

	@Override
	public MinMaxDouble getRandomSolidAndFreeAboveYBounds() {
		return MinMaxDouble.of(0d, 255d);
	}

	@Override
	public Location findClosestTo(Location loc) {
		if (!loc.getWorld().equals(center.getWorld())) {
			return null;
		}
		return match(loc) ? loc.clone() : LocationUtils.findClosestOnCylinderOutline(center, radius, loc);
	}

	@Override
	public Location findGPSFor(Player player) {
		if (!allowGPS) {
			return null;
		}
		if (match(player)) {
			return null;
		}
		if (!player.getWorld().equals(center.getWorld())) {
			return null;
		}
		return LocationUtils.findClosestOnCylinderOutline(center, radius, player.getLocation());
	}

	@Override
	public boolean canFill() {
		return false;
	}

	@Override
	public void fill(Mat blockType, List<BlockState> blockStates) {
	}

}
