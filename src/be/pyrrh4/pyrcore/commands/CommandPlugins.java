package be.pyrrh4.pyrcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandPlugins extends CommandArgument {

	public CommandPlugins() {
		super(PyrCore.inst(), Utils.asList("plugins", "pl"), "list registered plugins", PCPerm.PYRCORE_ADMIN, false);
	}

	@Override
	public void perform(CommandCall call) {
		List<String> plugins = new ArrayList<String>();
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (pl instanceof PyrPlugin) {
				plugins.add("§a" + pl.getName() + " v" + pl.getDescription().getVersion());
			}
		}
		PCLocale.MSG_PYRCORE_PLUGINSLIST.send(call.getSender(), "{count}", plugins.size(), "{plural}", Utils.getPlural(plugins.size()), "{plugins}", plugins.isEmpty() ? "/" : plugins);
	}

}
