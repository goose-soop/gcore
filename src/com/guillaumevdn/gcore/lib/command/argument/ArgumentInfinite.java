package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentInfinite extends Argument<String> {

	public ArgumentInfinite(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	// ----- do
	@Override
	public String consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		String arg = StringUtils.toTextString(" ", call.getArguments());
		call.getArguments().clear();
		return arg;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return getUsage() == null ? null : getUsage().parseLines();
	}

}
