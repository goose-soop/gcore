package com.guillaumevdn.gcore.lib.command.argument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentDynamicFakeEnum<E> extends PartialArgument<E> {

	private Supplier<List<E>> valuesSupplier;
	private Serializer<E> serializer;

	private List<E> values;
	private Map<String, E> serializedValues;
	private List<String> tabComplete;

	public ArgumentDynamicFakeEnum(NeedType need, boolean playerOnly, Permission permission, Text usage, Class<E> typeClass, Supplier<List<E>> valuesSupplier) {
		super(need, playerOnly, permission, usage);
		this.valuesSupplier = valuesSupplier;
		this.serializer = Serializer.find(typeClass);

		refetchValues();
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
			tabComplete.add(ser);
		}
	}

	@Override
	public E consume(CommandCall call) {
		refetchValues();
		return super.consume(call);
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
