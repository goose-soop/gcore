package com.guillaumevdn.gcore.lib.command.argument;

import org.bukkit.command.CommandSender;

/**
 * @author GuillaumeVDN
 */
public interface PlayerArgument {

	boolean canUseBecauseOfSenderIfNone(CommandSender sender);

}
