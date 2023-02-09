package com.guillaumevdn.gcore.lib.compatibility.worldguard;

import java.util.Objects;

import org.bukkit.World;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class WGRegion {

	private World world;
	private String regionId;

	public WGRegion(World world, String regionId) {
		this.world = world;
		this.regionId = regionId;
	}

	// ----- get
	public World getWorld() {
		return world;
	}

	public String getRegionId() {
		return regionId;
	}

	// ----- object
	@Override
	public int hashCode() {
		return Objects.hash(world.getName(), regionId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		WGRegion other = ObjectUtils.castOrNull(obj, WGRegion.class);
		return other != null && other.world.equals(world) && other.regionId.equalsIgnoreCase(regionId);
	}

}
