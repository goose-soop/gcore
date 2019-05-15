package com.guillaumevdn.gcore.lib.util;

public enum ServerImplementation {

	CRAFTBUKKIT,
	SPIGOT,
	PAPERSPIGOT;

	// current
	public static final ServerImplementation CURRENT = VersionUtils.getServerImplementation();

}
