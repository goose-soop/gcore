package com.guillaumevdn.gcore.lib.location.position.type.single;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.location.LocationUtils;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;

/**
 * @author GuillaumeVDN
 */
public class PositionSingle implements Position {

	private Location location;
	private PointTolerance tolerance;
	private boolean allowGPS;

	public PositionSingle(Location location, PointTolerance tolerance) {
		this(location, tolerance, true);
	}

	public PositionSingle(Location location, PointTolerance tolerance, boolean allowGPS) {
		this.location = location.clone();
		location.setX((double) location.getBlockX() + 0.5d);
		location.setZ((double) location.getBlockZ() + 0.5d);
		this.tolerance = tolerance;
		this.allowGPS = allowGPS;
	}

	// ----- get
	public Location getLocation() {
		return location;
	}

	public PointTolerance getTolerance() {
		return tolerance;
	}

	// ----- methods
	@Override
	public boolean match(Location loc) {
		if (loc == null) {
			return false;
		}
		return tolerance.match(loc, location);
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}

	@Override
	public boolean canFindRandom() {
		return true;
	}

	@Override
	public Location findRandom() {
		return location;
	}

	@Override
	public int findSafeRandomMaxY() {
		return location.getBlockY() + 5;
	}

	@Override
	public Location findClosestTo(Location loc) {
		if (!loc.getWorld().equals(location.getWorld())) {
			return null;
		}
		return location;
	}

	@Override
	public MinMaxDouble getRandomSolidAndFreeAboveYBounds() {
		return MinMaxDouble.of(location.getY(), location.getY());
	}

	@Override
	public Location findGPSFor(Player player) {
		if (!allowGPS) {
			return null;
		}
		if (match(player)) {
			return null;
		}
		if (!player.getWorld().equals(location.getWorld())) {
			return null;
		}
		return location;
	}

	@Override
	public boolean canFill() {
		return true;
	}

	@Override
	public void fill(Mat blockType, List<BlockState> blockStates) {
		LocationUtils.setBlock(location.getBlock(), blockType, blockStates);
	}

}
