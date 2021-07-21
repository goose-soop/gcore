package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentLegacyNPC extends Argument<ElementNPC> {

	public ArgumentLegacyNPC(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	// ----- do
	@Override
	public ElementNPC consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		ElementNPC npc = null;
		main: for (int i = 0; i < call.getArguments().size(); ++i) {
			Integer arg = NumberUtils.integerOrNull(call.getArguments().get(i).toLowerCase());
			npc = arg == null ? null : NPCManager.inst().getNPCsConfig().get(arg);
			if (npc != null) {
				call.getArguments().remove(i);
				break main;
			}
		}
		return npc;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return NPCManager.inst().getNPCsConfig().values().stream().map(ElementNPC::getId).sorted(String::compareTo).collect(Collectors.toList());
	}

}
