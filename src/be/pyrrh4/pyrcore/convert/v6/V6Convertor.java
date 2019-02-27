package be.pyrrh4.pyrcore.convert.v6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.bukkit.Location;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.libs.com.google.gson.Gson;
import be.pyrrh4.pyrcore.libs.org.apache.commons.io.FileUtils;

public class V6Convertor {

	public static final Gson V6_QUESTCREATOR_GSON = Utils.createGsonBuilder()
			.registerTypeAdapter(V6QuestCreatorFollowedObjectData.class, new AdapterV6QuestCreatorFollowedObjectData())
			.setPrettyPrinting()
			.create();

	public static final Gson PRE6_GSON = Utils.createGsonBuilder()
			.registerTypeAdapter(Location.class, new AdapterPre6Location())
			.setPrettyPrinting()
			.create();

	// base
	public V6Convertor() {
	}

	// run
	public void run() {
		// convert data
		File oldRoot = new File(new File(".").getAbsolutePath() + "/plugins/PyrCore_OLD_pre6/");
		File dataRoot = new File(new File(".").getAbsolutePath() + "/plugins/PyrCore/data/");
		dataRoot.mkdirs();

		// CUSTOM COMMANDS

		// data
		File file = new File(oldRoot + "/data/data/CustomCommandsData.json");
		if (file.exists()) {
			try {
				FileUtils.copyFile(file, new File(dataRoot + "/customcommands_board.json"));
				PyrCore.inst().success("Converted " + file);
			} catch (Throwable exception) {
				PyrCore.inst().error("Couldn't convert " + file + ", you'll have to do it manually.");
			}
		}

		// config
		file = new File(oldRoot + "/customcommands_config.yml");
		if (file.exists()) {
			try {
				File newFile = new File(new File(".").getAbsolutePath() + "/plugins/CustomCommands/config.yml");
				newFile.getParentFile().mkdirs();
				newFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
				for (String line : Utils.asList("#----------------------------------------------------------------------------------------------------\r\n" + 
						"# Configuration file for CustomCommands\r\n" + 
						"#----------------------------------------------------------------------------------------------------\r\n" + 
						"\r\n" + 
						"# Data management\r\n" + 
						"data:\r\n" + 
						"  # Back end (JSON, MYSQL) (default JSON)\r\n" + 
						"  # If you enable MySQL here, you should definitely enable it for PyrCore as well\r\n" + 
						"  backend: JSON\r\n" + 
						"  # Synchronization delay (in seconds) (disable with -1) (default 300)\r\n" + 
						"  # If this is enabled, the plugin will check the stored data (files/database), and if there's new/different data there, cached data will be overriden\r\n" + 
						"  sync_delay: -1\r\n" + 
						"  # MySQL identifiers (if backend is MYSQL)\r\n" + 
						"#  mysql:\r\n" + 
						"#    host: mysql.myserver.com\r\n" + 
						"#    name: mydatabase\r\n" + 
						"#    user: username\r\n" + 
						"#    pass: pwd\r\n" + 
						"\r\n")) {
					writer.write(line);
				}
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					writer.write(line + "\n");
				}
				reader.close();
				writer.close();
				PyrCore.inst().success("Converted " + file);
			} catch (Throwable exception) {
				PyrCore.inst().error("Couldn't convert " + file + ", you'll have to do it manually.");
			}
		}

		// POTATOES

		// data
		file = new File(oldRoot + "/data/data/PotatoesData.json");
		if (file.exists()) {
			try {
				Pre6PotatoesData data = Utils.loadFromGson(Pre6PotatoesData.class, file, false, PRE6_GSON);
				Utils.saveToGson(new V6PotatoesDiskBoard(data.getMainLobby()), new File(PyrCore.inst().getDataRootFolder() + "/potatoes_board.json"));
				for (Pre6PotatoesArena arena : data.getArenas().values()) {
					Utils.saveToGson(new V6PotatoesArena(arena), new File(PyrCore.inst().getDataRootFolder() + "/potatoes_arenas/" + arena.getName() + ".json"));
				}
				PyrCore.inst().success("Converted " + file);
			} catch (Throwable exception) {
				PyrCore.inst().error("Couldn't convert " + file + ", you'll have to do it manually.");
			}
		}

		// PYRSLOTMACHINE

		// data
		file = new File(oldRoot + "/data/data/PyrSlotMachineData.json");
		if (file.exists()) {
			try {
				Pre6PyrSlotMachineData data = Utils.loadFromGson(Pre6PyrSlotMachineData.class, file, false, PRE6_GSON);
				for (Pre6PyrSlotMachineMachine machine : data.getMachines()) {
					Utils.saveToGson(new V6PyrSlotMachineMachine(machine), new File(PyrCore.inst().getDataRootFolder() + "/pyrslotmachine_machines/" + machine.getId() + ".json"));
				}
				PyrCore.inst().success("Converted " + file);
			} catch (Throwable exception) {
				PyrCore.inst().error("Couldn't convert " + file + ", you'll have to do it manually.");
			}
		}

		// QUESTCREATOR

		// users
		file = new File(oldRoot + "/data/data/users/");
		if (file.exists() && file.isDirectory()) {
			for (File userRoot : file.listFiles()) {
				if (userRoot.isDirectory()) {
					File qcUserFile = new File(userRoot + "/QuestCreatorUser.json");
					if (qcUserFile.exists() && qcUserFile.isFile()) {
						try {
							Pre6QuestCreatorUser user = Utils.loadFromGson(Pre6QuestCreatorUser.class, qcUserFile, false, PRE6_GSON);
							File newFile = new File(PyrCore.inst().getUserDataRootFolder() + "/" + (userRoot.getName().contains("_") ? userRoot.getName() : userRoot.getName() + "_default") + "/questcreator_user.json");
							Utils.saveToGson(new V6QuestCreatorQCUser(user), newFile, V6_QUESTCREATOR_GSON);
							PyrCore.inst().success("Converted " + qcUserFile);
						} catch (Throwable exception) {
							PyrCore.inst().error("Couldn't convert " + qcUserFile + ", you'll have to do it manually.");
						}
					}
				}
			}
		}

		// statistics
		file = new File(oldRoot + "/data/data/users/");
		if (file.exists() && file.isDirectory()) {
			V6PyrCoreStatistics newStatistics = new V6PyrCoreStatistics();
			for (File userRoot : file.listFiles()) {
				if (userRoot.isDirectory()) {
					File statsFile = new File(userRoot + "/Statistics.json");
					if (statsFile.exists() && statsFile.isFile()) {
						try {
							Pre6PyrCoreStatistics statistics = Utils.loadFromGson(Pre6PyrCoreStatistics.class, statsFile, false, PRE6_GSON);
							String pcUser = userRoot.getName().contains("_") ? userRoot.getName() : userRoot.getName() + "_default";
							for (String stat : statistics.getStats().keySet()) {
								newStatistics.set(pcUser, stat, statistics.getStats().get(stat));
							}
							PyrCore.inst().success("Converted " + statsFile);
						} catch (Throwable exception) {
							PyrCore.inst().error("Couldn't convert " + statsFile + ", you'll have to do it manually.");
						}
					}
				}
			}
			Utils.saveToGson(newStatistics, new File(PyrCore.inst().getUserDataRootFolder() + "/statistics.json"));
		}
	}

}
