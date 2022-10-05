package com.guillaumevdn.gcore.lib.location;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public class Point {

	private String world;
	private int x, y, z;

	public Point(Location location) {
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public Point(Block block) {
		this(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
	}

	public Point(World world, int x, int y, int z) {
		this(world.getName(), x, y, z);
	}

	public Point(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// ----- get
	public World getWorld() {
		return Bukkit.getWorld(world);
	}

	public String getWorldName() {
		return world;
	}

	public Chunk getChunk() {
		return getWorld().getChunkAt(x >> 4, z >> 4);
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

	public Mat getType() {
		return Mat.fromBlock(toBlock()).orAir();
	}

	// ----- set
	public void setWorld(String world) {
		this.world = world;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	// ----- methods
	public Location toLocation() {
		World world = getWorld();
		return world == null ? null : new Location(world, x, y, z);
	}

	public Block toBlock() {
		World world = getWorld();
		return world == null ? null : getWorld().getBlockAt(x, y, z);
	}

	public void setType(Mat type) {
		Block block = toBlock();
		if (block != null) {
			type.setBlock(block);
		} else {
			GCore.inst().getMainLogger().warning("Couldn't set type " + type + " of block in unknown world " + toString());
		}
	}

	public boolean coordsEquals(Block block) {
		return block.getX() == x && block.getY() == y && block.getZ() == z;
	}

	public boolean coordsEquals(Location location) {
		return location.getX() == x && location.getY() == y && location.getZ() == z;
	}

	public double distance(Point other) {
		return Math.sqrt(Math.pow(other.getX() - this.getX(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d));
	}

	public double distance(Block other) {
		return Math.sqrt(Math.pow(other.getX() - this.getX(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d));
	}

	public double distance(Location location) {
		return Math.sqrt(Math.pow(location.getX() - this.getX(), 2d) + Math.pow(location.getZ() - this.getZ(), 2d) + Math.pow(location.getZ() - this.getZ(), 2d));
	}

	// ----- transform
	public Point getRelative(int x, int y, int z) {
		return new Point(world, this.x + x, this.y + y, this.z + z);
	}

	// ----- object
	@Override
	public String toString() {
		return Serializer.POINT.serialize(this);
	}

	public static Point fromString(String raw) {
		return Serializer.POINT.deserialize(raw);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
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
		Point other = (Point) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
