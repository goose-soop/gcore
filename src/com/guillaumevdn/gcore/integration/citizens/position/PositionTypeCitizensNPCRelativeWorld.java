package com.guillaumevdn.gcore.integration.citizens.position;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeWorld extends PositionTypeCitizensNPCRelative {

	public PositionTypeCitizensNPCRelativeWorld(String id) {
		super(id);
	}

	// parse
	@Override
	protected Position doParse(ElementPosition position, NPC npc, Replacer replacer) {
		World world = npc.getEntity().getWorld();
		return new Position() {
			@Override
			public boolean match(Location loc) {
				if (loc == null) {
					return false;
				}
				return loc.getWorld().equals(world);
			}
			@Override
			public World getWorld() {
				return world;
			}
			@Override
			public boolean canFindRandom() {
				return false;
			}
			@Override
			public Location findRandom() {
				return null;
			}
			@Override
			public MinMaxDouble getRandomSolidAndFreeAboveYBounds() {
				return null;
			}
			@Override
			public Location findClosestTo(Location loc) {
				if (!loc.getWorld().equals(world)) {
					return null;
				}
				return loc;
			}
			@Override
			public Location findGPSFor(Player player) {
				return null;
			}
			@Override
			public boolean canFill() {
				return false;
			}
			@Override
			public void fill(Mat blockType, List<BlockState> blockStates) {
			}
		};
	}

}
