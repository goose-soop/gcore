package com.guillaumevdn.gcore.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GPerm;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Messenger.Level;
import com.guillaumevdn.gcore.lib.util.Utils;

public class CommandItemClickinfo extends CommandArgument {

	public static Set<Player> enabled = new HashSet<Player>();

	public CommandItemClickinfo() {
		super(GCore.inst(), Utils.asList("clickinfo"), "toggle click info (in GUIs) debug for items", GPerm.GCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		Player player = call.getSenderAsPlayer();
		if (enabled.add(player)) {
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "Enabled item click info in GUIs for " + player.getName());
		} else if (enabled.remove(player)) {
			Messenger.send(player, Level.NORMAL_INFO, "GCore", "Disabled item click info in GUIs for " + player.getName());
		}
	}

}
