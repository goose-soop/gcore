package com.guillaumevdn.gcore.lib.location.position.type;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.number.MinMaxDouble;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeBiomes extends PositionType {

	public PositionTypeBiomes(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addBiomeList("biomes", Need.required(), TextEditorGeneric.descriptionPositionTypeBiomes);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		List<Biome> biomes = position.directParseNoCatchOrThrowParsingNull("biomes", replacer);
		return new Position() {
			@Override
			public boolean match(Location loc) {
				if (loc == null) {
					return false;
				}
				return biomes.contains(loc.getBlock().getBiome());
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
			public int findSafeRandomMaxY() {
				return 0;
			}
			@Override
			public MinMaxDouble getRandomSolidAndFreeAboveYBounds() {
				return null;
			}
			@Override
			public Location findClosestTo(Location loc) {
				return null;
			}
			@Override
			public Location findGPSFor(Player player) {
				return null;
			}
			@Override
			public boolean canFill() {
				return false;
			}
		};
	}

}
