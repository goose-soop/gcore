package com.guillaumevdn.gcore.lib.location.position;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;

/**
 * @author GuillaumeVDN
 */
public interface Position {

	// get
	World getWorld();

	// match
	default boolean match(Player player) {
		return match(player.getLocation());
	}
	boolean match(Location loc);

	// random
	boolean canFindRandom();
	Location findRandom();

	// closest
	default Location findClosestTo(Player player) {
		return findClosestTo(player.getLocation());
	}
	Location findClosestTo(Location loc);

	// gps
	Location findGPSFor(Player player);

	// fill
	boolean canFill();
	void fill(Mat blockType, List<BlockState> blockStates);

}
