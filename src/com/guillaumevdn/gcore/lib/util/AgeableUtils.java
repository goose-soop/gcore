package com.guillaumevdn.gcore.lib.util;

import org.bukkit.block.data.Ageable;

public class AgeableUtils {

	public static boolean instanceOf(Object object) {
		return object instanceof Ageable;
	}

	public static boolean isFullyAged(Object object) {
		Ageable ageable = (Ageable) object;
		return ageable.getAge() >= ageable.getMaximumAge();
	}

}
