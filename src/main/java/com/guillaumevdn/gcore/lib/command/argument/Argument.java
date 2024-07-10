package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.UsageRestriction;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class Argument<T> extends UsageRestriction implements Arg {

	private final NeedType need;
	private final Text usage;

	public Argument(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(playerOnly, permission);
		this.need = need;
		this.usage = usage;
	}

	// ----- get
	public final NeedType getNeed() {
		return need;
	}

	public final Text getUsage() {
		return usage;
	}

	@Override
	public String getName() {
		return usage.parseLine();
	}

	public final T get(CommandCall call) {
		return call.getArgumentValue(call.getSubcommand().getArguments().indexOf(this));
	}

	@Override
	public boolean has(CommandCall call) {
		return get(call) != null;
	}

	// ----- do
	public abstract T consume(CommandCall call);

	public abstract List<String> tabComplete(CommandCall call);

}
