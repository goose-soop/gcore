package com.guillaumevdn.gcore.lib.entity;

import org.bukkit.entity.Entity;

/**
 * @author GuillaumeVDN
 */
public enum VehicleType {

	BOAT, MINECART, HORSE, DONKEY, MULE, LLAMA, PIG;

	public static VehicleType fromEntity(Entity vehicle) {
		if (vehicle != null) {
			String vehicleType = vehicle.getType().toString().toUpperCase();
			for (VehicleType type : values()) {
				if (type.name().contains(vehicleType)) {
					return type;
				}
			}
		}
		return null;
	}

}
