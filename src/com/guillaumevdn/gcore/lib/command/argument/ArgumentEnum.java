package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentEnum<E extends Enum<E>> extends Argument<E> {

	private Class<E> enumClass;
	private List<String> tabComplete;

	public ArgumentEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> enumClass) {
		super(need, playerOnly, permission, usage);
		this.enumClass = enumClass;
		this.tabComplete = Serializer.ofEnum(enumClass).serialize(CollectionUtils.asList(enumClass.getEnumConstants()));
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

	// do
	@Override
	public E consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		E value = null;
		main: for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i).toLowerCase();
			// exact
			value = ObjectUtils.safeValueOf(call.getArguments().get(i), enumClass);
			if (value != null) {
				call.getArguments().remove(i);
				break main;
			}
			// match something
			for (E e : enumClass.getEnumConstants()) {
				if (e.name().toLowerCase().startsWith(arg)) {
					value = e;
					call.getArguments().remove(i);
					break main;
				}
			}
		}
		return value;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return tabComplete;
	}
}
