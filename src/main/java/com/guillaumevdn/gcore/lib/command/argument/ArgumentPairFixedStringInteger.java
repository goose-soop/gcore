package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairFixedStringInteger extends Argument<Integer> {

	private List<String> fixedStrings;

	public ArgumentPairFixedStringInteger(NeedType need, boolean playerOnly, Permission permission, Text usage, List<String> fixedStrings) {
		super(need, playerOnly, permission, usage);
		this.fixedStrings = fixedStrings;
	}

	public List<String> getFixedStrings() {
		return fixedStrings;
	}

	// ----- do
	@Override
	public Integer consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size()) break;
			String string = call.getArguments().get(i);
			if (fixedStrings.contains(string.toLowerCase())) {
				Integer nb = NumberUtils.integerOrNull(call.getArguments().get(i + 1));
				if (nb != null) {
					call.getArguments().remove(i);
					call.getArguments().remove(i);
					return nb;
				}
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return getUsage().getCurrentLines();
	}

}
