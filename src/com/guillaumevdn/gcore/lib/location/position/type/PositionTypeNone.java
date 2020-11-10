package com.guillaumevdn.gcore.lib.location.position.type;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeNone extends PositionType {

	public PositionTypeNone(String id) {
		super(id, CommonMats.BEDROCK);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		return new Position() {
			@Override
			public boolean match(Location loc) {
				return true;
			}
			@Override
			public World getWorld() {
				return null;
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
			public Location findClosestTo(Location loc) {
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
