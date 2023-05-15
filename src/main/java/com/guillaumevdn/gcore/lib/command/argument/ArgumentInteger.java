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

	// ----- do
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

	// ----- enum
	public static enum TabCompleteMode {

		_1_2(CollectionUtils.asList("1", "2")),
		_1_2_3_4_5_6_7_8_9_10(CollectionUtils.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")),
		_1_2_5_10(CollectionUtils.asList("1", "2", "5", "10")),
		_10_15_30_60_90_120(CollectionUtils.asList("10", "15", "30", "60", "90", "120")),
		_10_100_1000_10000_100000(CollectionUtils.asList("10", "100", "1000", "1000", "10000", "100000")),
		DECIMALS(CollectionUtils.asList("0.5", "1", "1.5", "2", "2.5")),
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
