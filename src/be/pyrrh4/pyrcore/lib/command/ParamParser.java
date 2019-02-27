package be.pyrrh4.pyrcore.lib.command;

import org.bukkit.command.CommandSender;

public interface ParamParser<T> {

	// methods
	public T parse(CommandSender sender, Param parameter, String value);

}