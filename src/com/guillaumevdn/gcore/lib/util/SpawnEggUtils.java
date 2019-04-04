package com.guillaumevdn.gcore.lib.util;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import com.guillaumevdn.gcore.lib.material.Mat;

public class SpawnEggUtils {

	public static EntityType getSpawnedType(ItemStack item) {
		try {
			if (Utils.instanceOf(item.getItemMeta(), SpawnEggMeta.class)) {
				SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
				return meta.getSpawnedType();
			}
		} catch (UnsupportedOperationException ignored) {// 1.13+, check item type
			Mat mat = Mat.from(item);
			if (mat.getModernName().endsWith("_SPAWN_EGG")) {
				EntityType type = Utils.valueOfOrNull(EntityType.class, mat.getModernName().substring(0, "_SPAWN_EGG".length() - 3));
				if (type != null) {
					return type;
				}
			}
		}
		return null;
	}

}
