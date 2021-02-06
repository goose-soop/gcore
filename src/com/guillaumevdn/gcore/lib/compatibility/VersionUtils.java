package com.guillaumevdn.gcore.lib.compatibility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
final class VersionUtils {

	/*static Version getCurrent() {
		String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
		List<Version> versions = CollectionUtils.asList(Version.values());
		Collections.reverse(versions);
		for (Version version : versions) {
			if (version.getNames().stream().anyMatch(v -> bukkitVersion.contains(v))) {
				return version;
			}
		}
		return Version.UNKNOWN;
	}*/

	static Version getCurrent() {
		List<Version> versions = CollectionUtils.asList(Version.values());
		Collections.reverse(versions);
		for (Version version : versions) {
			if (Package.getPackage("net.minecraft.server." + version.getPackageName()) != null) {
				return version;
			}
		}
		// didn't find a version ; try finding package name
		Package nms = Arrays.stream(Package.getPackages()).filter(p -> p.getName().startsWith("net.minecraft.server.v")).findFirst().orElse(null);
		if (nms != null) {
			String packageName = nms.getName().substring("net.minecraft.server.".length()).split("\\.")[0];
			Version.UNSUPPORTED.packageName = packageName;
			return Version.UNSUPPORTED;
		} else {
			return Version.UNKNOWN;
		}
	}

}
