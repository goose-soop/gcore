package com.guillaumevdn.gcore.lib.permission;

import com.guillaumevdn.gcore.lib.player.PlayerUtils;

/**
 * @author GuillaumeVDN
 */
public final class Permission {

	private final String name;

	public Permission(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean has(Object target) {
		return PlayerUtils.hasPermission(target, name);
	}

}
