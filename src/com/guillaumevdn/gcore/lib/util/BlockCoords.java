package com.guillaumevdn.gcore.lib.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockCoords {

	// base
	private World world;
	private int x, y, z;

	public BlockCoords(Location location) {
		this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public BlockCoords(Block block) {
		this(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public BlockCoords(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// get
	public World getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Location toLocation() {
		return new Location(world, x, y, z);
	}

	public Block toBlock() {
		return world.getBlockAt(x, y, z);
	}

	@Override
	public String toString() {
		return world.getName() + "," + x + "," + y + "," + z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.getName().hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockCoords other = (BlockCoords) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
        return z == other.z;
    }

	// methods
	public double distance(int x, int y, int z) {
		int offx = x - this.x;
		int offy = y - this.y;
		int offz = z - this.z;
		return Math.sqrt(offx * offx + offy * offy + offz * offz);
	}

}
