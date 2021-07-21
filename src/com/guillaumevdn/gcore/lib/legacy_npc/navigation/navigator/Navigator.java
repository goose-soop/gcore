package com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitTask;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.legacy_npc.NPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Pathfinding;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.PathfindingResult;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.MovementFinder;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.OptimizedPathPoint;
import com.guillaumevdn.gcore.lib.location.PointTolerance;

/**
 * @author GuillaumeVDN
 */
public abstract class Navigator {

	private final World world;
	private final Point target;
	private final PointTolerance tolerance;
	final MovementFinder movementFinder;
	private final Consumer<NavigatorResult> onDone;

	private final NavigatorTask runnable;

	Pathfinding finder = null;
	List<OptimizedPathPoint> path = null;
	List<AnimatedMovement> movements = null;
	int movementIndex = 0;
	int movementLocationsIndex = -1;

	private BukkitTask task = null;
	long ticks = 0L;

	/**
	 * @param target is the target block BELOW the entity's feet
	 */
	public Navigator(World world, Point target, PointTolerance tolerance, MovementFinder movementFinder, Consumer<NavigatorResult> onDone) {
		this.world = world;
		this.target = target;
		this.tolerance = tolerance;
		this.movementFinder = movementFinder;
		this.onDone = onDone;
		this.runnable = new NavigatorTask(this);
	}

	// ----- get
	public abstract List<NPC> getNpcs();

	public World getWorld() {
		return world;
	}

	public Point getTarget() {
		return target;
	}

	public PointTolerance getTolerance() {
		return tolerance;
	}

	public boolean isActive() {
		return task != null;
	}

	// ----- control
	void restart() {
		if (task != null) {
			task.cancel();
			runnable.cancel();
			task = null;
		}
		start();
	}

	public void start() {
		if (task == null) {
			// reset data
			this.path = null;
			this.movements = null;
			this.finder = new Pathfinding(world, new Point(getNpcs().get(0).getLocation().getBlock().getRelative(BlockFace.DOWN)), target, tolerance, 2, movementFinder,
					(result, path) -> {
						if (result.equals(PathfindingResult.SUCCESS)) {
							this.path = path;
							this.movements = AnimationUtils.animate(path, finder);
						} else if (result.equals(PathfindingResult.FAILURE_NO_MORE_POINTS)) {
							end(NavigatorResult.FAILURE_NO_PATH_FOUND);
						} else if (result.equals(PathfindingResult.CANCEL)) {
							end(NavigatorResult.CANCEL);
						}
					});
			finder.start();
			// start
			ticks = 0L;
			task = runnable.runTaskTimerAsynchronously(GCore.inst(), 0L, 1L);
			NPCManager.inst().addNavigator(this);
		}
	}

	public void end(NavigatorResult result) {
		if (task != null) {
			task.cancel();
			task = null;  // so we won't be called again from pathfinding's onDone
			if (result.equals(NavigatorResult.CANCEL) || result.equals(NavigatorResult.FAILURE_TIMEOUT)) {
				finder.end(PathfindingResult.CANCEL);
			}
			onDone.accept(result);
			NPCManager.inst().removeNavigator(this);
		}
	}

}
