package com.guillaumevdn.gcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandPlugins extends CommandArgument {

	public CommandPlugins() {
		super(GCore.inst(), Utils.asList("plugins", "pl"), "list registered plugins", GPerm.GCORE_ADMIN, false);
	}

	@Override
	public void perform(CommandCall call) {
		List<String> plugins = new ArrayList<String>();
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (pl instanceof GPlugin) {
				plugins.add("§a" + pl.getName() + " v" + pl.getDescription().getVersion());
			}
		}
		GLocale.MSG_GCORE_PLUGINSLIST.send(call.getSender(), "{count}", plugins.size(), "{plural}", Utils.getPlural(plugins.size()), "{plugins}", plugins.isEmpty() ? "/" : plugins);
	}

}
