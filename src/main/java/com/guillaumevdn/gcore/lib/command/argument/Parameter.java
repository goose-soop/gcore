package com.guillaumevdn.gcore.lib.command.argument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.UsageRestriction;
import com.guillaumevdn.gcore.lib.permission.Permission;

/**
 * @author GuillaumeVDN
 */
public final class Parameter extends UsageRestriction implements Arg {

	private List<String> aliases;
	private List<String> tabComplete;

	public Parameter(boolean playerOnly, Permission permission, String... aliases) {
		super(playerOnly, permission);
		this.aliases = CollectionUtils.asUnmodifiableLowercaseList(aliases);
		this.tabComplete = Collections.unmodifiableList(Stream.of(aliases).map(param -> "-" + param).collect(Collectors.toList()));
	}

	// ----- get
	public List<String> getAliases() {
		return aliases;
	}

	public List<String> getTabComplete() {
		return tabComplete;
	}

	@Override
	public String getName() {
		return aliases.get(0);
	}

	@Override
	public boolean has(CommandCall call) {
		return CollectionUtils.containsOne(call.getParameters(), aliases);
	}

}
