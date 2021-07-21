package com.guillaumevdn.gcore;

import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.permission.PermissionContainer;

/**
 * @author GuillaumeVDN
 */
public class PermissionGCore extends PermissionContainer {

	private static PermissionGCore instance = null;
	public static PermissionGCore inst() { return instance; }

	public PermissionGCore() {
		super(GCore.inst());
		instance = this;
	}

	public final Permission gcoreAdmin = setAdmin("gcore.admin");
	public final Permission gcoreBypassCommandRestrictions = set("gcore.bypass_command_restrictions");

}
