package com.guillaumevdn.gcore.lib.command.argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentPairFixedStringDynamicFakeEnum<E> extends PartialArgument<E> {

	private List<String> fixedStrings;
	private Supplier<List<E>> valuesSupplier;
	private Serializer<E> serializer;

	private List<E> values;
	private Map<String, E> serializedValues;
	private List<String> tabComplete;

	public ArgumentPairFixedStringDynamicFakeEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, List<String> fixedStrings, Class<E> typeClass, Supplier<List<E>> valuesSupplier) {
		super(need, playerOnly, permission, usage);
		this.fixedStrings = fixedStrings;
		this.valuesSupplier = valuesSupplier;
		this.serializer = Serializer.find(typeClass);

		refetchValues();
	}

	public List<String> getFixedStrings() {
		return fixedStrings;
	}

	public List<E> getValues() {
		return values;
	}

	private void refetchValues() {
		this.values = valuesSupplier.get();
		this.serializedValues = new HashMap<>();
		this.tabComplete = new ArrayList<>();

		for (E e : values) {
			String ser = serializer.serialize(e);
			serializedValues.put(ser.toLowerCase(), e);
			tabComplete.add(fixedStrings.get(0) + " " + ser);
		}
	}

	@Override
	public E consume(CommandCall call) {
		if (call.getArguments().size() < 2) {
			return null;
		}
		refetchValues();

		for (int i = 0; i < call.getArguments().size(); ++i) {
			if (i + 1 >= call.getArguments().size()) break;
			String string = call.getArguments().get(i);
			if (fixedStrings.contains(string.toLowerCase())) {
				E value = exactMatch(call, call.getArguments().get(i + 1));
				if (value == null) {
					List<E> matches = partialMatches(call, call.getArguments().get(i + 1)).collect(Collectors.toList());
					if (matches.size() == 1) {
						value = matches.get(0);
					}
				}
				if (value != null) {
					call.getArguments().remove(i);
					call.getArguments().remove(i);
					return value;
				}
			}
		}
		return null;
	}

	@Override
	protected E exactMatch(CommandCall call, String arg) {
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
