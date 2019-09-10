package com.guillaumevdn.gcore.lib.util;

import org.bukkit.entity.Projectile;

public enum ProjectileType {

	// values
	ARROW("ARROW"),
	EGG("EGG"),
	SNOWBALL("SNOWBALL"),
	FIREBALL("FIREBALL"),
	EXP_BOTTLE("THROWN_EXP_BOTTLE"),
	ENDER_PEARL("ENDER_PEARL"),
	TRIDENT("TRIDENT");

    // base
	private String entityType;

	private ProjectileType(String entityType) {
		this.entityType = entityType;
	}

	// get
	public String getEntityType() {
		return entityType;
	}

	// static methods
	public static ProjectileType get(String name) {
		name = name.toUpperCase();
		for (ProjectileType type : values()) {
			if (type.name().equals(name)) {
				return type;
			}
		}
		return null;
	}

	public static ProjectileType get(Projectile projectile) {
		if (projectile != null) {
			String entityType = projectile.getType().name();
			for (ProjectileType type : values()) {
				if (type.entityType.equals(entityType)) {
					return type;
				}
			}
		}
		return null;
	}

}
