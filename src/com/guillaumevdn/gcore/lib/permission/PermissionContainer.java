package com.guillaumevdn.gcore.lib.permission;

import java.util.Collections;
import java.util.Map;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;

/**
 * @author GuillaumeVDN
 */
public abstract class PermissionContainer {

	private final GPlugin plugin;
	private final LowerCaseHashMap<Permission> permissions = new LowerCaseHashMap<>();
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
		if (admin != null) throw new IllegalStateException();
		return admin = set(name);
	}

	public final Permission set(String name) {
		Permission permission = new Permission(name);
		permissions.put(name, permission);
		return permission;
	}

}
