package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Pathfinding;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.MovementFinder;

/**
 * @author GuillaumeVDN
 */
public final class PathOptimizer {

	public static List<OptimizedPathPoint> optimize(Path path, Pathfinding finder) {
		MovementFinder optimizer = MovementFinder.ofProperties(0, finder.getMovementFinder().getMinBottomVertical(),
				finder.getMovementFinder().canStickToWall(), false);
		List<OptimizedPathPoint> points = new ArrayList<>();
		path.iteratePoints((next, it) -> points.add(new OptimizedPathPoint(next.getMovementToPoint(), next.getPoint())));
		// connect points that could be connected (close, only use gap 0)
		connect: for (;;) {
			for (int i = 0; i < points.size(); ++i) {
				for (int j = i + 2; j < points.size(); ++j) {
					// try to find a new movement
					Point from = points.get(i).getPoint();
					Point to = points.get(j).getPoint();
					Movement movement = optimizer.findMovement(from, to, finder, false);
					if (movement != null) {
						// remove old movements
						for (int k = 0; k < j - i; ++k) {
							points.remove(i + 1);
						}
						// add movement
						points.add(i + 1, new OptimizedPathPoint(movement, to));
						continue connect;
					}
				}
			}
			break;
		}
		// done
		return points;
	}

}
