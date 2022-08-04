package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.lib.collection.IteratorControls;
import com.guillaumevdn.gcore.lib.concurrency.RWArrayList;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;

/**
 * @author GuillaumeVDN
 */
public final class Path {

	private RWArrayList<ExploringPathPoint> points;

	public Path(Point initialPoint) {
		this(new RWArrayList<>(5));
		add(new ExploringPathPoint(null, initialPoint));
	}

	public Path(RWArrayList<ExploringPathPoint> points) {
		this.points = points;
	}

	// ----- get
	public boolean isEmpty() {
		return points.isEmpty();
	}

	public ExploringPathPoint getLast() {
		return points.isEmpty() ? null : points.get(points.size() - 1);
	}

	public void iteratePoints(BiConsumer<ExploringPathPoint, IteratorControls> consumer) {
		points.iterate(consumer);
	}

	// ----- set
	public void add(ExploringPathPoint point) {
		points.add(point);
	}

	public void removeLast() {
		points.remove(points.size() - 1);
	}

}
