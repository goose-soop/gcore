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

}
