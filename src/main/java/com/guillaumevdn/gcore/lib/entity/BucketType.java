package com.guillaumevdn.gcore.lib.entity;

import org.bukkit.block.Block;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;

/**
 * @author GuillaumeVDN
 */
public enum BucketType {

	LAVA, WATER, MILK;

	public static BucketType fromBlock(Block block) {
		Mat mat = Mat.fromBlock(block).orElse(null);
		if (mat != null) {
			if (mat.getData().getDataName().toUpperCase().contains("LAVA"))
				return LAVA;
			if (mat.getData().getDataName().toUpperCase().contains("WATER"))
				return WATER;
		}
		return null;
	}

}
