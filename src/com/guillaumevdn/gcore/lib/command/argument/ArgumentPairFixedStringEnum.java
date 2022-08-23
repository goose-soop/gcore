package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairFixedStringEnum<E extends Enum<E>> extends Argument<E> {

	private List<String> fixedStrings;
	private Class<E> enumClass;

	public ArgumentPairFixedStringEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, List<String> fixedStrings, Class<E> enumClass) {
		super(need, playerOnly, permission, usage);
		this.fixedStrings = fixedStrings;
		this.enumClass = enumClass;
	}

	public List<String> getFixedStrings() {
		return fixedStrings;
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

	@Override
	public E consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size()) break;
			String string = call.getArguments().get(i);
			if (fixedStrings.contains(string.toLowerCase())) {
				E e = ObjectUtils.safeValueOf(call.getArguments().get(i + 1), enumClass);
				if (e != null) {
					call.getArguments().remove(i);
					call.getArguments().remove(i);
					return e;
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
