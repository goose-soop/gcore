package com.guillaumevdn.gcore.lib.permission;

import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class Permission {

	private Permission parent;
	private String part, fullName;

	public Permission(String part) {
		this(null, part);
	}

	public Permission(Permission parent, String part) {
		if ((part = StringUtils.nonEmptyAlphanumericOrNull(part.toLowerCase().replace("_", ""))) == null) throw new IllegalArgumentException("part isn't alphanumeric");
		this.parent = parent;
		this.part = part;
		fullName = part;
		parent = this;
		while ((parent = parent.getParent()) != null) {
			fullName = parent.getPart() + "." + fullName;
		}
	}

	// get
	public Permission getParent() {
		return parent;
	}

	public String getPart() {
		return part;
	}

	public String getFullName() {
		return fullName;
	}

	// methods
	public boolean has(Object target) {
		return PlayerUtils.hasPermission(target, fullName);
	}

}
