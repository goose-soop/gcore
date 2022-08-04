package com.guillaumevdn.gcore.lib.command.argument;

import java.time.LocalDate;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentLocalDate extends Argument<LocalDate> {

	private List<String> tabComplete;

	public ArgumentLocalDate(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
		this.tabComplete = CollectionUtils.asList(LocalDate.now().toString());
	}

	// ----- do
	@Override
	public LocalDate consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			try {
				LocalDate date = LocalDate.parse(call.getArguments().get(i));
				if (date != null) {
					call.getArguments().remove(i);
					return date;
				}
			} catch (Throwable ignored) {}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return tabComplete;
	}

}
