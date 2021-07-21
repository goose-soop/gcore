package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Offset;

/**
 * @author GuillaumeVDN
 */
public final class Point {

	private int x, y, z;

	public Point(Location loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public Point(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// ----- get
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	// ----- distance
	public double distance(Point other) {
		return Math.sqrt(NumberUtils.square(x - other.x) + NumberUtils.square(y - other.y) + NumberUtils.square(z - other.z));
	}

	// ----- manipulation
	public Location toLocation(World world) {
		return new Location(world, x + 0.5d, y, z + 0.5d);
	}

	public Vector vectorTo(Point target) {
		return new Vector(target.x - x, 0d, target.z - z);
	}

	@Override
	public Point clone() {
		return new Point(x, y, z);
	}

	public Point relative(Offset offset) {
		return new Point(x + offset.getX(), y + offset.getY(), z + offset.getZ());
	}

	public Point relative(int offx, int offy, int offz) {
		return new Point(x + offx, y + offy, z + offz);
	}

	// ----- object
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

}
