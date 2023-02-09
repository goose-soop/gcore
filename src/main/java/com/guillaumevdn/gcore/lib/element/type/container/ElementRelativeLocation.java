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
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFloat;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementRelativeLocation extends ParseableContainerElement<Location> {

	private ElementDouble horizontalAngle = addDouble("horizontal_angle", Need.optional(0d), 0d, 360d, TextEditorGeneric.descriptionRelativeLocationHorizontalAngle);
	private ElementDouble distance = addDouble("distance", Need.optional(0d), TextEditorGeneric.descriptionRelativeLocationDistance);
	private ElementDouble verticalOffset = addDouble("vertical_offset", Need.optional(0d), TextEditorGeneric.descriptionRelativeLocationVerticalOffset);
	private ElementBoolean baseRotationAware = addBoolean("base_rotation_aware", Need.optional(true), TextEditorGeneric.descriptionRelativeLocationBaseRotationAware);
	private ElementFloat addYaw = addFloat("add_yaw", Need.optional(0f), 0f, 360f, TextEditorGeneric.descriptionRelativeLocationAddYaw);
	private ElementFloat addPitch = addFloat("add_pitch", Need.optional(0f), -90f, 90f, TextEditorGeneric.descriptionRelativeLocationAddPitch);

	public ElementRelativeLocation(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	public ElementDouble getHorizontalAngle() {
		return horizontalAngle;
	}

	public ElementDouble getDistance() {
		return distance;
	}

	public ElementDouble getVerticalOffset() {
		return verticalOffset;
	}

	public ElementBoolean getBaseRotationAware() {
		return baseRotationAware;
	}

	public ElementFloat getAddYaw() {
		return addYaw;
	}

	public ElementFloat getAddPitch() {
		return addPitch;
	}

	@Override
	public ParsedCache<Location> getCache() {
		return null;  // don't valuesCache obviously since this relies on volatile data
	}

	@Override
	public Location doParse(Replacer replacer) throws ParsingError {
		final Location relativeTo = replacer.getReplacerData().getLocationOrPlayer();
		if (relativeTo == null) {
			throw new ParsingError(this, "no location found for replacer");
		}
		if (!readContains()) {
			return relativeTo.clone();
		}

		// parse
		final double horizontalAngle = getHorizontalAngle().parseNoCatchOrThrowParsingNull(replacer);
		final double verticalOffset = getVerticalOffset().parseNoCatchOrThrowParsingNull(replacer);
		final double distance = getDistance().parseNoCatchOrThrowParsingNull(replacer);
		final boolean baseRotationAware = getBaseRotationAware().parseNoCatchOrThrowParsingNull(replacer);

		// adapt and return
		final double sign = Math.signum(distance);
		final double dist = Math.abs(distance);
		final double horizontalRad = Math.toRadians(-horizontalAngle - (baseRotationAware ? relativeTo.getYaw() : 0d));
		final double x = relativeTo.getX() + sign * (dist * Math.sin(horizontalRad));
		final double y = relativeTo.getY() + verticalOffset;
		final double z = relativeTo.getZ() + sign * (dist * Math.cos(horizontalRad));

		final float yaw = relativeTo.getYaw() + getAddYaw().parse(replacer).orElse(0f);
		final float pitch = relativeTo.getPitch() + getAddPitch().parse(replacer).orElse(0f);

		return new Location(relativeTo.getWorld(), x, y, z, yaw, pitch);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.REPEATER;
	}

	@Override
	public List<String> editorCurrentValue() {
		final List<String> desc = new ArrayList<>();
		final double horizontalAngle = this.horizontalAngle.parseGeneric().orElse(0d);
		if (horizontalAngle != 0d) {
			desc.add("rotation : " + horizontalAngle + "°");
		}
		final double verticalOffset = this.verticalOffset.parseGeneric().orElse(0d);
		if (verticalOffset != 0d) {
			desc.add("vertical offset : " + verticalOffset + "m");
		}
		final double distance = this.distance.parseGeneric().orElse(0d);
		if (distance != 0d) {
			desc.add("distance from base : " + distance + "m");
		}
		final boolean baseRotationAware = this.baseRotationAware.parseGeneric().orElse(false);
		if (baseRotationAware) {
			desc.add("rotation aware : yes");
		}
		return desc.isEmpty() ? null : desc;
	}

}
