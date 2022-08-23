package com.guillaumevdn.gcore.integration.mythicmobs.v5;

import java.util.Map;

import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeAreaInside;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeAreaOutside;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeCylinderInside;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeCylinderOutside;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeSingle;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeSphereInside;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.position.PositionTypeClosestMythicMobRelativeSphereOutside;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.reflection.Reflection;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MythicMob;

/**
 * @author GuillaumeVDN
 */
public class IntegrationInstanceMythicMobsV5 extends IntegrationInstance {

	public IntegrationInstanceMythicMobsV5(Integration integration) {
		super(integration);
		registerSerializer(MythicMob.class, value -> value.getInternalName(), string -> MythicProvider.get().getMobManager().getMythicMob(string).orElse(null));
	}

	// ----- activation
	private Map<String, Class<? extends PositionType>> types = CollectionUtils.asMap(
			"CLOSEST_MYTHICMOB_RELATIVE_AREA_INSIDE", PositionTypeClosestMythicMobRelativeAreaInside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_AREA_OUTSIDE", PositionTypeClosestMythicMobRelativeAreaOutside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_CYLINDER_INSIDE", PositionTypeClosestMythicMobRelativeCylinderInside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_CYLINDER_OUTSIDE", PositionTypeClosestMythicMobRelativeCylinderOutside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_SPHERE_INSIDE", PositionTypeClosestMythicMobRelativeSphereInside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_SPHERE_OUTSIDE", PositionTypeClosestMythicMobRelativeSphereOutside.class,
			"CLOSEST_MYTHICMOB_RELATIVE_SINGLE", PositionTypeClosestMythicMobRelativeSingle.class
			);

	@Override
	public boolean activate() {
		types.forEach((id, typeClass) -> {
			try {
				PositionTypes.inst().register(Reflection.newInstance(typeClass, id).get());
			} catch (Throwable error) {
				error.printStackTrace();
			}
		});
		return true;
	}

	@Override
	public void deactivate() {
		types.keySet().forEach(id -> PositionTypes.inst().unregister(id));
	}

}
