package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ParseableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementRelativeLocation extends ParseableContainerElement<Location> {

	private ElementDouble horizontalAngle = addDouble("horizontal_angle", Need.optional(0d), 0d, 360d, TextEditorGeneric.descriptionRelativeLocationHorizontalAngle);
	private ElementDouble distance = addDouble("distance", Need.optional(0d), TextEditorGeneric.descriptionRelativeLocationDistance);
	private ElementDouble verticalOffset = addDouble("vertical_offset", Need.optional(0d), TextEditorGeneric.descriptionRelativeLocationVerticalOffset);

	public ElementRelativeLocation(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	@Override
	public List<String> editorCurrentValue() {
		List<String> desc = new ArrayList<>();
		double horizontalAngle = this.horizontalAngle.parseGeneric().orElse(0d);
		if (horizontalAngle != 0d) {
			desc.add("rotation : " + horizontalAngle + "°");
		}
		double verticalOffset = this.verticalOffset.parseGeneric().orElse(0d);
		if (verticalOffset != 0d) {
			desc.add("vertical offset : " + verticalOffset + "m");
		}
		double distance = this.distance.parseGeneric().orElse(0d);
		if (distance != 0d) {
			desc.add("distance from base : " + distance + "m");
		}
		return desc.isEmpty() ? null : desc;
	}

	// ----- get
	public ElementDouble getHorizontalAngle() {
		return horizontalAngle;
	}

	public ElementDouble getDistance() {
		return distance;
	}

	public ElementDouble getVerticalOffset() {
		return verticalOffset;
	}

	// ----- parse
	@Override
	public ParsedCache<Location> getCache() {
		return null;  // don't valuesCache obviously since this relies on volatile data
	}

	@Override
	public Location doParse(Replacer replacer) throws ParsingError {
		Location relativeTo = replacer.getReplacerData().getLocationOrPlayer();
		if (relativeTo == null) {
			throw new ParsingError(this, "no location found for replacer");
		}
		if (!readContains()) {
			return relativeTo.clone();
		}
		// parse
		double horizontalAngle = getHorizontalAngle().parseNoCatchOrThrowParsingNull(replacer);
		double verticalOffset = getVerticalOffset().parseNoCatchOrThrowParsingNull(replacer);
		double distance = getDistance().parseNoCatchOrThrowParsingNull(replacer);
		// adapt and return
		double sign = Math.signum(distance);
		double dist = Math.abs(distance);
		double horizontalRad = Math.toRadians(-horizontalAngle - relativeTo.getYaw());
		double x = relativeTo.getX() + sign * (dist * Math.sin(horizontalRad));
		double y = relativeTo.getY() + verticalOffset;
		double z = relativeTo.getZ() + sign * (dist * Math.cos(horizontalRad));
		return new Location(relativeTo.getWorld(), x, y, z);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

}
