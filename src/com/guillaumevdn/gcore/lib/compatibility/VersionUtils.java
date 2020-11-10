package com.guillaumevdn.gcore.lib.compatibility;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
final class VersionUtils {

	static Version getCurrent() {
		String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
		List<Version> versions = CollectionUtils.asList(Version.values());
		Collections.reverse(versions);
		for (Version version : versions) {
			if (version.getNames().stream().anyMatch(v -> bukkitVersion.contains(v))) {
				return version;
			}
		}
		return Version.ANY;
	}

}
