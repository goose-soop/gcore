package com.guillaumevdn.gcore.lib.command.generic;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;

/**
 * @author GuillaumeVDN
 */
public final class GenericReload extends Subcommand {

	private final GPlugin plugin;

	public GenericReload(GPlugin plugin) {
		super(false, plugin.getPermissionContainer().getAdminPermission(), TextGeneric.commandDescriptionGenericReload, ConfigGCore.genericCommandsAliasesReload);
		this.plugin = plugin;
	}

	@Override
	public void perform(CommandCall call) {
		if (!plugin.reload(() -> {
			if (plugin.isActivated()) {
				TextGCore.messagePluginReloaded.replace("{plugin}", () -> plugin.getName()).send(call);
			} else {
				call.getSender().sendMessage("§cCould not enable " + plugin.getName() + ", please check the console output.");
			}
		})) {
			TextGCore.messagePluginManipulateError.replace("{plugin}", () -> plugin.getName()).send(call);
		}
	}

}
