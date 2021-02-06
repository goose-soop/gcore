package com.guillaumevdn.gcore.lib.location.position;

import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.location.LocationUtils;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;

/**
 * @author GuillaumeVDN
 */
public interface Position {

	// get
	World getWorld();

	// match
	default boolean match(Player player) {
		return match(player.getLocation());
	}
	boolean match(Location loc);

	// random
	boolean canFindRandom();
	Location findRandom();
	int findSafeRandomMaxY();

	default Location findMaybeSafeRandom(int entityHeight, int maxTries) {
		if (!canFindRandom()) {
			return null;
		}
		int maxY = findSafeRandomMaxY();
		for (int tries = 0; tries <= maxTries; ++tries) {
			Location random = LocationUtils.trySafeize(findRandom(), maxY, entityHeight);
			if (random != null) {
				return random;
			}
		}
		return findRandom();
	}

	// random solid and free above
	MinMaxDouble getRandomSolidAndFreeAboveYBounds();

	default boolean canFindRandomSolidAndFreeAbove() {
		return canFindRandom() && getRandomSolidAndFreeAboveYBounds() != null;
	}

	default Location findRandomSolidAndFreeAbove(int height) {
		MinMaxDouble y = getRandomSolidAndFreeAboveYBounds();
		Location loc = findRandom().clone();
		loc.setY(y.getMin());
		IntPredicate isYFree = offy -> {
			Mat m = loc.getBlockY() + offy >= 256 ? null : Mat.fromBlock(loc.getBlock().getRelative(0, offy, 0)).orNull();
			return m != null && m.getData().isTraversable() && match(loc);
		};
		for (; loc.getY() <= y.getMax(); loc.add(0d, 1d, 0d)) {
			Mat mat = Mat.fromBlock(loc.getBlock()).orNull();
			if (mat != null && !mat.getData().isTraversable() && IntStream.range(1, height + 1).allMatch(isYFree)) {
				return loc;
			}
		}
		return null;
	}

	// closest
	default Location findClosestTo(Player player) {
		return findClosestTo(player.getLocation());
	}
	Location findClosestTo(Location loc);

	// gps
	Location findGPSFor(Player player);

	// fill
	boolean canFill();
	default void fill(Mat blockType, List<BlockState> blockStates) {
		throw new UnsupportedOperationException();
	}

}
