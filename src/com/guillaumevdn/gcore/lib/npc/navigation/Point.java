package com.guillaumevdn.gcore.lib.npc.navigation;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.guillaumevdn.gcore.lib.util.Utils;

public class Point implements Cloneable {

	// base
	private final int x, y, z;

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point(Location location) {
		this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public Point(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	// get
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	// methods
	public double distance(Point other) {
		int offx = other.x - x;
		int offy = other.y - y;
		int offz = other.z - z;
		return Math.sqrt(offx * offx + offy * offy + offz * offz);
	}

	public double distance(Location other) {
		int offx = other.getBlockX() - x;
		int offy = other.getBlockY() - y;
		int offz = other.getBlockZ() - z;
		return Math.sqrt(offx * offx + offy * offy + offz * offz);
	}

	public Location toLocation(World world) {
		return new Location(world, x, y, z);
	}

	public Block toBlock(World world) {
		return world.getBlockAt(x, y, z);
	}

	// object
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (Utils.instanceOf(obj, Point.class)) {
			Point other = (Point) obj;
			return other.x == x && other.y == y && other.z == z;
		}
		return false;
	}

	@Override
	public Point clone() {
		return new Point(x, y, z);
	}

}
