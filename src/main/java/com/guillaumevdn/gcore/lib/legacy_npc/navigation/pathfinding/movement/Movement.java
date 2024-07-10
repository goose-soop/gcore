package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class Movement {

	private Point origin;
	private Point target;
	private MovementType type;
	private Offset offset;
	private Offset extraOffset;

	public Movement(Point origin, Point target, MovementType type, Offset offset) {
		this(origin, target, type, offset, null);
	}

	public Movement(Point origin, Point target, MovementType type, Offset offset, Offset extraOffset) {
		this.origin = origin;
		this.target = target;
		this.type = type;
		this.offset = offset;
		this.extraOffset = extraOffset;
	}

	// ----- get
	public Point getOrigin() {
		return origin;
	}

	public Point getTarget() {
		return target;
	}

	public MovementType getType() {
		return type;
	}

	@Nonnull
	public Offset getOffset() {
		return offset;
	}

	@Nullable
	public Offset getExtraOffset() {
		return extraOffset;
	}

	// ----- obj
	@Override
	public String toString() {
		return type + ", " + offset + (extraOffset == null ? "" : ", extra " + extraOffset);
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin, target, type, offset, extraOffset);
	}

	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(obj, Movement.class, other -> hashCode() == other.hashCode());
	}

}
