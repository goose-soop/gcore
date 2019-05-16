package com.guillaumevdn.gcore.lib.util;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.GCore;

public class VersionUtils {

	public static ServerVersion getServerVersion() {
		// forced version
		if (GCore.inst().getConfiguration().contains("forced_version_compat")) {
			ServerVersion forced = GCore.inst().getConfiguration().getEnumValue("forced_version_compat", ServerVersion.class, (ServerVersion) null);
			if (forced != null) {
				return forced;
			} else {
				GCore.inst().error("Couldn't load custom server version '" + GCore.inst().getConfiguration().getString("forced_version_compat", null) + "', trying to detect it automatically anyway");
			}
		}
		// detect version
		String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
		List<ServerVersion> versions = Utils.asList(ServerVersion.values());
		Collections.reverse(versions);
		for (ServerVersion version : versions) {
			if (bukkitVersion.contains(version.getName())) {
				return version;
			}
		}
		return ServerVersion.UNSUPPORTED;
	}

	public static ServerVersion getHighestServerVersion() {
		ServerVersion[] versions = ServerVersion.values();
		return versions[versions.length - 1];
	}

	public static ServerImplementation getServerImplementation() {
		try {
			Class.forName("com.destroystokyo.paper.PaperConfig");
			return ServerImplementation.PAPERSPIGOT;
		} catch (Throwable ignored) {}
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			return ServerImplementation.SPIGOT;
		} catch (Throwable ignored) {}
		return ServerImplementation.CRAFTBUKKIT;
	}

}
