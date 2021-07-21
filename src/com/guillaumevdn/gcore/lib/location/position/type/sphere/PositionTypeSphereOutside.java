package com.guillaumevdn.gcore.lib.location.position.type.sphere;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeSphereOutside extends PositionType {

	public PositionTypeSphereOutside(String id) {
		super(id, CommonMats.NETHER_STAR);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addLocation("center", Need.required(), TextEditorGeneric.descriptionPositionTypeSphereCenter);
		position.addDouble("radius", Need.required(), 1, TextEditorGeneric.descriptionPositionTypeSphereRadius);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location center = position.getElementAs("center", ElementLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		double radius = position.getElementAs("radius", ElementDouble.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSphereOutside(center, radius);
	}

}
