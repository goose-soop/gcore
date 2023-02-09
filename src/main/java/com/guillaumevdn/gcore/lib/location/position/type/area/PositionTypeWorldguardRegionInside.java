package com.guillaumevdn.gcore.lib.location.position.type.area;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.worldguard.WGRegion;
import com.guillaumevdn.gcore.lib.compatibility.worldguard.WorldGuardCompat;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorld;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementWorldguardRegion;
import com.guillaumevdn.gcore.lib.location.Point;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeWorldguardRegionInside extends PositionType {

	public PositionTypeWorldguardRegionInside(String id) {
		super(id, CommonMats.IRON_DOOR);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		ElementWorld world = position.addWorld("world", Need.required(), TextEditorGeneric.descriptionPositionTypeWorldguardRegionWorld);
		position.addWorldguardRegion(world, "region", Need.required(), TextEditorGeneric.descriptionPositionTypeWorldguardRegionRegion);
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		ElementWorldguardRegion regionElement = position.getElementAs("region");
		WGRegion region = regionElement.parseNoCatchOrThrowParsingNull(replacer);
		Pair<Point, Point> bounds = WorldGuardCompat.getRegionBounds(region.getWorld(), region.getRegionId());
		if (bounds == null || bounds.getA() == null || bounds.getB() == null) {
			throw new ParsingError(regionElement, "no region found " + regionElement.getWorld().getRawValueLine(0) + "/" + regionElement.getRawValueLine(0));
		}
		return new PositionAreaInside(bounds.getA().toLocation(), bounds.getB().toLocation());
	}

}
