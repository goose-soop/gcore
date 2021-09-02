package com.guillaumevdn.gcore.lib.collection;

import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.location.Point;

/**
 * @author GuillaumeVDN
 */
public final class PositionCache<V> {

	private RWHashMap<Integer, RWHashMap<Integer, RWHashMap<Integer, V>>> map;
	private int initialCapacityZ, initialCapacityY;

	public PositionCache() {
		this(250, 1, 1);
	}

	public PositionCache(int initialCapacityX, int initialCapacityZ, int initialCapacityY) {
		map = new RWHashMap<>(initialCapacityX, 0.75f);
	}

	// ----- get
	public boolean contains(int x, int y, int z) {
		return getY(x, z).containsKey(y);
	}

	public boolean contains(Block block) {
		return contains(block.getX(), block.getY(), block.getZ());
	}

	public boolean contains(BlockState block) {
		return contains(block.getX(), block.getY(), block.getZ());
	}

	public boolean contains(Point point) {
		return contains(point.getX(), point.getY(), point.getZ());
	}

	public boolean contains(Location location) {
		return contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public V computeIfAbsent(int x, int y, int z, Supplier<V> ifAbsent) {
		return getY(x, z).computeIfAbsent(y, __ -> ifAbsent.get());
	}

	// ----- set
	public void add(int x, int y, int z, V value) {
		getY(x, z).put(y, value);
	}

	public void add(Block block, V value) {
		add(block.getX(), block.getY(), block.getZ(), value);
	}

	public void add(BlockState block, V value) {
		add(block.getX(), block.getY(), block.getZ(), value);
	}

	public void add(Point point, V value) {
		add(point.getX(), point.getY(), point.getZ(), value);
	}

	public void add(Location location, V value) {
		add(location.getBlockX(), location.getBlockY(), location.getBlockZ(), value);
	}

	public void remove(int x, int y, int z) {
		getY(x, z).remove(y);
	}

	public void remove(Block block) {
		remove(block.getX(), block.getY(), block.getZ());
	}

	public void remove(Point point) {
		remove(point.getX(), point.getY(), point.getZ());
	}

	public void remove(Location location) {
		remove(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public void clear() {
		map.clear();
	}

	// ----- util
	private RWHashMap<Integer, V> getY(int x, int z) {
		return map.computeIfAbsent(x, __ -> new RWHashMap<>(initialCapacityZ, 0.75f)).computeIfAbsent(z, __ -> new RWHashMap<>(initialCapacityY, 0.75f));
	}

}
