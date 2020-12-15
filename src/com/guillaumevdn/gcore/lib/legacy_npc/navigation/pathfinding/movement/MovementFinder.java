package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Pathfinding;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.path.ExploringPathPoint;

/**
 * A movement finder with some properties ; this class caches finder with the same properties
 * @author GuillaumeVDN
 */
public final class MovementFinder {

	private static Map<Integer, MovementFinder> cache = new HashMap<>();

	public static MovementFinder ofProperties(int maxGap, int minBottomVertical, boolean canStickToWall, boolean canSwim) {
		return cache.computeIfAbsent(Objects.hash(maxGap, minBottomVertical, canStickToWall, canSwim), __ -> new MovementFinder(maxGap, minBottomVertical, canStickToWall, canSwim));
	}

	// ----------------------------------------------------------------------------------------------------
	//		BUILD RELATIVE
	// ----------------------------------------------------------------------------------------------------

	private List<Offset> offsets = new ArrayList<>();
	private final int maxGap;
	private final int minBottomVertical;
	private final boolean canStickToWall;
	private final boolean canSwim;

	private MovementFinder(int maxGap, int minBottomVertical, boolean canStickToWall, boolean canSwim) {
		if (!NumberUtils.isInRange(maxGap, 0, 2)) throw new IllegalArgumentException("invalid max gap " + maxGap + ", should have 0 <= maxGap <= 2");
		if (!NumberUtils.isInRange(minBottomVertical, -3, -1)) throw new IllegalArgumentException("invalid min. bottom vertical " + minBottomVertical + ", should have -3 <= minBottomVertical <= -1");
		List<Integer> vertical = CollectionUtils.asList(0, 1);
		for (int i = -1; i >= minBottomVertical; --i) vertical.add(i);
		// initialize gaps offsets (from 2 to 0 so the list is kind of pre-sorted for findFrom)
		if (maxGap == 2) {  // gap 2
			// FIXME do :CringeHarold:
		}
		if (maxGap == 1 || maxGap == 2) {  // gap 1
			for (int y : vertical) {
				withOffset(1, 0, y, -2, b(0, -1));  // north
				withOffset(1, 1, y, -2, b(0, -1), b(0, -2), b(1, -1));
				withOffset(1, 2, y, -2, b(0, -1), b(1, -1), b(1, 0), b(1, -2), b(2, -1));  // north-east
				withOffset(1, 2, y, -1, b(1, 0), b(2, 0), b(1, -1));
				withOffset(1, 2, y, 0, b(1, 0));  // east
				withOffset(1, 2, y, 1, b(1, 0), b(2, 0), b(1, 1));
				withOffset(1, 2, y, 2, b(1, 0), b(1, 1), b(0, 1), b(2, 1), b(1, 2));  // south-east corner
				withOffset(1, 1, y, 2, b(0, 1), b(0, 2), b(1, 1));
				withOffset(1, 0, y, 2, b(0, 1));  // south
				withOffset(1, -1, y, 2, b(0, 1), b(0, 2), b(-1, 1));
				withOffset(1, -2, y, 2, b(0, 1), b(-1, 0), b(-1, 1), b(-1, 2), b(-2, 1));  // south-west corner
				withOffset(1, -2, y, 1, b(-1, 0), b(-2, 0), b(-1, 1));
				withOffset(1, -2, y, 0, b(-1, 0));  // west
				withOffset(1, -2, y, -1, b(-1, 0), b(-2, 0), b(-1, -1));
				withOffset(1, -2, y, -2, b(-1, 0), b(-1, -1), b(0, -1), b(-2, -1), b(-1, -2));  // north-west corner
				withOffset(1, -1, y, -2, b(0, -1), b(0, -2), b(-1, -1));
			}
		}
		for (int y : vertical) {  // gap 0
			withOffset(0, 0, y, -1);  // north
			withOffset(0, 1, y, -1, y >= 0 || !canStickToWall ? b(0, -1) : b(0, -1, 1, 0), y >= 0 || !canStickToWall ? b(1, 0) : b(1, 0, 0, -1));
			withOffset(0, 1, y, 0);  // east
			withOffset(0, 1, y, 1, y >= 0 || !canStickToWall ? b(1, 0) : b(1, 0, 0, 1), y >= 0 || !canStickToWall ? b(0, 1) : b(0, 1, 1, 0));
			withOffset(0, 0, y, 1);  // south
			withOffset(0, -1, y, 1, y >= 0 || !canStickToWall ? b(0, 1) : b(0, 1, -1, 0), y >= 0 || !canStickToWall ? b(-1, 0) : b(-1, 0));
			withOffset(0, -1, y, 0);  // west
			withOffset(0, -1, y, -1, y >= 0 || !canStickToWall ? b(-1, 0, 0, -1) : b(-1, 0), y >= 0 || !canStickToWall ? b(0, -1) : b(0, -1, -1, 0));
		}
		// fields
		this.offsets = Collections.unmodifiableList(offsets);
		this.maxGap = maxGap;
		this.minBottomVertical = minBottomVertical;
		this.canStickToWall = canStickToWall;
		this.canSwim = canSwim;
	}

	private static OffsetBlocking b(int x, int z) { return new OffsetBlocking(x, z); }
	private static OffsetBlocking b(int x, int z, int backupX, int backupZ) { return new OffsetBlocking(x, z, backupX, backupZ); }

	// get
	public List<Offset> getOffsets() {
		return offsets;
	}

	public int getMaxGap() {
		return maxGap;
	}

	public int getMinBottomVertical() {
		return minBottomVertical;
	}

	public boolean canStickToWall() {
		return canStickToWall;
	}

	public boolean canSwim() {
		return canSwim;
	}

	// set
	private void withOffset(int gap, int x, int y, int z, OffsetBlocking... blocking) {  // FIXME : in offsets, add 'MovementType' for particular movements
		offsets.add(new Offset(gap, x, y, z, CollectionUtils.asUnmodifiableList(blocking)));
	}

	// ----------------------------------------------------------------------------------------------------
	// 		FIND MOVEMENT
	// ----------------------------------------------------------------------------------------------------

	/**
	 * Find a new movement, assuming the origin is a solid block and is free above
	 * @param origin the starting block BELOW the entity's feet
	 * @return a new movement from the origin to a relative point that's as close to the target as possible, or null if not found
	 */

	@Nullable
	public Movement findNewMovement(ExploringPathPoint pathPoint, Pathfinding finder) {

		Point origin = pathPoint.getPoint();
		Movement previous = pathPoint.getMovementToPoint();

		// filter offsets
		Stream<Offset> offsets = this.offsets.stream();
		if (previous != null) {
			// when coming from a ladder, we can only go forward to get out of the ladder
			if (previous.getType().equals(MovementType.LADDER)) {
				offsets = offsets.filter(offset -> offset.getY() == 0 && offset.getGap() == 0);  // FIXME ladder ; + up/down the ladder
			}
			// when coming from swimming, we can only go forward to get out of the water
			if (previous.getType().equals(MovementType.SWIMMING) || previous.getType().equals(MovementType.GET_IN_WATER)) {
				offsets = offsets.filter(offset -> offset.getY() == 0 && offset.getGap() == 0);
			}
		}

		// remove offsets we must ignore
		offsets = offsets.filter(offset -> !pathPoint.getOffsetsToIgnore().contains(offset));

		// sort offsets by distance to target
		offsets = offsets.sorted((a, b) -> Double.compare(a.distanceToTargetIfApplied(origin, finder.getTarget()), b.distanceToTargetIfApplied(origin, finder.getTarget())));  // TODO : see how long this takes ; and eventually try to improve with distance caching for instance

		// check offsets
		List<Offset> offs = offsets.collect(Collectors.toList());

		for (Offset offset : offs) {
			// mark offset to ignore whatsoever
			// - no matter the result, we don't want this offset to be re-checked again later (because invalid, or discontinued dead end, or whatever)
			pathPoint.ignoreOffset(offset);

			// target must be ignored
			Point target = origin.relative(offset);
			if (finder.mustIgnore(target)) {
				continue;
			}

			// find movement
			Movement movement = doFindMovement(origin, target, offset, finder, true);
			if (movement != null) {
				return movement;
			}
		}

		// didn't find anything
		return null;
	}

	/**
	 * Find a movement of this type from a point to another, assuming the origin is a solid block and is free above
	 * @param origin the starting block BELOW the entity's feet
	 * @return a new movement from the origin to a relative point that's as close to the target as possible, or null if not found
	 */

	@Nullable
	public Movement findMovement(Point origin, Point target, Pathfinding finder, boolean ignoreFinderBlocks) {
		int x = target.getX() - origin.getX();
		int y = target.getY() - origin.getY();
		int z = target.getZ() - origin.getZ();
		Offset offset = offsets.stream().filter(o -> o.getX() == x && o.getY() == y && o.getZ() == z).findFirst().orElse(null);
		// FIXME : ensure can swim ?
		// FIXME : ensure can climb ?
		return offset == null ? null : doFindMovement(origin, target, offset, finder, ignoreFinderBlocks);
	}

	private Movement doFindMovement(Point origin, Point target, Offset offset, Pathfinding finder, boolean ignoreFinderBlocks) {
		// we must ignore this target block
		if (ignoreFinderBlocks && finder.mustIgnore(target)) {
			return null;
		}

		// ensure there's no block above entity if we're jumping up gap 0, or non-zero gap up/forward
		if (offset.getY() == 1 || (offset.getGap() != 0 && offset.getY() >= 0)) {
			if (!finder.getBlockType(origin.relative(0, finder.getEntityHeight() + 1, 0)).getData().isTraversable()) {
				return null;
			}
		}

		// ensure the target block is solid and free above
		// - this also prevents to go in the ground if gap is 0 and vertical <= 0 but blocking
		int extraHeight = offset.getY() < 0 ? -offset.getY() : offset.getY();
		if (!finder.isSolidOrWaterAndFreeAbove(target, origin, extraHeight, canSwim)) {
			return null;
		}

		// ensure that the corridor is free
		// AND, if gap is not zero, that there's actually a hole below corridor (otherwise we'll want to use a closer offset)
		for (OffsetBlocking blocking : offset.getBlocking()) {
			Point check = origin.relative(blocking.getX(), offset.getY(), blocking.getZ());
			if (offset.getGap() == 0 && offset.getY() == 0 ? !finder.isFreeAbove(check, origin, extraHeight) : !finder.isTraversableOrWaterAndFreeAbove(check, origin, extraHeight)) {
				if (blocking.hasBackup() && !finder.getBlockType(target).isWater()) {  // only allow stick to wall towards non-water block
					if (finder.isTraversableOrWaterAndFreeAbove(origin.relative(blocking.getBackupX(), offset.getY(), blocking.getBackupX()), origin, extraHeight)) {  // allow stick to wall above water/traversable
						return new Movement(origin, target, MovementType.OFFSET_STICK_TO_WALL, new Offset(0, blocking.getBackupX(), 0, blocking.getBackupZ(), null));
					}
				}
				return null;
			}
		}

		// get movement type
		MovementType type;
		if (finder.getBlockType(origin).isWater()) {
			type = finder.getBlockType(target).isWater() ? MovementType.SWIMMING : MovementType.GET_OUT_OF_WATER;
		} else {
			type = finder.getBlockType(target).isWater() ? MovementType.GET_IN_WATER : MovementType.OFFSET;
		}

		// seems good
		return new Movement(origin, target, type, offset);
	}

	// FIXME : when navigating, when about to do a movement, make sure it's actually still possible ; otherwise, recalculate path

}
