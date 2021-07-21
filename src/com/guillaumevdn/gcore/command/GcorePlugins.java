package com.guillaumevdn.gcore.command;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class GcorePlugins extends Subcommand {

	public GcorePlugins() {
		super(false, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcorePlugins, ConfigGCore.commandsAliasesPlugins);
	}

	@Override
	public void perform(CommandCall call) {
		int inactive = 0;
		List<String> plugins = new ArrayList<>();
		for (GPlugin plugin : PluginUtils.getGPlugins()) {
			if (plugin.isEnabled() && plugin.isActivated()) {
				plugins.add("§a" + plugin.getName());
			} else {
				++inactive;
				plugins.add("§c" + plugin.getName());
			}
		}
		final int inactiveF = inactive;
		TextGCore.messagePluginList.replace("{count}", () -> plugins.size()).replace("{active}", () -> plugins.size() - inactiveF).replace("{inactive}", () -> inactiveF).replace("{plugins}", () -> StringUtils.toTextString(", ", plugins)).send(call);
	}

}
