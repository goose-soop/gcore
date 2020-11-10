package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.worldguard.WGRegion;
import com.guillaumevdn.gcore.lib.compatibility.worldguard.WorldGuardCompat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementWorldguardRegion extends ElementAbstractEnum<WGRegion> {

	private final ElementWorld world;

	public ElementWorldguardRegion(Element parent, ElementWorld world, String id, Need need, Text editorDescription) {
		super(Serializer.of(WGRegion.class, region -> region.getRegionId(), string -> {
			if (!PluginUtils.isPluginEnabled("WorldGuard")) {
				return null;
			}
			World w = world.parseGeneric().orNull();
			if (w == null) {
				return null;
			}
			Object validRegion = WorldGuardCompat.getRegion(w, string);
			return validRegion == null ? null : new WGRegion(w, string);
		}, false), false, parent, id, need, editorDescription);
		this.world = world;
	}

	// get
	public ElementWorld getWorld() {
		return world;
	}

	@Override
	public List<WGRegion> getValues() {
		if (!PluginUtils.isPluginEnabled("WorldGuard")) {
			return null;
		}
		World w = world.parseGeneric().orNull();
		if (w == null) {
			return null;
		}
		List<WGRegion> result = new ArrayList<>();
		WorldGuardCompat.getRegions(w).forEach(regionId -> result.add(new WGRegion(w, regionId)));
		return result;
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.COMPASS;
	}

}
