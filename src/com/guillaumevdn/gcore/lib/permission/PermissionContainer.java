package com.guillaumevdn.gcore.lib.permission;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;

/**
 * @author GuillaumeVDN
 */
public abstract class PermissionContainer {

	private final GPlugin plugin;
	private final LowerCaseHashMap<Permission> permissions = new LowerCaseHashMap<>(10, 0.75f);
	private Permission admin = null;

	public PermissionContainer(GPlugin plugin) {
		this.plugin = plugin;
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final Map<String, Permission> getPermissions() {
		return Collections.unmodifiableMap(permissions);
	}

	public final Permission getAdminPermission() {
		if (admin == null) throw new IllegalStateException();
		return admin;
	}

	// ----- set
	public final Permission setAdmin(String name) {
		return setAdmin(name, null);
	}

	public final Permission setAdmin(String name, @Nullable String alternativeName) {
		if (admin != null) throw new IllegalStateException();
		return admin = set(name, alternativeName);
	}

	public final Permission set(String name) {
		return set(name, null);
	}

	public final Permission set(String name, @Nullable String alternativeName) {
		Permission permission = new Permission(name, alternativeName);
		permissions.put(name, permission);
		return permission;
	}

}
