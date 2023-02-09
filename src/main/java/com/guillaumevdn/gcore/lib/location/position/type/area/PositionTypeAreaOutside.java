package com.guillaumevdn.gcore.lib.location.position.type.area;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeAreaOutside extends PositionType {

	public PositionTypeAreaOutside(String id) {
		super(id, CommonMats.IRON_DOOR);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addLocation("bound1", Need.required(), TextEditorGeneric.descriptionPositionTypeBound1);
		position.addLocation("bound2", Need.required(), TextEditorGeneric.descriptionPositionTypeBound2);
	}

	// ----- parse
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location a = position.getElementAs("bound1", ElementLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		Location b = position.getElementAs("bound2", ElementLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionAreaOutside(a, b);
	}

}
