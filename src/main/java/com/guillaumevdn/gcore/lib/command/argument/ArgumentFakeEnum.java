package com.guillaumevdn.gcore.lib.command.argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentFakeEnum<E> extends PartialArgument<E> {

	private List<E> values;
	private Map<String, E> serializedValues = new HashMap<>();
	private List<String> tabComplete = new ArrayList<>();

	public ArgumentFakeEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> typeClass, List<E> values) {
		super(need, playerOnly, permission, usage);
		this.values = values;

		Serializer serializer = Serializer.find(typeClass);

		for (E e : values) {
			String ser = serializer.serialize(e);
			serializedValues.put(ser.toLowerCase(), e);
			tabComplete.add(ser);
		}
	}

	public List<E> getValues() {
		return values;
	}

	@Override
	protected E exactMatch(CommandCall call, String arg) {
		arg = arg.toLowerCase();
		return serializedValues.get(arg.toLowerCase());
	}

	@Override
	protected Stream<E> partialMatches(CommandCall call, String arg) {
		final String argF = arg.toLowerCase();
		return serializedValues.entrySet().stream().filter(e -> e.getKey().startsWith(argF)).map(e -> e.getValue());
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return tabComplete;
	}

}
