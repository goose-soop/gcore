package com.guillaumevdn.gcore.integration.citizens.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionCylinderInside;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeCylinderInside extends PositionTypeCitizensNPCRelative {

	public PositionTypeCitizensNPCRelativeCylinderInside(String id) {
		super(id);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		super.doFillTypeSpecificElements(position);
		position.addRelativeLocation("center", Need.optional(), TextEditorGeneric.descriptionPositionTypeCylinderCenter);
		position.addDouble("radius", Need.required(), 1, TextEditorGeneric.descriptionPositionTypeCylinderRadius);
	}

	// ----- parse
	@Override
	protected Position doParse(ElementPosition position, NPC npc, Replacer replacer) throws ParsingError {
		Location center = position.getElementAs("center", ElementRelativeLocation.class)
				.parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(npc.getEntity().getLocation()));
		double radius = position.getElementAs("radius", ElementDouble.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionCylinderInside(center, radius);
	}

}
