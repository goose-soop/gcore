package be.pyrrh4.pyrcore.lib.util;

public enum ServerImplementation {

	CRAFTBUKKIT,
	SPIGOT,
	PAPERSPIGOT;

	// current
	public static final ServerImplementation CURRENT = Utils.getServerImplementation();

}
