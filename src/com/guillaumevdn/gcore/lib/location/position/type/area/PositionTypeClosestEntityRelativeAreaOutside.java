package com.guillaumevdn.gcore.lib.location.position.type.area;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypeClosestEntityRelativeSingle;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeClosestEntityRelativeAreaOutside extends PositionType {

	public PositionTypeClosestEntityRelativeAreaOutside(String id) {
		super(id, CommonMats.ZOMBIE_HEAD);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.addEntityTypeList("entity_types", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityTypes);
		position.addStringList("entity_names", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityNames);
		position.addDyeColorList("entity_colors", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestEntityColors);
		position.addRelativeLocation("bound1", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound1);
		position.addRelativeLocation("bound2", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound2);
	}

	// parse
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;
	}

	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Entity entity = PositionTypeClosestEntityRelativeSingle.findMatching(position, replacer);
		if (entity == null) return null;
		Location a = position.getElementAs("bound1", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		Location b = position.getElementAs("bound2", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionAreaOutside(a, b);
	}

}
