package com.guillaumevdn.gcore.lib.location.position.type.single;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeSingle extends PositionType {

	public PositionTypeSingle(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addLocation("location", Need.required(), TextEditorGeneric.descriptionPositionTypeSingleLocation);
		position.addPointTolerance("point_tolerance", Need.optional(PointTolerance.LENIENT), TextEditorGeneric.descriptionPositionTypePointTolerance);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location location = position.getElementAs("location", ElementLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		PointTolerance pointTolerance = position.getElementAs("point_tolerance", ElementPointTolerance.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSingle(location, pointTolerance);
	}

}
