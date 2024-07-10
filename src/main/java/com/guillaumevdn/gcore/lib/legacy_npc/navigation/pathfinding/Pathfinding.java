package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding;

import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.PositionCache;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.MovementFinder;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.OptimizedPathPoint;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.Path;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.PathOptimizer;
import com.guillaumevdn.gcore.lib.location.PointTolerance;

/**
 * @author GuillaumeVDN
 */
public final class Pathfinding {

	// ----- properties
	private final World world;
	private final Point origin;
	private final Point target;
	private final PointTolerance tolerance;
	private final int entityHeight;
	private final MovementFinder movementFinder;

	// ----- callbacks
	private final BiConsumer<PathfindingResult, List<OptimizedPathPoint>> onDone; // B will either be the successful path, either the closest path

	// ----- active
	private final PathfindingTask runnable;
	private BukkitTask task = null;
	private PositionCache<Mat> typeCache; // valuesCache block types since we'll more than likely check the same blocks multiple times
	private PositionCache<Object> toIgnore;
	Object __VALUE = new Object();
	private Path path;

	// ----- construct
	/**
	 * @param origin is the origin block BELOW the entity's feet
	 * @param target is the target block BELOW the entity's feet
	 */
	public Pathfinding(World world, Point origin, Point target, PointTolerance tolerance, int entityHeight, MovementFinder movementFinder,
			BiConsumer<PathfindingResult, List<OptimizedPathPoint>> onDone) {
		this.world = world;
		this.origin = origin;
		this.tolerance = tolerance;
		this.target = target;
		this.entityHeight = entityHeight;
		this.movementFinder = movementFinder;
		this.onDone = onDone;
		this.runnable = new PathfindingTask(this);
	}

	// ----- get
	public World getWorld() {
		return world;
	}

	public Point getOrigin() {
		return origin;
	}

	public Point getTarget() {
		return target;
	}

	public PointTolerance getTolerance() {
		return tolerance;
	}

	public int getEntityHeight() {
		return entityHeight;
	}

	public MovementFinder getMovementFinder() {
		return movementFinder;
	}

	Path getPath() {
		return path;
	}

	public boolean isActive() {
		return task != null;
	}

	// ----- block
	public boolean mustIgnore(Point point) {
		return toIgnore.contains(point.getX(), point.getY(), point.getZ());
	}

	public void ignore(Point point) {
		toIgnore.add(point.getX(), point.getY(), point.getZ(), __VALUE);
	}

	public Mat getBlockType(Point point) {
		return typeCache.computeIfAbsent(point.getX(), point.getY(), point.getZ(),
				() -> Mat.fromBlock(world.getBlockAt(point.getX(), point.getY(), point.getZ())).orElse(CommonMats.AIR));
	}

	public boolean isTraversableOrWaterAndFreeAbove(Point point, Point comingFrom, int extraHeight) {
		Mat mat = getBlockType(point);
		return (mat.isWater() || mat.getData().isTraversable()) && isFreeAbove(point, comingFrom, extraHeight);
	}

	public boolean isSolidOrWaterAndFreeAbove(Point point, Point comingFrom, int extraHeight, boolean canSwim) {
		Mat mat = getBlockType(point);
		return ((canSwim && mat.isWater()) || !mat.getData().isTraversable()) && isFreeAbove(point, comingFrom, extraHeight);
	}

	public boolean isFreeAbove(Point point, Point comingFrom, int extraHeight) {
		int maxY = entityHeight + extraHeight;
		int nextY = 1;
		// maybe there's a door, check if it's open
		Mat bottomType = getBlockType(point.relative(0, 1, 0));
		if (bottomType.getData().isDoor()) { // FIXME : see if door is open from 'comingFrom'
		}
		// check remaining height
		for (int y = nextY; y <= maxY; ++y) {
			Mat mat = getBlockType(point.relative(0, y, 0));
			if (!mat.getData().isTraversable() || mat.isWater() /* don't accept water here */) {
				return false;
			}
		}
		// we good
		return true;
	}

	// ----- control
	public void start() {
		if (task == null) {
			// reset data
			typeCache = new PositionCache(100, 100, 5);
			toIgnore = new PositionCache(100, 100, 5);
			ignore(origin);
			path = new Path(origin);
			// start
			task = runnable.runTaskTimerAsynchronously(GCore.inst(), 0L, 1L);
		}
	}

	public void end(PathfindingResult result) {
		if (task != null) {
			task.cancel();
			task = null;
			onDone.accept(result, path.isEmpty() || result.equals(PathfindingResult.CANCEL) ? null : PathOptimizer.optimize(path, this));
		}
	}

	// ----- FIXME : when animating, make sure we're animating slabs correctly

}
