package com.guillaumevdn.gcore.lib.location.position.type.single;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Colorable;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementPointTolerance;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.PointTolerance;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeClosestEntityRelativeSingle extends PositionType {

	public PositionTypeClosestEntityRelativeSingle(String id) {
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
		position.addRelativeLocation("location", Need.optional(), TextEditorGeneric.descriptionPositionTypeRelativeSingleLocation);
		position.addPointTolerance("point_tolerance", Need.optional(PointTolerance.LENIENT), TextEditorGeneric.descriptionPositionTypePointTolerance);
	}

	public static Entity findMatching(ContainerElement element, Replacer replacer) {
		return findMatchingStream(element, replacer).findFirst().orElse(null);
	}

	public static Stream<Entity> findMatchingStream(ContainerElement element, Replacer replacer) {
		Location parsingLocation = replacer.getReplacerData().getLocationOrPlayer();
		if (parsingLocation == null) {
			return null;
		}
		// find entities by types (or all entities if no type)
		List<Class<? extends Entity>> typesClassesList = element.parseElementAsList("entity_types", EntityType.class, replacer).orEmptyList().stream()
				.map(EntityType::getEntityClass).collect(Collectors.toList());
		Class[] typesClasses = typesClassesList.toArray(new Class[typesClassesList.size()]);
		Stream<Entity> stream = (typesClasses.length == 0 ? parsingLocation.getWorld().getEntities()
				: parsingLocation.getWorld().getEntitiesByClasses(typesClasses)).stream();
		// filter by name
		List<String> names = element.parseElementAsList("entity_names", String.class, replacer).orNull();
		if (names != null) {
			stream = stream.filter(entity -> entity.getCustomName() != null && names.stream().anyMatch(name -> name.equalsIgnoreCase(entity.getCustomName())));
		}
		// filter by color
		List<DyeColor> colors = element.parseElementAsList("entity_colors", DyeColor.class, replacer).orNull();
		if (colors != null) {
			stream = stream.filter(entity -> {
				Colorable colorable = ObjectUtils.castOrNull(entity, Colorable.class);
				return colorable != null && colors.contains(colorable.getColor());
			});
		}
		// sort and find first
		return stream.sorted((a, b) -> Double.compare(a.getLocation().distance(parsingLocation), b.getLocation().distance(parsingLocation)));
	}

	public static boolean matches(Entity entity, ContainerElement element, Replacer replacer) {
		// filter by type
		List<EntityType> types = element.parseElementAsList("entity_types", EntityType.class, replacer).orEmptyList();
		if (!types.isEmpty() && !types.contains(entity.getType())) {
			return false;
		}
		// filter by name
		List<String> names = element.parseElementAsList("entity_names", String.class, replacer).orEmptyList();
		if (!names.isEmpty() && entity.getCustomName() == null || !names.stream().anyMatch(name -> name.equalsIgnoreCase(entity.getCustomName()))) {
			return false;
		}
		// filter by color
		List<DyeColor> colors = element.parseElementAsList("entity_colors", DyeColor.class, replacer).orEmptyList();
		if (!colors.isEmpty()) {
			Colorable colorable = ObjectUtils.castOrNull(entity, Colorable.class);
			if (colorable == null || !colors.contains(colorable.getColor())) {
				return false;
			}
		}
		// we good
		return true;
	}

	// ----- parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Entity entity = findMatching(position, replacer);
		if (entity == null)
			return null;
		Location location = position.getElementAs("location", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer);
		PointTolerance pointTolerance = position.getElementAs("point_tolerance", ElementPointTolerance.class).parseNoCatchOrThrowParsingNull(replacer);
		return new PositionSingle(location, pointTolerance);
	}

}
