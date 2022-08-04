package com.guillaumevdn.gcore.integration.mythicmobs.v4.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.mythicmobs.v4.element.ElementMythicMobsMobList;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionSphereOutside;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeClosestMythicMobRelativeSphereOutside extends PositionTypeClosestMythicMob {

	public PositionTypeClosestMythicMobRelativeSphereOutside(String id) {
		super(id);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementMythicMobsMobList(position, "mobs", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestMythicMobMobs));
		position.addRelativeLocation("center", Need.optional(), TextEditorGeneric.descriptionPositionTypeSphereCenter);
		position.addDouble("radius", Need.required(), 1, TextEditorGeneric.descriptionPositionTypeSphereRadius);
	}

	// ----- parse
	@Override
	public Position doParseMob(ElementPosition position, Location mobLocation, Replacer replacer) throws ParsingError {
		Location center = position.getElementAs("center", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(mobLocation));
		double radius = position.getElementAs("radius", ElementDouble.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSphereOutside(center, radius);
	}

}
