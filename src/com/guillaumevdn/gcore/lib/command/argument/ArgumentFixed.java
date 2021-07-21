package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.TextElement;

/**
 * @author GuillaumeVDN
 */
public class ArgumentFixed extends Argument<String> {

	private List<String> aliases;

	public ArgumentFixed(NeedType need, boolean playerOnly, Permission permission, String... aliases) {
		super(need, playerOnly, permission, new TextElement(CollectionUtils.asList(aliases[0])));
		this.aliases = CollectionUtils.asUnmodifiableLowercaseList(aliases);
	}

	// ----- get
	public List<String> getAliases() {
		return aliases;
	}

	// ----- do
	@Override
	public String consume(CommandCall call) {
		for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i);
			if (aliases.contains(arg.toLowerCase())) {
				call.getArguments().remove(i);
				return arg;
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return aliases;
	}

}
