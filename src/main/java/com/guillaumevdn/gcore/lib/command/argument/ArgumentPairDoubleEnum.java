package com.guillaumevdn.gcore.lib.command.argument;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairDoubleEnum<E extends Enum<E>> extends Argument<Pair<Double, E>> {

	private Class<E> enumClass;

	public ArgumentPairDoubleEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> enumClass) {
		super(need, playerOnly, permission, usage);
		this.enumClass = enumClass;
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

	// ----- do
	@Override
	public Pair<Double, E> consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size())
				break;
			Double nb = NumberUtils.doubleOrNull(call.getArguments().get(i + 1));
			if (nb != null) {
				E e = ObjectUtils.safeValueOf(call.getArguments().get(i), enumClass);
				if (e != null) {
					call.getArguments().remove(i);
					call.getArguments().remove(i);
					return Pair.of(nb, e);
				}
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		List<String> result = new ArrayList<>();
		for (E e : enumClass.getEnumConstants()) {
			result.add("1 " + e.name().toLowerCase());
		}
		return result;
	}

}
