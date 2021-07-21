package com.guillaumevdn.gcore.lib.compatibility.worldguard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.location.LocationUtils;
import com.guillaumevdn.gcore.lib.location.Point;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiFunction;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureFunction;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public final class WorldGuardCompat {

	// ----- get region
	private static final ReflectionProcedureBiFunction<World, String, Object> GET_REGION = new ReflectionProcedureBiFunction<World, String, Object>()
			.setIf(Version.ATLEAST_1_13, (world, regionId) -> {
				Object bukkitWorld = Reflection.newInstance("com.sk89q.worldedit.bukkit.BukkitWorld", world).justGet();
				return Reflection.invokeMethod("com.sk89q.worldguard.WorldGuard", "getInstance", null).invokeMethod("getPlatform").invokeMethod("getRegionContainer").invokeMethod("get", bukkitWorld).invokeMethod("getRegion", regionId);
			})
			.orElse((world, regionId) -> ReflectionObject.ofPlugin("WorldGuard").invokeMethod("getRegionContainer").invokeMethod("get", world).invokeMethod("getRegion", regionId).orNull());

	public static Object getRegion(World world, String regionId) {
		if (!PluginUtils.isPluginEnabled("WorldGuard")) {
			return null;
		}
		return GET_REGION.process(world, regionId);
	}

	// ----- get regions
	private static final ReflectionProcedureFunction<World, Collection<String>> GET_REGIONS = new ReflectionProcedureFunction<World, Collection<String>>()
			.setIf(Version.ATLEAST_1_13, (world) -> {
				Object bukkitWorld = Reflection.newInstance("com.sk89q.worldedit.bukkit.BukkitWorld", world).justGet();
				Map<String, ?> map = Reflection.invokeMethod("com.sk89q.worldguard.WorldGuard", "getInstance", null).invokeMethod("getPlatform").invokeMethod("getRegionContainer").invokeMethod("get", bukkitWorld).invokeMethod("getRegions").get();
				return map == null ? null : CollectionUtils.asList(map.keySet());
			})
			.orElse((world) -> {
				Map map = ReflectionObject.ofPlugin("WorldGuard").invokeMethod("getRegionContainer").invokeMethod("get", world).invokeMethod("getRegions").get();
				return map == null ? null : CollectionUtils.asList(map.keySet());
			});

	public static Collection<String> getRegions(World world) {
		if (!PluginUtils.isPluginEnabled("WorldGuard")) {
			return new ArrayList<>();
		}
		return GET_REGIONS.process(world);
	}

	// ----- get region bounds
	private static final ReflectionProcedureBiFunction<World, String, Pair<Point, Point>> GET_REGION_BOUNDS = new ReflectionProcedureBiFunction<World, String, Pair<Point, Point>>()
			.orElse((world, regionId) -> {
				ReflectionObject region = ReflectionObject.ofOrNull(getRegion(world, regionId));
				if (region == null) return null;
				ReflectionObject regionMin = region.invokeMethod("getMinimumPoint");
				ReflectionObject regionMax = region.invokeMethod("getMaximumPoint");
				Point min = new Point(world.getName(), regionMin.invokeMethod("getX").get(), regionMin.invokeMethod("getY").get(), regionMin.invokeMethod("getZ").get());
				Point max = new Point(world.getName(), regionMax.invokeMethod("getX").get(), regionMax.invokeMethod("getY").get(), regionMax.invokeMethod("getZ").get());
				return Pair.of(min, max);
			});

	public static Pair<Point, Point> getRegionBounds(World world, String regionId) {
		if (!PluginUtils.isPluginEnabled("WorldGuard")) {
			return null;
		}
		return GET_REGION_BOUNDS.process(world, regionId);
	}

	// ----- is in region
	public static boolean isInRegion(World world, String regionId, Location location) {
		if (!PluginUtils.isPluginEnabled("WorldGuard")) {
			return false;
		}
		if (!world.equals(location.getWorld())) {
			return false;
		}
		Pair<Point, Point> bounds = getRegionBounds(world, regionId);
		return bounds != null && LocationUtils.isLocationContained(location, bounds.getA(), bounds.getB());
	}

}
