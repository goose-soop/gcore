package com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Pathfinding;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.MovementType;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Offset;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.OptimizedPathPoint;

/**
 * @author GuillaumeVDN
 */
public final class AnimationUtils {

	private static final double DEBUG_MULTIPLIER = 1d;

	// ----- - https://minecraft.gamepedia.com/Sprinting#Usage
	private static final double TICK_WALKING = 0.21585 * DEBUG_MULTIPLIER;
	private static final double TICK_SPRINTING = 0.2806 * DEBUG_MULTIPLIER;

	private static final double GAP0_UP_TICK = 0.4d * DEBUG_MULTIPLIER;
	private static final double GAP0_UP_FORWARD_MULTIPLIER = 1d;
	private static final double GAP0_UP_Y_OFFSET = 2.5d;
	private static final double GAP0_UP_Y_TOLERANCE = 0.25d;

	private static final double TICK_ACCELERATION_DOWN = 0.065d * DEBUG_MULTIPLIER;

	private static final double GAP1_FORWARD_Y_OFFSET_UP = 0.85d;
	private static final double GAP1_FORWARD_Y_OFFSET_DOWN = 1d;
	private static final double GAP1_FORWARD_UP = 0.4d * DEBUG_MULTIPLIER;
	private static final double GAP1_FORWARD_DOWN = 0.25d * DEBUG_MULTIPLIER;

	private static final double GAP1_UP_TICK = 0.4d * DEBUG_MULTIPLIER;
	private static final double GAP1_UP_FORWARD_MULTIPLIER = 0.75d;
	private static final double GAP1_UP_Y_OFFSET = 2d;
	private static final double GAP1_UP_Y_EXTRAUP = 0.25d;

	public static final List<AnimatedMovement> animate(List<OptimizedPathPoint> path, Pathfinding finder) {
		List<AnimatedMovement> movements = new ArrayList<>();
		for (int i = 1; i < path.size(); ++i) {
			OptimizedPathPoint point = path.get(i);
			Movement movement = point.getMovementToPoint();
			Offset offset = movement.getOffset();
			Point origin = movement.getOrigin().relative(0, 1, 0);  // normalize points
			Point target = movement.getTarget().relative(0, 1, 0);  // normalize points
			// find settings to use for this movement
			final boolean sprinting = false;  // FIXME : find speed
			final double speed = sprinting ? TICK_SPRINTING : TICK_WALKING;
			List<Movement> movementsToRecheck = CollectionUtils.asList(movement);
			List<Location> ticks = new ArrayList<>();
			boolean animated = false;  // FIXME remove this once everything is animated

			// ---- OFFSET ----------------------------------------------------------------------------------------
			if (movement.getType().equals(MovementType.OFFSET)) {
				// ---- GAP 0 -----------------------------------------------------------------------------------------
				if (offset.getGap() == 0) {
					// walk
					if (offset.getY() == 0) {
						// as long as we walk towards the same direction, animate in a straight line so there are no cuts
						boolean straight = offset.isStraight();
						for (int j = i + 1; j < path.size(); ++j) {
							Movement next = path.get(j).getMovementToPoint();
							if (next.getType().equals(MovementType.OFFSET) && next.getOffset().getGap() == 0 && next.getOffset().getY() == 0 && next.getOffset().isStraight() == straight) {
								movementsToRecheck.add(next);
								target = next.getTarget().relative(0, 1, 0);  // normalize points
								++i;
							} else break;
						}
						// add steps
						Location loc = origin.toLocation(finder.getWorld());
						Vector step = origin.vectorTo(target);
						double length = step.length();
						step.normalize().multiply(speed);
						for (double d = 0d; d <= length; d += speed) {
							ticks.add(loc = loc.clone().add(step));
						}
						// add target
						//ticks.add(target.toLocation(finder.getWorld()));
						// set look
						animated = true;
					}
					// jump up
					else if (offset.getY() == 1) {
						jumpUp(0, GAP0_UP_FORWARD_MULTIPLIER, GAP0_UP_Y_OFFSET, GAP0_UP_TICK, GAP0_UP_Y_TOLERANCE, origin, target, speed, finder, ticks);
						animated = true;
					}
					// jump down
					else if (offset.getY() < 0) {
						jumpDown(0, origin, target, speed, finder, ticks);
						animated = true;
					}
				}
				// ---- GAP 1 -----------------------------------------------------------------------------------------
				else if (offset.getGap() == 1) {
					// forward
					if (offset.getY() == 0) {
						Vector straight = origin.vectorTo(target).multiply(0.5d);
						// first part : up
						Vector step = straight.clone().setY(GAP1_FORWARD_Y_OFFSET_UP);
						Location loc = origin.toLocation(finder.getWorld());
						double length = step.length();
						step.normalize().multiply(GAP1_FORWARD_UP);
						for (double d = 0d; d <= length; d += GAP1_FORWARD_UP) {
							ticks.add(loc = loc.clone().add(step));
						}
						// second part : down
						step = straight.clone().setY(-GAP1_FORWARD_Y_OFFSET_DOWN);
						step.normalize().multiply(GAP1_FORWARD_DOWN);
						for (double d = 0d; d <= length - GAP1_FORWARD_DOWN; d += GAP1_FORWARD_DOWN) {
							ticks.add(loc = loc.clone().add(step));
						}
						// add target
						ticks.add(target.toLocation(finder.getWorld()));
						animated = true;
					}
					// jump up
					else if (offset.getY() == 1) {
						jumpUp(1, GAP1_UP_FORWARD_MULTIPLIER, GAP1_UP_Y_OFFSET, GAP1_UP_TICK, GAP1_UP_Y_EXTRAUP, origin, target, speed, finder, ticks);
						animated = true;
					}
					// jump down
					else if (offset.getY() < 0) {
						jumpDown(1, origin, target, speed, finder, ticks);
						animated = true;
					}
				}
				// ---- GAP 2 -----------------------------------------------------------------------------------------
				else if (offset.getGap() == 2) {
					// FIXME : TO DO LOL
				}
			}
			// FIXME : for "get in water", try to animate a plongeon si on peut directement aller en swimming animation (more than 1 block depth)
			if (!animated) {
				ticks.add(target.toLocation(finder.getWorld()));
			}
			// add movement
			movements.add(new AnimatedMovement(movementsToRecheck, target, sprinting, ticks));
		}
		return movements;
	}

	private static void jumpUp(int gap, double forwardMultiplier, double yOffset, double tick, double yExtraUp, Point origin, Point target, double speed, Pathfinding finder, List<Location> ticks) {
		// take the vector, add y offset, and make the player follow it until it reaches y + extraUp
		Vector step = origin.vectorTo(target).multiply(forwardMultiplier).setY(target.getY() - origin.getY() + yOffset).normalize().multiply(tick);
		if (step.getY() < 0) throw new IllegalStateException("gap 0, offset y 1, but step y is " + step.getY());  // let's avoid infinite loops, just in case
		Location loc = origin.toLocation(finder.getWorld());
		while (loc.getY() < target.getY() + yExtraUp) {
			ticks.add(loc = loc.clone().add(step));
		}
		// then move towards target
		step = new Vector((target.getX() + 0.5d) - loc.getX(), target.getY() - loc.getY(), (target.getZ() + 0.5d) - loc.getZ());
		double length = step.length() - speed;
		step.normalize().multiply(speed);
		for (double d = 0d; d <= length; d += speed) {
			ticks.add(loc = loc.clone().add(step));
		}
		// add a little pause when jumping 0
		ticks.add(target.toLocation(finder.getWorld()));
		if (gap == 0) {
			ticks.add(target.toLocation(finder.getWorld()));
			ticks.add(target.toLocation(finder.getWorld()));
		}
	}

	private static void jumpDown(int gap, Point origin, Point target, double speed, Pathfinding finder, List<Location> ticks) {
		final int y = origin.getY() - target.getY();
		// walk towards above first block center
		Location loc = origin.toLocation(finder.getWorld());
		Vector oneForward = origin.vectorTo(target).setY(0d).normalize();
		Vector step = oneForward.clone();
		double length = step.length();
		step.normalize().multiply(speed);
		for (double d = 0d; d <= length; d += speed) {
			ticks.add(loc = loc.clone().add(step));
		}
		// then go go down towards target ; calculate steps
		int steps = 0;
		for (double tickFall = TICK_ACCELERATION_DOWN, d = 0d; d <= y; d += tickFall) {
			tickFall += TICK_ACCELERATION_DOWN;
			++steps;
		}
		if (gap == 0) {
			step = new Vector();
		} else {
			step = origin.vectorTo(target).setY(0d).subtract(oneForward).normalize().multiply(1d / ((double) steps));
		}
		// and fall
		double tickFall = TICK_ACCELERATION_DOWN;
		loc = loc.clone().subtract(step);  // subtract one
		for (int s = 0; s <= steps - 1; ++s) {
			ticks.add(loc = loc.clone().add(step.clone().setY(-tickFall)));
			tickFall += TICK_ACCELERATION_DOWN;
		}
		ticks.add(target.toLocation(finder.getWorld()));
	}

	// ----- FIXME : make sure we're animating slabs correctly

}
