package com.guillaumevdn.gcore.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.file.FileUtils;

/**
 * @author GuillaumeVDN
 */
public final class GcoreItemReadChat extends Subcommand {

	public GcoreItemReadChat() {
		super(true, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreItemReadChat, ConfigGCore.commandsAliasesItemReadChat);
	}

	@Override
	public void perform(CommandCall call) {
		Player sender = call.getSenderPlayer();
		ItemStack item = sender.getItemInHand();
		if (Mat.isVoid(item)) {
			TextGCore.messageItemReadNull.send(sender);
		} else {
			YMLConfiguration config = GCore.inst().loadConfigurationFile("data_v8/tmp.yml");
			config.write("item", item);
			config.save();
			try (BufferedReader reader = new BufferedReader(new FileReader(config.getFile()))) {
				for (String line = null; (line = reader.readLine()) != null; ) {
					call.getSender().sendMessage(line);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			FileUtils.delete(config.getFile());
		}
	}

}
