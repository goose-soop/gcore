package com.guillaumevdn.gcore.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.PermissionGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class GcoreExport extends Subcommand {

	public GcoreExport() {
		super(false, PermissionGCore.inst().gcoreAdmin, TextGCore.commandDescriptionGcoreExport, ConfigGCore.commandsAliasesExport);
	}

	private static final DateTimeFormatter LOCALDATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'-'HH'-'mm'-'ss");

	@Override
	public void perform(CommandCall call) {
		File temp = new File(GCore.inst().getDataFolder().getParentFile() + "/GCore_temp");
		FileUtils.delete(temp);
		temp.mkdirs();
		// copy plugins
		for (GPlugin plugin : PluginUtils.getGPlugins()) {
			File f = new File(temp + "/" + plugin.getName());
			try {
				FileUtils.copy(plugin.getDataFolder(), f);
				if (plugin.equals(GCore.inst())) {
					File configFile = new File(temp + "/" + plugin.getName() + "/config.yml");
					YMLConfiguration config = !configFile.exists() ? null : new YMLConfiguration(plugin, configFile);
					if (config != null && config.contains("mysql")) {
						config.write("mysql.host", "/");
						config.write("mysql.name", "/");
						config.write("mysql.user", "/");
						config.write("mysql.pass", "/");
						config.save();
					}
				}
			} catch (IOException exception) {
				exception.printStackTrace();
				TextGCore.messageGcoreExportCouldnt.replace("{file}", () -> f).send(call.getSender());
			}
		}
		// copy latest logs
		File logRoot = new File(Bukkit.getWorldContainer() + "/logs");
		int logCount = 0;
		File[] arr = logRoot.listFiles();
		if (arr != null) {  // happens
			for (File logFile : Stream.of(arr).sorted((a, b) -> -a.compareTo(b)).collect(Collectors.toList())) {
				if (++logCount > 3) break;
				File f = new File(temp + "/log_" + logFile.getName());
				try {
					FileUtils.copy(logFile, f);
				} catch (IOException exception) {
					exception.printStackTrace();
					TextGCore.messageGcoreExportCouldnt.replace("{file}", () -> f).send(call.getSender());
				}
			}
		}
		// create server info file
		String implementation = "unknown";
		try { Class.forName("com.destroystokyo.paper.PaperConfig"); implementation = "paper"; } catch (Throwable ignored) {}
		try { Class.forName("org.spigotmc.SpigotConfig"); implementation = "spigot"; } catch (Throwable ignored) {}
		List<GPlugin> gplugins = PluginUtils.getGPlugins();
		List<Plugin> plugins = CollectionUtils.asList(Bukkit.getPluginManager().getPlugins());
		plugins.removeAll(gplugins);
		File f = new File(temp + "/info.txt");
		try {
			f.createNewFile();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
				writer.write("Server version : " + Bukkit.getVersion() + " / " + Bukkit.getBukkitVersion() + "\n");
				writer.write("Detected : " + Version.CURRENT + " (" + implementation + ")\n");
				writer.write("\n----- " + StringUtils.pluralizeAmountDesc("GPlugin", gplugins.size()) + " -----\n");
				for (GPlugin plugin : gplugins) {
					writer.write("- " + plugin.getName() + (!plugin.isEnabled() ? " (disabled)" : "") + " v" + plugin.getDescription().getVersion() + "\n");
				}
				writer.write("\n----- " + StringUtils.pluralizeAmountDesc("plugin", plugins.size()) + " -----\n");
				for (Plugin plugin : plugins) {
					writer.write("- " + plugin.getName() + (!plugin.isEnabled() ? " (disabled)" : "") + " v" + plugin.getDescription().getVersion() + "\n");
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			TextGCore.messageGcoreExportCouldnt.replace("{file}", () -> f).send(call.getSender());
		}
		// zip
		File target = new File(GCore.inst().getDataFolder().getParentFile() + "/GCore_" + LOCALDATETIME_FORMAT.format(ConfigGCore.timeNow()) + ".zip");
		FileUtils.zip(temp, target);
		FileUtils.delete(temp);
		// done
		TextGCore.messageGcoreExportFile.replace("{plugins}", () -> StringUtils.toTextString(", ", gplugins)).replace("{file}", () -> target).send(call.getSender());
	}

}
