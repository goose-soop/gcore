package com.guillaumevdn.gcore.lib.util;

import org.bukkit.entity.Entity;

public enum VehicleType
{
	// ------------------------------------------------------------
	// Enum
	// ------------------------------------------------------------

	BOAT, MINECART, HORSE, DONKEY, MULE, LLAMA, PIG;

	// ------------------------------------------------------------
	// Static methods
	// ------------------------------------------------------------

	public static VehicleType get(String name) {
		for (VehicleType type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	public static VehicleType get(Entity vehicle) {
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
