package com.guillaumevdn.gcore.lib.legacy_npc.navigation.navigator;

import java.util.List;

import org.bukkit.Location;

import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.Point;
import com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement.Movement;

/**
 * @author GuillaumeVDN
 */
public final class AnimatedMovement {

	private List<Movement> movementsToRecheck;
	private Point target;
	private boolean sprinting;
	private boolean onGround;
	private List<Location> ticks;

	public AnimatedMovement(List<Movement> movementsToRecheck, Point target, boolean sprinting, List<Location> ticks) {
		this.movementsToRecheck = movementsToRecheck;
		this.target = target;
		this.sprinting = sprinting;
		this.sprinting = sprinting;
		this.ticks = ticks;
	}

	// ----- get
	public List<Movement> getMovementsToRecheck() {
		return movementsToRecheck;
	}

	public Point getTarget() {
		return target;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public List<Location> getTicks() {
		return ticks;
	}

}
