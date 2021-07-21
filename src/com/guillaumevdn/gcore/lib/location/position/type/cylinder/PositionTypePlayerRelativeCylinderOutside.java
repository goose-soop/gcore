package com.guillaumevdn.gcore.lib.location.position.type.cylinder;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypePlayerRelativeCylinderOutside extends PositionType {

	public PositionTypePlayerRelativeCylinderOutside(String id) {
		super(id, CommonMats.PLAYER_HEAD);
	}

	// ----- elements
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;
	}

	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addRelativeLocation("center", Need.optional(), TextEditorGeneric.descriptionPositionTypeCylinderCenter);
		position.addDouble("radius", Need.required(), 1, TextEditorGeneric.descriptionPositionTypeCylinderRadius);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location center = position.getElementAs("center", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		double radius = position.getElementAs("radius", ElementDouble.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionCylinderOutside(center, radius);
	}

}
