package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;

/**
 * @author GuillaumeVDN
 */
public class OptimizedPathPoint {

	private Movement movementToPoint;
	private Point point;

	public OptimizedPathPoint(Movement movementToPoint, Point point) {
		this.movementToPoint = movementToPoint;
		this.point = point;
	}

	// ----- get
	@Nullable
	public Movement getMovementToPoint() {
		return movementToPoint;
	}

	public Point getPoint() {
		return point;
	}

	// ----- str
	@Override
	public String toString() {
		return point.toString();
	}

}
