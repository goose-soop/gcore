package be.guillaumevdn.gcore.lib.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.GLocale;
import be.guillaumevdn.gcore.lib.GPlugin;

public class CommandCall {

	// base
	private GPlugin plugin;
	private CommandSender sender;
	private String commandName;
	private List<String> arguments;
	private Map<String, String> parameters;

	public CommandCall(GPlugin plugin, CommandSender sender, String commandName, List<String> arguments, Map<String, String> parameters) {
		this.plugin = plugin;
		this.sender = sender;
		this.commandName = commandName;
		this.arguments = Collections.unmodifiableList(arguments);
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	// get
	public GPlugin getPlugin() {
		return plugin;
	}

	public CommandSender getSender() {
		return sender;
	}

	public Player getSenderAsPlayer() {
		return (Player) sender;
	}

	public boolean senderIsPlayer() {
		return sender instanceof Player;
	}

	public boolean ensureSenderIsPlayer() {
		// isn't player
		if (!senderIsPlayer()) {
			GLocale.MSG_GENERIC_NOTPLAYER.send(sender, "{plugin}", plugin.getName());
			return false;
		}
		// is player
		return true;
	}

	public String getCommandName() {
		return commandName;
	}

	public String getAllArguments() {
		return getAllArguments(0);
	}

	public String getAllArguments(int beginIndex) {
		String result = "";
		for (int i = beginIndex; i < arguments.size(); i++) {
			result += " " + arguments.get(i);
		}
		if (result.length() > 0) {
			result = result.substring(1);
		}
		return result;
	}

	public List<String> getArguments() {
		return arguments;
	}

	// params
	public Map<String, String> getParameters() {
		return parameters;
	}

}
