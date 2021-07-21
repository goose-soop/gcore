package com.guillaumevdn.gcore.lib.command.argument;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairEnumString<E extends Enum<E>> extends Argument<Pair<E, String>> {

	private Class<E> enumClass;

	public ArgumentPairEnumString(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> enumClass) {
		super(need, playerOnly, permission, usage);
		this.enumClass = enumClass;
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

	// ----- do
	@Override
	public Pair<E, String> consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size()) break;
			E e = ObjectUtils.safeValueOf(call.getArguments().get(i), enumClass);
			if (e != null) {
				String value = call.getArguments().get(i + 1);
				call.getArguments().remove(i);
				call.getArguments().remove(i);
				return Pair.of(e, value);
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		List<String> result = new ArrayList<>();
		for (E e : enumClass.getEnumConstants()) {
			result.add(e.name().toLowerCase() + " 1");
		}
		return result;
	}

}
