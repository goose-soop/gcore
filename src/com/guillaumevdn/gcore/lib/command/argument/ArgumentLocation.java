package com.guillaumevdn.gcore.lib.command.argument;

import java.util.List;

import org.bukkit.Location;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ArgumentLocation extends Argument<Location> {

	public ArgumentLocation(NeedType need, boolean playerOnly, Permission permission, Text usage) {
		super(need, playerOnly, permission, usage);
	}

	@Override
	public Location consume(CommandCall call) {
		if (call.getArguments().isEmpty()) {
			return null;
		}
		for (int i = 0; i < call.getArguments().size(); ++i) {
			final Location loc = Serializer.LOCATION.deserialize(call.getArguments().get(i).toLowerCase());
			if (loc != null) {
				call.getArguments().remove(i);
				return loc;
			}
		}
		return null;
	}

	@Override
	public List<String> tabComplete(CommandCall call) {
		return CollectionUtils.asList("world,0,0,0");
	}

}
