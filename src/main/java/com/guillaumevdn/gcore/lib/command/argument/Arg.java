package com.guillaumevdn.gcore.lib.command.argument;

import com.guillaumevdn.gcore.lib.command.CommandCall;

/**
 * @author GuillaumeVDN
 */
public interface Arg {

	boolean has(CommandCall call);

	String getName();

}
