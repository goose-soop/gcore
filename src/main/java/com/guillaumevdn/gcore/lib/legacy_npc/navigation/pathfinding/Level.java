package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding;

import java.util.List;
import java.util.WeakHashMap;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.MovementType;
import com.guillaumevdn.gcore.lib.object.Optional;

/**
 * @author GuillaumeVDN
 */
public class Level {

	// ----- FIXME : default those in config

	// ----- FIXME : implement this (this will be in the animation/navigation part)

	/**
	 * Default values :
	 * 
	 * FORWARD : walk / sprint
	 * 
	 * JUMPS : - 0 block walk / sprint with pause if >=90° - 1 block walk / sprint with pause if >=45° - 2 blocks sprint
	 * with pause if >=0°
	 */
	public static Level NOOB,

			/**
			 * Default values :
			 * 
			 * FORWARD : walk / sprint
			 * 
			 * JUMPS : - 0 block walk / sprint with pause if >=90° - 1 block sprint / walk with pause if >90° - 2 blocks sprint with
			 * pause if >90°
			 */
			REGULAR,

			/**
			 * Default values :
			 * 
			 * FORWARD : sprint / walk
			 * 
			 * JUMPS : - 0 block sprint / walk with pause if >=90° - 1 block sprint / walk with pause if >115° - 2 blocks sprint
			 * with pause if >115°
			 */
			MLG;

	private WeakHashMap<MovementType, List<SpeedType>> preferredSpeeds;
	private WeakHashMap<MovementType, Double> pauseIfAngle; // 0 >= angle < 180 FIXME only allow this

	private Level(WeakHashMap<MovementType, List<SpeedType>> preferredSpeeds, WeakHashMap<MovementType, Double> pauseIfAngle) {
		this.preferredSpeeds = preferredSpeeds;
		this.pauseIfAngle = pauseIfAngle;
	}

	// ----- get
	public Optional<List<SpeedType>> getPreferredSpeeds(MovementType type) {
		return Optional.of(preferredSpeeds.get(type));
	}

	public Optional<Double> getPauseIfAngle(MovementType type) {
		return Optional.of(pauseIfAngle.get(type));
	}

}
