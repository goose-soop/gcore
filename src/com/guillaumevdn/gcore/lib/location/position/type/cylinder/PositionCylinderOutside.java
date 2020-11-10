package com.guillaumevdn.gcore.lib.location.position.type.cylinder;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.location.LocationUtils;
import com.guillaumevdn.gcore.lib.location.position.Position;

/**
 * @author GuillaumeVDN
 */
public class PositionCylinderOutside implements Position {

	private Location center;
	private double radius;

	public PositionCylinderOutside(Location center, double radius) {
		this.center = center.clone();
		center.setY(1d);
		this.radius = radius;
	}

	// methods
	@Override
	public boolean match(Location loc) {
		if (loc == null) {
			return false;
		}
		if (!loc.getWorld().equals(center.getWorld())) {
			return true;
		}
		loc = loc.clone();
		loc.setY(1d);
		return loc.distance(center) > radius;
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
		return LocationUtils.findRandomInCylinder(center, radius, radius * 2d);
	}

	@Override
	public Location findClosestTo(Location loc) {
		if (!loc.getWorld().equals(center.getWorld())) {
			return loc;
		}
		return !match(loc) ? loc.clone() : LocationUtils.findClosestOnCylinderOutline(center, radius, loc);
	}

	@Override
	public Location findGPSFor(Player player) {
		if (match(player)) {
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
