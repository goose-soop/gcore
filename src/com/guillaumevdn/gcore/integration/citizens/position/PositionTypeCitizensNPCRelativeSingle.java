package com.guillaumevdn.gcore.integration.citizens.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.citizens.element.ElementCitizensNPC;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionSingle;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeSingle extends PositionType {

	public PositionTypeCitizensNPCRelativeSingle(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementCitizensNPC(position, "npc", Need.required(), TextEditorGeneric.descriptionPositionTypeCitizensNPCRelative));
		position.addRelativeLocation("location", Need.optional(), TextEditorGeneric.descriptionPositionTypeRelativeSingleLocation);
		position.addPointTolerance("point_tolerance", Need.optional(PointTolerance.LENIENT), TextEditorGeneric.descriptionPositionTypePointTolerance);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		NPC npc = position.getElementAs("npc", ElementCitizensNPC.class).parseNoCatchOrThrowParsingNull(replacer);
		if (!npc.isSpawned()) return null;
		Location location = position.getElementAs("location", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(npc.getEntity().getLocation()));
		PointTolerance pointTolerance = position.getElementAs("point_tolerance", ElementPointTolerance.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSingle(location, pointTolerance);
	}

}
