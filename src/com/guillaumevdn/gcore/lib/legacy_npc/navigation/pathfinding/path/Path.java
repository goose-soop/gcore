package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;

/**
 * @author GuillaumeVDN
 */
public class Path {

	private LinkedList<ExploringPathPoint> points;

	public Path(Point initialPoint) {
		this(new LinkedList<>());
		add(new ExploringPathPoint(null, initialPoint));
	}

	public Path(LinkedList<ExploringPathPoint> points) {
		this.points = points;
	}

	// get
	public List<ExploringPathPoint> getPoints() {
		return Collections.unmodifiableList(points);
	}

	public ExploringPathPoint getLast() {
		try {
			return points.getLast();
		} catch (NoSuchElementException ignored) {
			return null;
		}
	}

	// set
	public void add(ExploringPathPoint point) {
		points.add(point);
	}

	public void removeLast() {
		try {
			points.removeLast();
		} catch (NoSuchElementException ignored) {}
	}

}
