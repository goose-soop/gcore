package com.guillaumevdn.gcore.integration.citizens.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionSingle;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeSingle extends PositionTypeCitizensNPCRelative {

	public PositionTypeCitizensNPCRelativeSingle(String id) {
		super(id);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		super.doFillTypeSpecificElements(position);
		position.addRelativeLocation("location", Need.optional(), TextEditorGeneric.descriptionPositionTypeRelativeSingleLocation);
		position.addPointTolerance("point_tolerance", Need.optional(PointTolerance.LENIENT), TextEditorGeneric.descriptionPositionTypePointTolerance);
	}

	// ----- parse
	@Override
	protected Position doParse(ElementPosition position, NPC npc, Replacer replacer) throws ParsingError {
		Location location = position.getElementAs("location", ElementRelativeLocation.class)
				.parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(npc.getEntity().getLocation()));
		PointTolerance pointTolerance = position.getElementAs("point_tolerance", ElementPointTolerance.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSingle(location, pointTolerance);
	}

}
