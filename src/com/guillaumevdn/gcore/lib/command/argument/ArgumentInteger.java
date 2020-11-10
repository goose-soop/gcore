package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentInteger extends Argument<Integer> {

	private List<String> tabComplete;

	public ArgumentInteger(NeedType need, boolean playerOnly, Permission permission, Text usage, TabCompleteMode mode) {
		super(need, playerOnly, permission, usage);
		this.tabComplete = mode.complete().collect(Collectors.toList());
	}

	// do
	@Override
	public Integer consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			Integer nb = NumberUtils.integerOrNull(call.getArguments().get(i).toLowerCase());
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

	// enum
	public static enum TabCompleteMode {

		_1_2_5_10(CollectionUtils.asList("1", "2", "5", "10"))
		;

		private List<String> list;

		TabCompleteMode(List<String> list) {
			this.list = list;
		}

		public Stream<String> complete() {
			return list.stream();
		}

	}

}
