package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.element.struct.list.referenceable.ElementsContainer;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class SuperElementArgument<E extends SuperElement, C extends ElementsContainer<E>> extends PartialArgument<E> {

	private C container;

	public SuperElementArgument(C container, NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
		this.container = container;
	}

	// ----- do
	@Override
	protected E exactMatch(CommandCall call, String arg) {
		return container.getElement(arg).orNull();
	}

	@Override
	protected Stream<E> partialMatches(CommandCall call, String arg) {
		return container.values().stream().filter(gui -> gui.getId().startsWith(arg));
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return container.values().stream().map(SuperElement::getId).sorted(String::compareTo).collect(Collectors.toList());
	}

}
