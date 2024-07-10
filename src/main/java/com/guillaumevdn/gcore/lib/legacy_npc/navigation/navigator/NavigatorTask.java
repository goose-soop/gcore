package com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator;

import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.NPCConfig;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.OptimizedPathPoint;

/**
 * @author GuillaumeVDN
 */
final class NavigatorTask extends BukkitRunnable {

	private final Navigator navigator;

	NavigatorTask(Navigator navigator) {
		this.navigator = navigator;
	}

	private WeakHashMap<List<OptimizedPathPoint>, Long> lastMovementCheck = new WeakHashMap<>();

	@Override
	public void run() {
		if (!navigator.isActive()) {
			return;
		}
		// still finding a path
		if (navigator.path == null) {
			// timeout
			if (++navigator.ticks >= NPCConfig.navigationTimeoutTicks) {
				navigator.end(NavigatorResult.FAILURE_TIMEOUT);
			}
			return;
		}
		// navigating
		if (navigator.movements != null) {
			AnimatedMovement movement = navigator.movements.get(navigator.movementIndex);
			// FIXME : gravity ? the movement finder does not check if the NPC is on a solid block ; so try that before ? or maybe
			// the gravity behavior will pause the navigator ? :o then do that !
			// make sure movements are still possible
			boolean multipleRechecks = movement.getMovementsToRecheck().size() == 1;
			boolean recheckNow = navigator.movementLocationsIndex == -1 || (multipleRechecks && System.currentTimeMillis()
					- lastMovementCheck.computeIfAbsent(navigator.path, __ -> System.currentTimeMillis()) >= NPCConfig.navigationBatchMovementsRecheckMillis);
			if (recheckNow) {
				if (multipleRechecks) {
					lastMovementCheck.put(navigator.path, System.currentTimeMillis());
				}
				for (Movement originalMovement : movement.getMovementsToRecheck()) {
					Movement newMovement = navigator.movementFinder.findMovement(originalMovement.getOrigin(), originalMovement.getTarget(), navigator.finder,
							false);
					// not possible anymore, or different movement ; re-animate, restart pathfinding from new location
					if (!originalMovement.equals(newMovement)) {
						navigator.restart();
						return;
					}
				}
			}
			// move to next animation tick
			navigator.getNpcs().forEach(npc -> {
				npc.move(movement.getTicks().get(++navigator.movementLocationsIndex), movement.isOnGround());
			});
			// mark next point, or eventually end
			if (navigator.movementLocationsIndex + 1 >= movement.getTicks().size()) {
				navigator.movementLocationsIndex = -1;
				if (++navigator.movementIndex >= navigator.movements.size()) {
					navigator.end(NavigatorResult.SUCCESS);
					return;
				}
			}
		}
	}

}
