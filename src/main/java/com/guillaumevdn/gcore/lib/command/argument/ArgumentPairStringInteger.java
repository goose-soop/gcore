package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairStringInteger extends Argument<Pair<String, Integer>> {

	public ArgumentPairStringInteger(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	// ----- do
	@Override
	public Pair<String, Integer> consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size()) break;
			String string = call.getArguments().get(i);
			Integer nb = NumberUtils.integerOrNull(call.getArguments().get(i + 1));
			if (nb != null) {
				call.getArguments().remove(i);
				call.getArguments().remove(i);
				return Pair.of(string, nb);
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return getUsage().getCurrentLines();
	}

}
