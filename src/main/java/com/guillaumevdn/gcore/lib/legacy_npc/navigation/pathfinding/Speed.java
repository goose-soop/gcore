package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding;

import java.util.Set;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
public enum Speed {

	WALK_ONLY(SpeedType.WALK), WALK_BUT_CAN_SPRINT(SpeedType.WALK, SpeedType.SPRINT), SPRINT_ONLY(SpeedType.SPRINT);

	private Set<SpeedType> speeds;

	private Speed(SpeedType... speeds) {
		this.speeds = CollectionUtils.asSet(speeds);
	}

	public boolean can(SpeedType speed) {
		return speeds.contains(speed);
	}

	public boolean isAtLeast(Speed other) {
		return ordinal() >= other.ordinal();
	}

}
