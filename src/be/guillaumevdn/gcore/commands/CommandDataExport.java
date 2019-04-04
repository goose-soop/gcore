package be.guillaumevdn.gcore.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import be.guillaumevdn.gcore.GCore;
import be.guillaumevdn.gcore.GPerm;
import be.guillaumevdn.gcore.lib.GPlugin;
import be.guillaumevdn.gcore.lib.command.CommandArgument;
import be.guillaumevdn.gcore.lib.command.CommandCall;
import be.guillaumevdn.gcore.lib.messenger.Messenger;
import be.guillaumevdn.gcore.lib.util.Utils;
import be.guillaumevdn.gcore.libs.org.apache.commons.io.FileUtils;

public class CommandDataExport extends CommandArgument {

	public CommandDataExport() {
		super(GCore.inst(), Utils.asList("export"), "generate a debug file (to send to the developper)", GPerm.GCORE_ADMIN, false);
	}

	@Override
	public void perform(CommandCall call) {
		// temp root
		File tempRoot = new File(GCore.inst().getDataFolder() + "/data/temp/");
		if (tempRoot.exists()) {
			tempRoot.delete();
		}
		tempRoot.mkdirs();
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin instanceof GPlugin) {
				try {
					FileUtils.copyDirectory(plugin.getDataFolder(), new File(tempRoot + "/" + plugin.getDataFolder().getName() + "/"));
				} catch (Throwable exception) {
					exception.printStackTrace();
					GCore.inst().error("Couldn't copy data folder of plugin " + plugin.getName());
				}
			}
		}
		// debug root
		File debugRoot = new File(tempRoot + "/debug");
		if (debugRoot.exists()) {
			debugRoot.delete();
		}
		debugRoot.mkdirs();
		// copy latest logs
		File logRoot = new File(new File(".").getAbsolutePath() + "/logs");
		int logCount = 0;
		for (File logFile : Utils.asReverseList(Utils.asSortedList(Utils.asList(logRoot.listFiles())))) {
			if (++logCount > 3) break;
			try {
				File newLatestLog = new File(debugRoot + File.separator + logFile.getName());
				newLatestLog.createNewFile();
				FileUtils.copyFile(logFile, newLatestLog);
			} catch (IOException exception) {
				exception.printStackTrace();
				Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, "GCore", "Couldn't move latest log file to the debug folder.");
			}
		}
		// create server info file
		try {
			File versions = new File(debugRoot + File.separator + "serverinfo.txt");
			versions.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(versions));
			writer.write("----- SERVER -----\n");
			writer.write("Server version : " + Bukkit.getVersion() + "\n");
			writer.write("Bukkit version : " + Bukkit.getBukkitVersion() + "\n\n");
			writer.write("----- PLUGINS -----\n");
			writer.write("Plugins count : " + Bukkit.getPluginManager().getPlugins().length + "\n");
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				writer.write("- " + plugin.getName() + (!plugin.isEnabled() ? " (disabled)" : "") + " : " + plugin.getDescription().getVersion() + "\n");
			}
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
			Messenger.send(call.getSender(), Messenger.Level.SEVERE_ERROR, "GCore", "Couldn't create server info file.");
		}
		// zip
		File target = new File(new File(".").getAbsolutePath() + File.separator + "plugins" + File.separator + "GCore_" + new SimpleDateFormat("YYYY'-'MM'-'dd'_'HH'-'mm").format(Calendar.getInstance().getTime()) + ".zip");
		Utils.zipFile(tempRoot, target);
		// delete temp root
		if (!tempRoot.delete()) {
			tempRoot.deleteOnExit();
		}
		// message
		Messenger.send(call.getSender(), Messenger.Level.NORMAL_SUCCESS, "GCore", "Debug file exported to <" + target.getPath() + ">. Send this zip file to the developper !");
	}

}
