package com.guillaumevdn.gcore.lib.location;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public class VectorPoint {

	private int x, y, z;

	private VectorPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// ----- get
	public Chunk getChunk(World world) {
		return world.getChunkAt(x >> 4, z >> 4);
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

	public Mat getType(World world) {
		return Mat.fromBlock(toBlock(world)).orAir();
	}

	// ----- set
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
	public Location toLocation(World world) {
		return new Location(world, x, y, z);
	}

	public Block toBlock(World world) {
		return world.getBlockAt(x, y, z);
	}

	public void setType(World world, Mat type) {
		type.setBlock(toBlock(world));
	}

	public boolean coordsEquals(Block block) {
		return block.getX() == x && block.getY() == y && block.getZ() == z;
	}

	public boolean coordsEquals(Location location) {
		return location.getX() == x && location.getY() == y && location.getZ() == z;
	}

	public double distance(VectorPoint other) {
		return Math.sqrt(Math.pow(other.getX() - this.getX(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d));
	}

	public double distance(Block other) {
		return Math.sqrt(Math.pow(other.getX() - this.getX(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d) + Math.pow(other.getZ() - this.getZ(), 2d));
	}

	public double distance(Location location) {
		return Math
				.sqrt(Math.pow(location.getX() - this.getX(), 2d) + Math.pow(location.getZ() - this.getZ(), 2d) + Math.pow(location.getZ() - this.getZ(), 2d));
	}

	// ----- transform
	public VectorPoint getRelative(int x, int y, int z) {
		return new VectorPoint(this.x + x, this.y + y, this.z + z);
	}

	// ----- object
	@Override
	public String toString() {
		return Serializer.VECTOR_POINT.serialize(this);
	}

	public static VectorPoint fromString(String raw) {
		return Serializer.VECTOR_POINT.deserialize(raw);
	}

	public static VectorPoint from(Location location) {
		return new VectorPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static VectorPoint from(Block block) {
		return new VectorPoint(block.getX(), block.getY(), block.getZ());
	}

	public static VectorPoint from(int x, int y, int z) {
		return new VectorPoint(x, y, z);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorPoint other = (VectorPoint) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
