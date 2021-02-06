package com.guillaumevdn.gcore.lib.location.position.type.area;

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
public class PositionAreaOutside implements Position {

	private Location a, b;

	public PositionAreaOutside(Location a, Location b) {
		this.a = a;
		this.b = b;
	}

	// methods
	@Override
	public boolean match(Location loc) {
		if (loc == null) {
			return false;
		}
		return !loc.getWorld().equals(a.getWorld()) || !LocationUtils.isLocationContained(loc, a, b);
	}

	@Override
	public World getWorld() {
		return a.getWorld();
	}

	@Override
	public boolean canFindRandom() {
		return true;
	}

	@Override
	public Location findRandom() {
		return LocationUtils.findRandomOutsideArea(a, b, 1d);
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
		if (!loc.getWorld().equals(a.getWorld())) {
			return null;
		}
		return LocationUtils.isLocationContained(loc, a, b) ? loc.clone() : LocationUtils.findClosestOnAreaOutline(a, b, loc);
	}

	@Override
	public Location findGPSFor(Player player) {
		if (match(player)) {
			return null;
		}
		return LocationUtils.findClosestOnAreaOutline(a, b, player.getLocation());
	}

	@Override
	public boolean canFill() {
		return true;
	}

	@Override
	public void fill(Mat blockType, List<BlockState> blockStates) {
		LocationUtils.getAreaOutlineBlocks(a, b).forEach(block -> LocationUtils.setBlock(block, blockType, blockStates));
	}


}
