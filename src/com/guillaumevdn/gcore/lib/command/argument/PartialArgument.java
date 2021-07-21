package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class PartialArgument<T> extends Argument<T> {

	public PartialArgument(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	// ----- do
	@Override
	public final T consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			String arg = call.getArguments().get(i).toLowerCase();
			// exact
			T value = exactMatch(call, arg);
			if (value == null) {
				List<T> matches = partialMatches(call, arg).collect(Collectors.toList());
				if (matches.size() == 1) {
					value = matches.get(0);
				}
			}
			if (value != null) {
				call.getArguments().remove(i);
				return value;
			}
		}
		return null;
	}

	@Nullable
	protected abstract T exactMatch(CommandCall call, String arg);

	@Nonnull
	protected abstract Stream<T> partialMatches(CommandCall call, String arg);

}
