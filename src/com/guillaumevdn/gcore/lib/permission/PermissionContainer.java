package com.guillaumevdn.gcore.lib.permission;

import java.util.HashMap;
import java.util.Map;

import com.guillaumevdn.gcore.lib.GPlugin;

/**
 * @author GuillaumeVDN
 */
public abstract class PermissionContainer {

	private final GPlugin plugin;
	private final Map<String, Permission> permissions = new HashMap<>();
	private Permission admin = null;

	public PermissionContainer(GPlugin plugin) {
		this.plugin = plugin;
	}

	// get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final Map<String, Permission> getPermissions() {
		return permissions;
	}

	public final Permission getAdminPermission() {
		if (admin == null) throw new IllegalStateException();
		return admin;
	}

	// set
	public final Permission setAdmin(Permission parent, String name) {
		if (admin != null) throw new IllegalStateException();
		return admin = set(parent, name);
	}

	public final Permission set(Permission parent, String name) {
		Permission permission = new Permission(parent, name);
		permissions.put(permission.getFullName(), permission);
		return permission;
	}

}
