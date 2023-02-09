package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentString extends Argument<String> {

	public ArgumentString(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	@Override
	public String consume(CommandCall call) {
		return call.getArguments().isEmpty() ? null : call.getArguments().remove(0);
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return getUsage() == null ? null : getUsage().parseLines();
	}

}
