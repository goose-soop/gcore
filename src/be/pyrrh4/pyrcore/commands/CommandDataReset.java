package be.pyrrh4.pyrcore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PCPerm;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.command.CommandArgument;
import be.pyrrh4.pyrcore.lib.command.CommandCall;
import be.pyrrh4.pyrcore.lib.command.Param;
import be.pyrrh4.pyrcore.lib.command.ParamParser;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Messenger.Level;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CommandDataReset extends CommandArgument {

	private static final Param paramPlugin = new Param(Utils.asList("plugin"), "name", PCPerm.PYRCORE_ADMIN, false, true);
	private List<PyrPlugin> confirm = new ArrayList<PyrPlugin>();

	public CommandDataReset() {
		super(PyrCore.inst(), Utils.asList("reset"), "reset data for a plugin", PCPerm.PYRCORE_ADMIN, false, paramPlugin);
	}

	@Override
	public void perform(CommandCall call) {
		CommandSender sender = call.getSender();
		final PyrPlugin plugin = paramPlugin.get(call, PLUGIN_PARSER);
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
				}.runTaskLater(PyrCore.inst(), 20L * 10L);
				Messenger.send(sender, Level.SEVERE_INFO, plugin.getName(), "Execute the command again if you really wish to reset all plugin and users data for plugin. This action will expire in 10 seconds.");
			}
		}
	}

	public static final ParamParser<PyrPlugin> PLUGIN_PARSER = new ParamParser<PyrPlugin>() {
		public PyrPlugin parse(CommandSender sender, Param parameter, String value) {
			Plugin plugin = Utils.getPlugin(value);
			// doesn't exist
			if (!Utils.instanceOf(plugin, PyrPlugin.class)) {
				PCLocale.MSG_PYRCORE_INVALIDPLUGINPARAM.send(sender, "{parameter}", "-" + parameter.toString() + (parameter.getDescription() == null ? "" : ":" + value.toString()));
				return null;
			}
			// return plugin
			return (PyrPlugin) plugin;
		}
	};

}
