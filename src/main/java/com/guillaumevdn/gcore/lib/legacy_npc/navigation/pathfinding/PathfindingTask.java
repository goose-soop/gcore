package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.NPCConfig;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.ExploringPathPoint;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.Path;

/**
 * @author GuillaumeVDN
 */
final class PathfindingTask extends BukkitRunnable {

	private final Pathfinding finder;
	private final Location targetLocation;

	PathfindingTask(Pathfinding finder) {
		this.finder = finder;
		this.targetLocation = finder.getTarget().toLocation(finder.getWorld());
	}

	@Override
	public void run() {
		if (!finder.isActive()) {
			return;
		}
		final long started = System.currentTimeMillis();
		for (;;) {
			// no more points to explore, invalid
			Path path = finder.getPath();
			ExploringPathPoint exploring = path.getLast();
			if (exploring == null) {
				finder.end(PathfindingResult.FAILURE_NO_MORE_POINTS);
				return;
			}
			// explore next path
			Movement movement = finder.getMovementFinder().findNewMovement(exploring, finder);
			// ... found movement
			if (movement != null) {
				// add point to path
				path.add(new ExploringPathPoint(movement, movement.getTarget()));
				// mark the new point to be ignored ; we don't want a path to re-check it later (this avoids loops)
				finder.ignore(movement.getTarget());
				// FIXME : setting for pathfinding
				// Particle.fromId("VILLAGER_ANGRY").orNull().send(PlayerUtils.getOnline(),
				// movement.getTarget().toLocation(finder.getWorld())); // FIXME : remove
				// maybe we're done ?
				if (finder.getTolerance().match(movement.getTarget().toLocation(finder.getWorld()), targetLocation)) {
					finder.end(PathfindingResult.SUCCESS);
					return;
				}
			}
			// ... no movement found
			else {
				path.removeLast(); // remove last ; it will already has been marked as ignored, so it won't be re-checked
			}
			// stop for this task iteration
			if (System.currentTimeMillis() - started >= NPCConfig.pathfindingMaxMillisPerTick) {
				return;
			}
		}

	}

}
