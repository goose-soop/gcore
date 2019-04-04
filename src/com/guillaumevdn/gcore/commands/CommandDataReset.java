package com.guillaumevdn.gcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Param;
import com.guillaumevdn.gcore.lib.command.ParamParser;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandDataReset extends CommandArgument {

	private static final Param paramPlugin = new Param(Utils.asList("plugin"), "name", GPerm.GCORE_ADMIN, false, true);
	private List<GPlugin> confirm = new ArrayList<GPlugin>();

	public CommandDataReset() {
		super(GCore.inst(), Utils.asList("reset"), "reset data for a plugin", GPerm.GCORE_ADMIN, false, paramPlugin);
	}

	@Override
	public void perform(CommandCall call) {
		CommandSender sender = call.getSender();
		final GPlugin plugin = paramPlugin.get(call, PLUGIN_PARSER);
		if (plugin != null) {
			if (confirm.contains(plugin)) {
				confirm.remove(plugin);
				plugin.resetData();
				Messenger.send(sender, Level.NORMAL_SUCCESS, plugin.getName(), "Data was reset.");
			} else {
				confirm.add(plugin);
				new BukkitRunnable() {
					@Override
					public void run() {
						confirm.remove(plugin);
					}
				}.runTaskLater(GCore.inst(), 20L * 10L);
				Messenger.send(sender, Level.SEVERE_INFO, plugin.getName(), "Execute the command again if you really wish to reset all plugin and users data for plugin. This action will expire in 10 seconds.");
			}
		}
	}

	public static final ParamParser<GPlugin> PLUGIN_PARSER = new ParamParser<GPlugin>() {
		public GPlugin parse(CommandSender sender, Param parameter, String value) {
			Plugin plugin = Utils.getPlugin(value);
			// doesn't exist
			if (!Utils.instanceOf(plugin, GPlugin.class)) {
				GLocale.MSG_GCORE_INVALIDPLUGINPARAM.send(sender, "{parameter}", "-" + parameter.toString() + (parameter.getDescription() == null ? "" : ":" + value.toString()));
				return null;
			}
			// return plugin
			return (GPlugin) plugin;
		}
	};

}
