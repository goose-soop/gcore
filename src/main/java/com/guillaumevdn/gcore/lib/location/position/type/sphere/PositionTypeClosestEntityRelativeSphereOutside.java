package com.guillaumevdn.gcore.lib.location.position.type.sphere;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypeClosestEntityRelativeSingle;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeClosestEntityRelativeSphereOutside extends PositionType {

	public PositionTypeClosestEntityRelativeSphereOutside(String id) {
		super(id, CommonMats.ZOMBIE_HEAD);
	}

	// ----- elements
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;
	}

	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addEntityTypeList("entity_types", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityTypes);
		position.addStringList("entity_names", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityNames);
		position.addDyeColorList("entity_colors", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityColors);
		position.addRelativeLocation("center", Need.optional(), TextEditorGeneric.descriptionPositionTypeSphereCenter);
		position.addDouble("radius", Need.required(), 1, TextEditorGeneric.descriptionPositionTypeSphereRadius);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Entity entity = PositionTypeClosestEntityRelativeSingle.findMatching(position, replacer);
		if (entity == null) return null;
		Location center = position.getElementAs("center", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		double radius = position.getElementAs("radius", ElementDouble.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSphereOutside(center, radius);
	}

}
