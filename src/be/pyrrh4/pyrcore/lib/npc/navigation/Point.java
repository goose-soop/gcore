package be.pyrrh4.pyrcore.lib.npc.navigation;

import be.pyrrh4.pyrcore.lib.util.Utils;

public class Point {

	// base
	public int x, y, z;

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// methods
	public double distance(Point other) {
		int offx = other.x - x;
		int offy = other.y - y;
		int offz = other.z - z;
		return Math.sqrt(offx * offx + offy * offy + offz * offz);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

}
