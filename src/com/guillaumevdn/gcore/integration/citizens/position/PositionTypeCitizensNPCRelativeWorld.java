package com.guillaumevdn.gcore.integration.citizens.position;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.citizens.element.ElementCitizensNPC;
import com.guillaumevdn.gcore.lib.block.BlockState;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeWorld extends PositionType {

	public PositionTypeCitizensNPCRelativeWorld(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementCitizensNPC(position, "npc", Need.required(), TextEditorGeneric.descriptionPositionTypeCitizensNPCRelative));
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		NPC npc = position.getElementAs("npc", ElementCitizensNPC.class).parseNoCatchOrThrowParsingNull(replacer);
		if (!npc.isSpawned()) return null;
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
