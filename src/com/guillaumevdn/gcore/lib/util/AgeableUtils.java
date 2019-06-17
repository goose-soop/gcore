package com.guillaumevdn.gcore.lib.util;

import org.bukkit.block.data.Ageable;

public class AgeableUtils {

	public static boolean instanceOf(Object object) throws Throwable {
		return object instanceof Ageable;
	}

	public static boolean isFullyAged(Object object) throws Throwable {
		Ageable ageable = (Ageable) object;
		return ageable.getAge() >= ageable.getMaximumAge();
	}

}
