package be.guillaumevdn.gcore.lib.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import be.guillaumevdn.gcore.lib.Perm;
import be.guillaumevdn.gcore.lib.GPlugin;
import be.guillaumevdn.gcore.lib.util.Pair;
import be.guillaumevdn.gcore.lib.util.Utils;

public class CommandRoot extends CommandArgument implements CommandExecutor, TabCompleter {

	// base
	public CommandRoot(GPlugin plugin, List<String> aliases, String description, Perm permission, boolean playerOnly) {
		super(plugin, aliases, description, permission, playerOnly);
	}

	public CommandRoot(GPlugin plugin, List<String> aliases, String description, Perm permission, boolean playerOnly, Param... expectedParams) {
		super(plugin, aliases, description, permission, playerOnly, expectedParams);
	}

	// overriden methods
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// get raw
		String raw = label;
		for (String arg : args) {
			raw += " " + arg;
		}
		String[] split = raw.split(" -");
		// get arguments
		List<String> arguments = Utils.asList(split[0].split(" "));
		// get parameters
		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 1; i < split.length; i++) {
			String spl = split[i];
			if (spl.contains(":")) {
				if (spl.charAt(spl.length() - 1) == ':') spl = spl.substring(0, spl.length() - 1);
				Pair<String, String> splSplit = Utils.separateRootAtChar(spl, ':');
				parameters.put(splSplit.getA().toLowerCase(), splSplit.getB().isEmpty() ? null : splSplit.getB());
			} else {
				parameters.put(spl.toLowerCase(), null);
			}
		}
		// call
		call(new CommandCall(getPlugin(), sender, command.getName(), arguments, parameters), 0);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// get raw
		String raw = label;
		for (int i = 0; i < args.length - 1; i++) {// - 1 because we don't include the partial argument in the call object
			raw += " " + args[i];
		}
		String[] split = raw.split(" -");
		// get arguments
		List<String> arguments = Utils.asList(split[0].split(" "));
		// get parameters
		Map<String, String> parameters = new HashMap<String, String>();
		for (int i = 1; i < split.length; i++) {
			String spl = split[i];
			if (spl.contains(" ")) {
				if (spl.charAt(spl.length() - 1) == ' ') spl = spl.substring(0, spl.length() - 1);
				Pair<String, String> splSplit = Utils.separateRootAtChar(spl, ' ');
				parameters.put(splSplit.getA().toLowerCase(), splSplit.getB().isEmpty() ? null : splSplit.getB());
			} else {
				parameters.put(spl.toLowerCase(), null);
			}
		}
		// tab complete
		List<String> result = tabComplete(new CommandCall(getPlugin(), sender, command.getName(), arguments, parameters), args.length == 0 ? "" : args[args.length - 1], 0);
		return result == null ? Utils.emptyList() : result;
	}

}
