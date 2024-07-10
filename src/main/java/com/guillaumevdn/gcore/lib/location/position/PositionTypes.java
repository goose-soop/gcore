package com.guillaumevdn.gcore.lib.location.position;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementTypes;
import com.guillaumevdn.gcore.lib.location.position.type.PositionTypeBiomes;
import com.guillaumevdn.gcore.lib.location.position.type.PositionTypeNone;
import com.guillaumevdn.gcore.lib.location.position.type.PositionTypeWorlds;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeAreaInside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeAreaOutside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeClosestEntityRelativeAreaInside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeClosestEntityRelativeAreaOutside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypePlayerRelativeAreaInside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypePlayerRelativeAreaOutside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeWorldguardRegionInside;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionTypeWorldguardRegionOutside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypeClosestEntityRelativeCylinderInside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypeClosestEntityRelativeCylinderOutside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypeCylinderInside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypeCylinderOutside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypePlayerRelativeCylinderInside;
import com.guillaumevdn.gcore.lib.location.position.type.cylinder.PositionTypePlayerRelativeCylinderOutside;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypeClosestEntityRelativeSingle;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypePlayerRelativeSingle;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypeSingle;
import com.guillaumevdn.gcore.lib.location.position.type.single.PositionTypeSingleRandom;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypeClosestEntityRelativeSphereInside;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypeClosestEntityRelativeSphereOutside;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypePlayerRelativeSphereInside;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypePlayerRelativeSphereOutside;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypeSphereInside;
import com.guillaumevdn.gcore.lib.location.position.type.sphere.PositionTypeSphereOutside;

/**
 * @author GuillaumeVDN
 */
public final class PositionTypes extends TypableElementTypes<PositionType> {

	public PositionTypes() {
		super(PositionType.class);
	}

	// ----- types
	public final PositionTypeNone NONE = register(new PositionTypeNone("NONE"));

	public final PositionTypeWorlds WORLDS = register(new PositionTypeWorlds("WORLDS"));
	public final PositionTypeBiomes BIOMES = register(new PositionTypeBiomes("BIOMES"));
	public final PositionTypeSingleRandom SINGLE_RANDOM = register(new PositionTypeSingleRandom("SINGLE_RANDOM"));

	public final PositionTypeSingle SINGLE = register(new PositionTypeSingle("SINGLE"));
	public final PositionTypePlayerRelativeSingle PLAYER_RELATIVE_SINGLE = register(new PositionTypePlayerRelativeSingle("PLAYER_RELATIVE_SINGLE"));
	public final PositionTypeClosestEntityRelativeSingle CLOSEST_ENTITY_RELATIVE_SINGLE = register(
			new PositionTypeClosestEntityRelativeSingle("CLOSEST_ENTITY_RELATIVE_SINGLE"));

	public final PositionTypeAreaInside AREA_INSIDE = register(new PositionTypeAreaInside("AREA_INSIDE"));
	public final PositionTypeAreaOutside AREA_OUTSIDE = register(new PositionTypeAreaOutside("AREA_OUTSIDE"));
	public final PositionTypePlayerRelativeAreaInside PLAYER_RELATIVE_AREA_INSIDE = register(
			new PositionTypePlayerRelativeAreaInside("PLAYER_RELATIVE_AREA_INSIDE"));
	public final PositionTypePlayerRelativeAreaOutside PLAYER_RELATIVE_AREA_OUTSIDE = register(
			new PositionTypePlayerRelativeAreaOutside("PLAYER_RELATIVE_AREA_OUTSIDE"));
	public final PositionTypeClosestEntityRelativeAreaInside CLOSEST_ENTITY_RELATIVE_AREA_INSIDE = register(
			new PositionTypeClosestEntityRelativeAreaInside("CLOSEST_ENTITY_RELATIVE_AREA_INSIDE"));
	public final PositionTypeClosestEntityRelativeAreaOutside CLOSEST_ENTITY_RELATIVE_AREA_OUTSIDE = register(
			new PositionTypeClosestEntityRelativeAreaOutside("CLOSEST_ENTITY_RELATIVE_AREA_OUTSIDE"));
	public final PositionTypeWorldguardRegionInside WORLDGUARD_REGION_INSIDE = register(new PositionTypeWorldguardRegionInside("WORLDGUARD_REGION_INSIDE"));
	public final PositionTypeWorldguardRegionOutside WORLDGUARD_REGION_OUTSIDE = register(new PositionTypeWorldguardRegionOutside("WORLDGUARD_REGION_OUTSIDE"));

	public final PositionTypeSphereInside SPHERE_INSIDE = register(new PositionTypeSphereInside("SPHERE_INSIDE"));
	public final PositionTypeSphereOutside SPHERE_OUTSIDE = register(new PositionTypeSphereOutside("SPHERE_OUTSIDE"));
	public final PositionTypePlayerRelativeSphereInside PLAYER_RELATIVE_SPHERE_INSIDE = register(
			new PositionTypePlayerRelativeSphereInside("PLAYER_RELATIVE_SPHERE_INSIDE"));
	public final PositionTypePlayerRelativeSphereOutside PLAYER_RELATIVE_SPHERE_OUTSIDE = register(
			new PositionTypePlayerRelativeSphereOutside("PLAYER_RELATIVE_SPHERE_OUTSIDE"));
	public final PositionTypeClosestEntityRelativeSphereInside CLOSEST_ENTITY_RELATIVE_SPHERE_INSIDE = register(
			new PositionTypeClosestEntityRelativeSphereInside("CLOSEST_ENTITY_RELATIVE_SPHERE_INSIDE"));
	public final PositionTypeClosestEntityRelativeSphereOutside CLOSEST_ENTITY_RELATIVE_SPHERE_OUTSIDE = register(
			new PositionTypeClosestEntityRelativeSphereOutside("CLOSEST_ENTITY_RELATIVE_SPHERE_OUTSIDE"));

	public final PositionTypeCylinderInside CYLINDER_INSIDE = register(new PositionTypeCylinderInside("CYLINDER_INSIDE"));
	public final PositionTypeCylinderOutside CYLINDER_OUTSIDE = register(new PositionTypeCylinderOutside("CYLINDER_OUTSIDE"));
	public final PositionTypePlayerRelativeCylinderInside PLAYER_RELATIVE_CYLINDER_INSIDE = register(
			new PositionTypePlayerRelativeCylinderInside("PLAYER_RELATIVE_CYLINDER_INSIDE"));
	public final PositionTypePlayerRelativeCylinderOutside PLAYER_RELATIVE_CYLINDER_OUTSIDE = register(
			new PositionTypePlayerRelativeCylinderOutside("PLAYER_RELATIVE_CYLINDER_OUTSIDE"));
	public final PositionTypeClosestEntityRelativeCylinderInside CLOSEST_ENTITY_RELATIVE_CYLINDER_INSIDE = register(
			new PositionTypeClosestEntityRelativeCylinderInside("CLOSEST_ENTITY_RELATIVE_CYLINDER_INSIDE"));
	public final PositionTypeClosestEntityRelativeCylinderOutside CLOSEST_ENTITY_RELATIVE_CYLINDER_OUTSIDE = register(
			new PositionTypeClosestEntityRelativeCylinderOutside("CLOSEST_ENTITY_RELATIVE_CYLINDER_OUTSIDE"));

	// ----- values
	public static PositionTypes inst() {
		return GCore.inst().getPositionTypes();
	}

	@Override
	public PositionType defaultValue() {
		return NONE;
	}

}
