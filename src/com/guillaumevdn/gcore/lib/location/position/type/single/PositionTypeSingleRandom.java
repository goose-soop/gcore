package com.guillaumevdn.gcore.lib.location.position.type.single;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocationList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeSingleRandom extends PositionType {

	public PositionTypeSingleRandom(String id) {
		super(id, CommonMats.MINECART);
	}

	// ----- elements
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;  // don't cache since random every time
	}

	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addLocationList("locations", Need.required(), TextEditorGeneric.descriptionPositionTypeSingleRandomLocations);
		position.addPointTolerance("point_tolerance", Need.optional(PointTolerance.LENIENT), TextEditorGeneric.descriptionPositionTypePointTolerance);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		ElementLocationList locations = position.getElementAs("locations", ElementLocationList.class);
		if (locations.getRawValueSize() == 0) {
			throw new ParsingError(locations, "no location found");
		}
		PointTolerance pointTolerance = position.getElementAs("point_tolerance", ElementPointTolerance.class).parseNoCatchOrThrowParsingNull(replacer);
		Location location = CollectionUtils.random(locations.parseNoCatchOrThrowParsingNull(replacer));
		return new PositionSingle(location, pointTolerance);
	}

}
