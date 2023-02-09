package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Offset;

/**
 * @author GuillaumeVDN
 */
public class ExploringPathPoint {

	private Movement movementToPoint;
	private Point point;
	private Set<Offset> offsetsToIgnore = new HashSet<>();  // remember offsets to ignore from a here, since we might recheck the same origin multiple times (if a movement is found before checking all offsets, and later discontinued)

	public ExploringPathPoint(Movement movementToPoint, Point point) {
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

	public Set<Offset> getOffsetsToIgnore() {
		return offsetsToIgnore;
	}

	// ----- set
	public void ignoreOffset(Offset offset) {
		offsetsToIgnore.add(offset);
	}

	// ----- str
	@Override
	public String toString() {
		return point.toString();
	}

}
