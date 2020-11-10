package com.guillaumevdn.gcore.lib.location.position.type.sphere;

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
public class PositionSphereInside implements Position {

	private Location center;
	private double radius;

	public PositionSphereInside(Location center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	// methods
	@Override
	public boolean match(Location loc) {
		if (loc == null) {
			return false;
		}
		return loc.getWorld().equals(center.getWorld()) && loc.distance(center) <= radius;
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
		return LocationUtils.findRandomInSphere(center, 0d, radius);
	}

	@Override
	public Location findClosestTo(Location loc) {
		if (!loc.getWorld().equals(center.getWorld())) {
			return null;
		}
		return loc.distance(center) <= radius ? loc.clone() : LocationUtils.findClosestOnSphereOutline(center, radius, loc);
	}

	@Override
	public Location findGPSFor(Player player) {
		if (match(player)) {
			return null;
		}
		if (!player.getWorld().equals(center.getWorld())) {
			return null;
		}
		return LocationUtils.findClosestOnSphereOutline(center, radius, player.getLocation());
	}

	@Override
	public boolean canFill() {
		return true;
	}

	@Override
	public void fill(Mat blockType, List<BlockState> blockStates) {
		LocationUtils.getSphereBlocks(center, (int) radius).forEach(block -> LocationUtils.setBlock(block, blockType, blockStates));
	}

}
