package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentInteger.TabCompleteMode;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentDouble extends Argument<Double> {

	private List<String> tabComplete;

	public ArgumentDouble(NeedType need, boolean playerOnly, Permission permission, Text usage, TabCompleteMode mode) {
		super(need, playerOnly, permission, usage);
		this.tabComplete = mode.complete().collect(Collectors.toList());
	}

	// ----- do
	@Override
	public Double consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			Double nb = NumberUtils.doubleOrNull(call.getArguments().get(i).toLowerCase());
			if (nb != null) {
				call.getArguments().remove(i);
				return nb;
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return tabComplete;
	}

}
