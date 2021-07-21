package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import com.guillaumevdn.gcore.lib.concurrency.RWArrayList;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.wrapper.WrapperBoolean;

/**
 * @author GuillaumeVDN
 */
public final class Path {

	private RWArrayList<ExploringPathPoint> points;

	public Path(Point initialPoint) {
		this(new RWArrayList<>());
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

	public void iteratePoints(TriConsumer<ExploringPathPoint, WrapperBoolean, WrapperBoolean> consumer) {
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
