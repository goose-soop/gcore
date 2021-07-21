package com.guillaumevdn.gcore.lib.entity;

import org.bukkit.entity.Projectile;

/**
 * @author GuillaumeVDN
 */
public enum ProjectileType {

	ARROW("ARROW"),
	SPECTRAL_ARROW("SPECTRAL_ARROW"),
	EGG("EGG"),
	SNOWBALL("SNOWBALL"),
	FIREBALL("FIREBALL"),
	EXP_BOTTLE("THROWN_EXP_BOTTLE"),
	ENDER_PEARL("ENDER_PEARL"),
	TRIDENT("TRIDENT");

	// ----- base
	private String entityType;

	private ProjectileType(String entityType) {
		this.entityType = entityType;
	}

	// ----- get
	public String getEntityType() {
		return entityType;
	}

	// ----- static
	public static ProjectileType fromProjectile(Projectile projectile) {
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
