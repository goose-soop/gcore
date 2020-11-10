package com.guillaumevdn.gcore.integration.mythicmobs.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionAreaInside;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeClosestMythicMobRelativeAreaInside extends PositionTypeClosestMythicMob {

	public PositionTypeClosestMythicMobRelativeAreaInside(String id) {
		super(id);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		super.doFillTypeSpecificElements(position);
		position.addRelativeLocation("bound1", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound1);
		position.addRelativeLocation("bound2", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound2);
	}

	// parse
	@Override
	public Position doParseMob(ElementPosition position, Location mobLocation, Replacer replacer) throws ParsingError {
		Location a = position.getElementAs("bound1", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(mobLocation));
		Location b = position.getElementAs("bound2", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(mobLocation));
		return new PositionAreaInside(a, b);
	}

}
