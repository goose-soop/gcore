package com.guillaumevdn.gcore.lib.command.argument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
public class ArgumentEnum<E extends Enum<E>> extends PartialArgument<E> {

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

	// ----- do
	@Override
	protected E exactMatch(CommandCall call, String arg) {
		return ObjectUtils.safeValueOf(arg, enumClass);
	}

	@Override
	protected Stream<E> partialMatches(CommandCall call, String arg) {
		return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().toLowerCase().startsWith(arg));
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return tabComplete;
	}

}
