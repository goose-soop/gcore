package com.guillaumevdn.gcore.lib.location.position.type.area;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypePlayerRelativeAreaInside extends PositionType {

	public PositionTypePlayerRelativeAreaInside(String id) {
		super(id, CommonMats.PLAYER_HEAD);
	}

	// ----- elements
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;
	}

	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addRelativeLocation("bound1", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound1);
		position.addRelativeLocation("bound2", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound2);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location a = position.getElementAs("bound1", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		Location b = position.getElementAs("bound2", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionAreaInside(a, b);
	}

}
