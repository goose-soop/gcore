package com.guillaumevdn.gcore.lib.location.position.type;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorldList;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeWorlds extends PositionType {

	public PositionTypeWorlds(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addWorldList("worlds", Need.required(), TextEditorGeneric.descriptionPositionTypeWorld);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		List<World> worlds = position.getElementAs("worlds", ElementWorldList.class).parseNoCatchOrThrowParsingNull(replacer);
		return new Position() {
			@Override
			public boolean match(Location loc) {
				if (loc == null) {
					return false;
				}
				return worlds.contains(loc.getWorld());
			}
			@Override
			public World getWorld() {
				return CollectionUtils.random(worlds);
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
			public int findSafeRandomMaxY() {
				return 0;
			}
			@Override
			public MinMaxDouble getRandomSolidAndFreeAboveYBounds() {
				return null;
			}
			@Override
			public Location findClosestTo(Location loc) {
				if (!worlds.contains(loc.getWorld())) {
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
