package com.guillaumevdn.gcore.lib.location;

import org.bukkit.Location;

/**
 * @author GuillaumeVDN
 */
public enum PointTolerance {

	COORDS(null),
	STRICT(0.75d),
	REGULAR(1.5d),
	LENIENT(2.25d);

	private Double distance;

	PointTolerance(Double distance) {
		this.distance = distance;
	}

	public boolean match(Location location, Location reference) {
		if (!location.getWorld().equals(reference.getWorld())) {
			return false;
		}
		return distance == null ? LocationUtils.coordsEquals(location, reference) : location.distance(reference) <= distance;
	}

}
