package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement;

import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.number.NumberUtils;

/**
 * @author GuillaumeVDN
 */
public final class Offset {

	private int gap;
	private int x, y, z;
	private List<OffsetBlocking> blocking;

	public Offset(int gap, int x, int y, int z, List<OffsetBlocking> blocking) {
		if (x == 0 && y == 0 && z == 0) {
			throw new IllegalArgumentException("invalid offset (x " + x + ", y " + y + ", z " + z + ")");
		}
		this.gap = gap;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocking = blocking;
	}

	// ----- get
	public int getGap() {
		return gap;
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

	public List<OffsetBlocking> getBlocking() {
		return blocking;
	}

	public boolean isStraight() {
		return (x == 0 && z != 0) || (x != 0 && z == 0);
	}

	// ----- do
	public double distanceToTargetIfApplied(Point applyTo, Point finalTarget) {
		double newX = applyTo.getX() + x;
		double newY = applyTo.getY() + y;
		double newZ = applyTo.getZ() + z;
		double tX = finalTarget.getX();
		double tY = finalTarget.getY();
		double tZ = finalTarget.getZ();
		return Math.sqrt(NumberUtils.square(newX - tX) + NumberUtils.square(newY - tY) + NumberUtils.square(newZ - tZ));
	}

	// ----- obj
	@Override
	public int hashCode() {
		return Objects.hash(gap, x, y, z, blocking);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj.hashCode() == hashCode();
	}

	@Override
	public String toString() {
		return "(" + gap + "," + x + "," + y + "," + z + ")";
	}

}
