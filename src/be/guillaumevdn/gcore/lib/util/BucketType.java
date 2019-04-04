package be.guillaumevdn.gcore.lib.util;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import be.guillaumevdn.gcore.lib.material.Mat;

public enum BucketType {

	LAVA, WATER, MILK;

	public static BucketType get(String name) {
		if (name == null) return null;
		if (name.equalsIgnoreCase("LAVA")) return LAVA;
		if (name.equalsIgnoreCase("WATER")) return WATER;
		if (name.equalsIgnoreCase("MILK")) return MILK;
		return null;
	}

	public static BucketType get(Mat material) {
		if (material == null) return null;
		if (material.equals(Mat.LAVA_BUCKET)) return LAVA;
		if (material.equals(Mat.WATER_BUCKET)) return WATER;
		if (material.equals(Mat.MILK_BUCKET)) return MILK;
		return null;
	}

	public static BucketType get(Block block) {
		Mat material = Mat.from(block);
		if (material.equals(Mat.LAVA) || material.equals(Mat.STATIONARY_LAVA)) return LAVA;
		if (material.equals(Mat.WATER) || material.equals(Mat.STATIONARY_WATER)) return WATER;
		return null;
	}

	public static BucketType get(Entity entity) {
		if (entity.getType().toString().contains("COW")) return MILK;
		return null;
	}

}
