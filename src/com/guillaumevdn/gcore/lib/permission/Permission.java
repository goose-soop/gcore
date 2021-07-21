package com.guillaumevdn.gcore.lib.permission;

import com.guillaumevdn.gcore.lib.player.PlayerUtils;

/**
 * @author GuillaumeVDN
 */
public final class Permission {

	private String name;

	public Permission(String name) {
		this.name = name;
	}

	// ----- get
	public String getName() {
		return name;
	}

	// ----- methods
	public boolean has(Object target) {
		return PlayerUtils.hasPermission(target, name);
	}

}
