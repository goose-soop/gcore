package com.guillaumevdn.gcore.lib.location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.collection.PositionCache;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;
import com.guillaumevdn.gcore.lib.number.MinMaxInteger;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public final class LocationUtils {

	// ----- get
	public static List<Block> getSphereBlocks(Location center, int radius) {
		List<Block> result = new ArrayList<>(); // a list so we don't check for duplicates every time we add one
		for (int offx = -radius; offx <= radius; ++offx) {
			for (int offy = -radius; offy <= radius; ++offy) {
				for (int offz = -radius; offz <= radius; ++offz) {
					Location b = center.clone().add(offx, offy, offz);
					if (b.distance(center) <= radius) {
						result.add(b.getBlock());
					}
				}
			}
		}
		return result;
	}

	public static List<Block> getBlocksOnSphere(Location center, int radius) {
		List<Block> result = new ArrayList<>(); // a list so we don't check for duplicates every time we add one
		for (int offx = -radius; offx <= radius; ++offx) {
			for (int offy = -radius; offy <= radius; ++offy) {
				for (int offz = -radius; offz <= radius; ++offz) {
					Location b = center.clone().add(offx, offy, offz);
					if (b.distance(center) >= radius - 1) {
						result.add(b.getBlock());
					}
				}
			}
		}
		return result;
	}

	public static List<Block> getAreaBlocks(Location a, Location b) {
		List<Block> result = new ArrayList<>(); // a list so we don't check for duplicates every time we add one
		MinMaxInteger xm = MinMaxInteger.of(a.getBlockX(), b.getBlockX());
		MinMaxInteger ym = MinMaxInteger.of(a.getBlockY(), b.getBlockY());
		MinMaxInteger zm = MinMaxInteger.of(a.getBlockZ(), b.getBlockZ());
		for (int x = xm.getMin(); x < xm.getMax(); ++x) {
			for (int y = ym.getMin(); y < ym.getMax(); ++y) {
				for (int z = zm.getMin(); z < zm.getMax(); ++z) {
					result.add(a.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return result;
	}

	// ----- https://www.spigotmc.org/threads/how-to-get-blocks-around-a-rectangle.352010/
	public static Set<Block> getAreaOutlineBlocks(Location a, Location b) {
		Set<Block> result = new HashSet<>();
		MinMaxInteger xm = MinMaxInteger.of(a.getBlockX(), b.getBlockX());
		MinMaxInteger ym = MinMaxInteger.of(a.getBlockY(), b.getBlockY());
		MinMaxInteger zm = MinMaxInteger.of(a.getBlockZ(), b.getBlockZ());
		for (int y = ym.getMin(); y <= ym.getMax(); ++y) {
			for (int x = xm.getMin(); x <= xm.getMax(); ++x) {
				result.add(a.getWorld().getBlockAt(x, y, zm.getMin()));
				result.add(a.getWorld().getBlockAt(x, y, zm.getMax()));
			}
			for (int z = zm.getMin(); z <= zm.getMax(); ++z) {
				result.add(a.getWorld().getBlockAt(xm.getMin(), y, z));
				result.add(a.getWorld().getBlockAt(xm.getMax(), y, z));
			}
		}
		return result;
	}

	public static Location centerOf(Point min, Point max) {
		return new Location(min.getWorld(), (min.getX() + max.getX()) / 2d, (min.getY() + max.getY()) / 2d, (min.getZ() + max.getZ()) / 2d);
	}

	public static Location centerOf(Location min, Location max) {
		return new Location(min.getWorld(), (min.getX() + max.getX()) / 2d, (min.getY() + max.getY()) / 2d, (min.getZ() + max.getZ()) / 2d);
	}

	public static Location findRandomInArea(Location a, Location b) {
		MinMaxDouble x = MinMaxDouble.of(a.getX(), b.getX());
		MinMaxDouble y = MinMaxDouble.of(a.getY(), b.getY());
		MinMaxDouble z = MinMaxDouble.of(a.getZ(), b.getZ());
		return new Location(a.getWorld(), NumberUtils.random(x.getMin(), x.getMax()), NumberUtils.random(y.getMin(), y.getMax()), NumberUtils.random(z.getMin(), z.getMax()));
	}

	public static Point findRandomInArea(Point a, Point b) {
		MinMaxInteger x = MinMaxInteger.of(a.getX(), b.getX());
		MinMaxInteger y = MinMaxInteger.of(a.getY(), b.getY());
		MinMaxInteger z = MinMaxInteger.of(a.getZ(), b.getZ());
		return new Point(a.getWorld(), NumberUtils.random(x.getMin(), x.getMax()), NumberUtils.random(y.getMin(), y.getMax()), NumberUtils.random(z.getMin(), z.getMax()));
	}

	public static Location findRandomOutsideArea(Location a, Location b, double rangeMultiplier) {
		MinMaxDouble mmx = MinMaxDouble.of(a.getX(), b.getX());
		MinMaxDouble mmy = MinMaxDouble.of(a.getY(), b.getY());
		MinMaxDouble mmz = MinMaxDouble.of(a.getZ(), b.getZ());
		double sizex = (mmx.getMax() - mmx.getMin()) * rangeMultiplier;
		double sizey = (mmy.getMax() - mmy.getMin()) * rangeMultiplier;
		double sizez = (mmz.getMax() - mmz.getMin()) * rangeMultiplier;
		double x = NumberUtils.random(mmx.getMin() - sizex, mmx.getMax() + sizex);
		double z = x < mmx.getMin() || x > mmx.getMax() ? NumberUtils.random(mmz.getMin() - sizez, mmz.getMax() + sizez) : (NumberUtils.random() ? NumberUtils.random(mmz.getMin() - sizez, mmz.getMin()) : NumberUtils.random(mmz.getMax(), mmz.getMax() + sizez));
		double y = (x < mmx.getMin() || x > mmx.getMax()) && (z < mmz.getMin() || z > mmz.getMax()) ? NumberUtils.random(mmy.getMin() - sizey, mmy.getMax() + sizey) : (NumberUtils.random() ? NumberUtils.random(mmy.getMin() - sizey, mmy.getMin()) : NumberUtils.random(mmy.getMax(), mmy.getMax() + sizey));
		return new Location(a.getWorld(), x, y, z);
	}

	public static Location getRandomInAreaLoc(Point a, Point b) {
		MinMaxInteger x = MinMaxInteger.of(a.getX(), b.getX());
		MinMaxInteger y = MinMaxInteger.of(a.getY(), b.getY());
		MinMaxInteger z = MinMaxInteger.of(a.getZ(), b.getZ());
		return new Location(a.getWorld(), NumberUtils.random((double) x.getMin(), (double) x.getMax()), NumberUtils.random((double) y.getMin(), (double) y.getMax()), NumberUtils.random((double) z.getMin(), (double) z.getMax()));
	}

	// ----- https://math.stackexchange.com/questions/831109/closest-point-on-a-sphere-to-another-point
	public static Location findClosestOnSphereOutline(Location center, double radius, Location point) {
		double div = Math.sqrt(Math.pow(point.getX() - center.getX(), 2d) + Math.pow(point.getY() - center.getY(), 2d) + Math.pow(point.getZ() - center.getZ(), 2d));
		double x = center.getX() + (radius * (point.getX() - center.getX())) / div;
		double y = center.getY() + (radius * (point.getY() - center.getY())) / div;
		double z = center.getZ() + (radius * (point.getZ() - center.getZ())) / div;
		return new Location(center.getWorld(), x, y, z);
	}

	public static Location findClosestOnCylinderOutline(Location center, double radius, Location point) {
		double div = Math.sqrt(Math.pow(point.getX() - center.getX(), 2d) + Math.pow(point.getZ() - center.getZ(), 2d));
		double x = center.getX() + (radius * (point.getX() - center.getX())) / div;
		double z = center.getZ() + (radius * (point.getZ() - center.getZ())) / div;
		return new Location(center.getWorld(), x, point.getY(), z);
	}

	// ----- https://gamedev.stackexchange.com/questions/26713/calculate-random-points-pixel-within-a-circle-image
	public static Location findRandomInSphere(Location center, double minRadius, double maxRadius) {
		double angle = NumberUtils.random(0d, 1d) * Math.PI * 2d;
		double rad = Math.sqrt(NumberUtils.random(minRadius, maxRadius));
		int x = (int) (center.getX() + rad * Math.cos(angle));
		int z = (int) (center.getZ() + rad * Math.sin(angle));
		int highest = center.getWorld().getHighestBlockYAt(x, z);
		int y = center.clone().add(0d, (double) highest, 0d).distance(center) >= minRadius && center.clone().add(0d, (double) highest, 0d).distance(center) <= maxRadius ? highest : center.getBlockY();
		return center.getWorld().getBlockAt(x, y, z).getLocation();
	}

	public static Location findRandomInCylinder(Location center, double minRadius, double maxRadius) {
		double angle = NumberUtils.random(0d, 1d) * Math.PI * 2d;
		double rad = Math.sqrt(NumberUtils.random(minRadius, maxRadius));
		int x = (int) (center.getX() + rad * Math.cos(angle));
		int z = (int) (center.getZ() + rad * Math.sin(angle));
		int y = center.getWorld().getHighestBlockYAt(x, z);
		return center.getWorld().getBlockAt(x, y, z).getLocation();
	}

	// ----- https://gamedev.stackexchange.com/questions/44483/how-do-i-calculate-distance-between-a-point-and-an-axis-aligned-rectangle
	public static Location findClosestOnAreaOutline(Location a, Location b, Location point) {
		MinMaxDouble mmx = MinMaxDouble.of(a.getX(), b.getX());
		MinMaxDouble mmy = MinMaxDouble.of(a.getY(), b.getY());
		MinMaxDouble mmz = MinMaxDouble.of(a.getZ(), b.getZ());
		double x = Math.max(Math.min(point.getX(), mmx.getMax()), mmx.getMin());
		double z = Math.max(Math.min(point.getZ(), mmz.getMax()), mmz.getMin());
		double y = NumberUtils.inRange(point.getY(), mmy.getMin(), mmy.getMax());
		return new Location(point.getWorld(), x, y, z);
	}

	// ----- check
	public static boolean isPointContained(Point point, Point a, Point b) {
		MinMaxInteger x = MinMaxInteger.of(a.getX(), b.getX());
		MinMaxInteger y = MinMaxInteger.of(a.getY(), b.getY());
		MinMaxInteger z = MinMaxInteger.of(a.getZ(), b.getZ());
		return point.getY() >= y.getMin() && point.getY() <= y.getMax() && point.getX() >= x.getMin() && point.getX() <= x.getMax() && point.getZ() >= z.getMin() && point.getZ() <= z.getMax();
	}

	public static boolean isPlayerContained(Player player, Point a, Point b) {
		return isLocationContained(player.getLocation(), a, b);
	}

	public static boolean isLocationContained(Block block, Point a, Point b) {
		MinMaxInteger x = MinMaxInteger.of(a.getX(), b.getX());
		MinMaxInteger y = MinMaxInteger.of(a.getY(), b.getY());
		MinMaxInteger z = MinMaxInteger.of(a.getZ(), b.getZ());
		return block.getY() >= y.getMin() && block.getY() <= y.getMax() && block.getX() >= x.getMin() && block.getX() <= x.getMax() && block.getZ() >= z.getMin() && block.getZ() <= z.getMax();
	}

	public static boolean isLocationContained(Location location, Point a, Point b) {
		MinMaxDouble x = MinMaxDouble.of(a.getX(), b.getX());
		MinMaxDouble y = MinMaxDouble.of(a.getY(), b.getY());
		MinMaxDouble z = MinMaxDouble.of(a.getZ(), b.getZ());
		return location.getY() >= y.getMin() && location.getY() <= y.getMax() && location.getX() >= x.getMin() && location.getX() <= x.getMax() && location.getZ() >= z.getMin() && location.getZ() <= z.getMax();
	}

	public static boolean isLocationContained(Location location, Location a, Location b) {
		MinMaxDouble x = MinMaxDouble.of(a.getX(), b.getX());
		MinMaxDouble y = MinMaxDouble.of(a.getY(), b.getY());
		MinMaxDouble z = MinMaxDouble.of(a.getZ(), b.getZ());
		return location.getY() >= y.getMin() && location.getY() <= y.getMax() && location.getX() >= x.getMin() && location.getX() <= x.getMax() && location.getZ() >= z.getMin() && location.getZ() <= z.getMax();
	}

	public static Pair<Point, Point> minMaxPoints(Point a, Point b) {
		MinMaxInteger x = MinMaxInteger.of(a.getX(), b.getX());
		MinMaxInteger y = MinMaxInteger.of(a.getY(), b.getY());
		MinMaxInteger z = MinMaxInteger.of(a.getZ(), b.getZ());
		return Pair.of(new Point(a.getWorldName(), x.getMin(), y.getMin(), z.getMin()), new Point(b.getWorldName(), x.getMax(), y.getMax(), z.getMax()));
	}

	public static boolean regionOverlap(Point a1, Point a2, Point b1, Point b2) {
		Pair<Point, Point> minMaxA = minMaxPoints(a1, a2);
		Pair<Point, Point> minMaxB = minMaxPoints(b1, b2);
		return regionOverlapMinMax(minMaxA.getA(), minMaxA.getB(), minMaxB.getA(), minMaxB.getB());
	}

	public static boolean regionOverlapMinMax(Point minA, Point maxA, Point minB, Point maxB) {
		if (maxB.getX() < minA.getX()) return false;
		if (maxB.getY() < minA.getY()) return false;
		if (maxB.getZ() < minA.getZ()) return false;
		if (minB.getX() > maxA.getX()) return false;
		if (minB.getY() > maxA.getY()) return false;
		if (minB.getZ() > maxA.getZ()) return false;
		return true;
	}

	public static boolean coordsEquals(Location a, Location b) {
		return a.getWorld().equals(b.getWorld()) && a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
	}

	public static void setBlock(Block block, Mat blockType, List<BlockState> blockStates) {
		blockType.setBlock(block);
		if (blockStates != null) {
			blockStates.forEach(state -> state.getType().set(state, block));
		}
	}

	// ----- vector

	// https://stackoverflow.com/questions/31225062/rotating-a-vector-by-angle-and-axis-in-java
	/**
	 * @param axis est un vecteur normal au plan dans lequel on veut faire tourner le vecteur
	 * @param angle est un angle en radians
	 */
	private static final Vector rotationAxis = new Vector(0d, 1d, 0d);
	public static Vector rotateVector(Vector vec, double angleInRadians) {
		double x = vec.getX();
		double y = vec.getY();
		double z = vec.getZ();
		double u = rotationAxis.getX();
		double v = rotationAxis.getY();
		double w = rotationAxis.getZ();
		double rotatedX = u * (u * x + v * y + w * z) * (1d - Math.cos(angleInRadians)) + x * Math.cos(angleInRadians) + (-w * y + v * z) * Math.sin(angleInRadians);
		double rotatedY = v * (u * x + v * y + w * z) * (1d - Math.cos(angleInRadians)) + y * Math.cos(angleInRadians) + (w * x - u * z) * Math.sin(angleInRadians);
		double rotatedZ = w * (u * x + v * y + w * z) * (1d - Math.cos(angleInRadians)) + z * Math.cos(angleInRadians) + (-v * x + u * y) * Math.sin(angleInRadians);
		return new Vector(rotatedX, rotatedY, rotatedZ);
	}

	// ----- block type
	public static boolean isTraversable(Block block) {
		return Mat.fromBlock(block).orAir().getData().isTraversable();
	}

	@Nullable
	public static Location trySafeize(Location base, int maxY, int entityHeight) {
		PositionCache<Mat> cache = new PositionCache<>(1, 1, maxY - base.getBlockY() + 1);
		main: for (Block block = base.getBlock(); block.getY() <= maxY; block = block.getRelative(BlockFace.UP)) {
			final Block bl = block;  // pepega
			// look for a solid base
			if (!cache.computeIfAbsent(0, block.getY(), 0, () -> Mat.fromBlock(bl).orAir()).getData().isTraversable()) {
				// ensure there's space above
				for (int i = 1; i <= entityHeight; ++i) {
					final Block b = block.getRelative(0, i, 0);
					if (!cache.computeIfAbsent(0, b.getY(), 0, () -> Mat.fromBlock(b).orAir()).getData().isTraversable()) {
						continue main;
					}
				}
				// we're good
				return bl.getLocation().clone().add(.5d, 0d, .5d);
			}
		}
		return null;
	}

}
