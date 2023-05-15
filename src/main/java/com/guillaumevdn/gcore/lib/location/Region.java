package com.guillaumevdn.gcore.lib.location;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class Region {

	private final Point min;
	private final Point max;

	public Region(Point a, Point b) {
		final Pair<Point, Point> minMax = LocationUtils.minMaxPoints(a, b);
		this.min = minMax.getA();
		this.max = minMax.getB();
	}

	public World getWorld() {
		return min.getWorld();
	}

	public Point getMin() {
		return min;
	}

	public Point getMax() {
		return max;
	}

	public List<Player> getPlayersInside() {
		return getWorld().getPlayers().stream().filter(pl -> contains(pl)).collect(Collectors.toList());
	}

	public boolean contains(Player player) {
		return contains(player.getLocation());
	}

	public boolean contains(Location location) {
		return LocationUtils.isLocationContained(location, min, max);
	}

	@Override
	public String toString() {
		return "(" + min + "," + max + ")";
	}

}
