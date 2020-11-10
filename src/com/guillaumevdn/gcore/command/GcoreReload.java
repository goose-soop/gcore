package com.guillaumevdn.gcore.command;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;

/**
 * @author GuillaumeVDN
 */
public final class GcoreReload extends Subcommand {

	public GcoreReload() {
		super(false, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreReload, ConfigGCore.commandsAliasesReload);
	}

	@Override
	public void perform(CommandCall call) {
		if (GCore.inst().isReloading()) {
			return;
		}
		if (!GCore.inst().reload(() -> TextGCore.messagePluginReloaded.replace("{plugin}", () -> GCore.inst().getName()).send(call))) {
			TextGCore.messagePluginManipulateError.replace("{plugin}", () -> GCore.inst().getName()).send(call);
		}
	}

}
