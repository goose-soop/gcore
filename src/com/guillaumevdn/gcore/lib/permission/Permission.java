package com.guillaumevdn.gcore.lib.permission;

import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.player.PlayerUtils;

/**
 * @author GuillaumeVDN
 */
public final class Permission {

	private String name;
	private String alternativeName;

	public Permission(String name, @Nullable String alternativeName) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Nullable
	public String getAlternativeName() {
		return alternativeName;
	}

	public boolean has(Object target) {
		return PlayerUtils.hasPermission(target, name) || (alternativeName != null && PlayerUtils.hasPermission(target, alternativeName));
	}

}
