package com.guillaumevdn.gcore.lib.command;

import org.bukkit.command.CommandSender;

public interface ParamParser<T> {

	// methods
	public T parse(CommandSender sender, Param parameter, String value);

}