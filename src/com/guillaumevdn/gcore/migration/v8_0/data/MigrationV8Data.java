package com.guillaumevdn.gcore.migration.v8_0.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.usernpcs.UserNPC;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.data.sql.Query;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;
import com.guillaumevdn.gcore.migration.v8_0.config.MigrationV8Config;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8Data extends Migration {

	public MigrationV8Data() {
		super(GCore.inst(), null, "v7 -> v8 (data)", "data_v8/migrated_v8.0.0_data.DONTREMOVE");
	}

	@Override
	public boolean mustMigrate() {
		return true;  // if not made yet
	}

	private final Gson gson = getPlugin().createGsonBuilder().create();
	private final Gson prettyGson = getPlugin().createGsonBuilder().setPrettyPrinting().create();
	private InstantMySQL mysql = null;

	@Override
	protected void doMigrate() throws Throwable {
		YMLConfiguration config = getPlugin().loadConfigurationFile("config.yml");

		// load materials
		MigrationV8Config.loadMaterialsForMigration(this);

		// load NPCs
		Map<Integer, ElementNPC> npcsConfig = new HashMap<>();

		attemptOperation("loading NPCs", BackupBehavior.NONE, () -> {
			YMLConfiguration npcsConfigFile = getPlugin().loadConfigurationFile("npcs.yml");
			for (int id : npcsConfigFile.readNumberKeysForSection("npcs", null)) {
				ElementNPC npc = new ElementNPC("" + id);
				npc.read();
				npcsConfig.put(id, npc);
			}
		});

		// mysql
		if (config.readString("data_backend.gcore_statistics_v8", "JSON").equalsIgnoreCase("MYSQL")) {
			// connect
			attemptOperation("connecting to SQLConnector", BackupBehavior.NONE, () -> {
				String host = config.readMandatoryString("mysql.host");
				String name = config.readMandatoryString("mysql.name");
				String usr = config.readMandatoryString("mysql.user");
				String pwd = config.readMandatoryString("mysql.pass");
				String customArgs = config.readString("mysql.args", "");
				String url = "jdbc:mysql://" + host + "/" + name + "?allowMultiQueries=true" + customArgs;
				mysql = new InstantMySQL(url, usr, pwd);
			});
			// create tables
			attemptOperation("creating SQLConnector tables", BackupBehavior.NONE, () -> {
				mysql.performUpdateQuery(getPlugin(), new Query("DROP TABLE IF EXISTS gcore_users_npcs_v8;"
						+ "CREATE TABLE gcore_users_npcs_v8("
						+ "user_uuid CHAR(36) NOT NULL,"
						+ "data LONGTEXT NOT NULL,"
						+ "PRIMARY KEY(user_uuid)"
						+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"));
			});
			// statistics
			attemptOperation("copying SQLConnector statistics", BackupBehavior.NONE, () -> {
				mysql.performUpdateQuery(getPlugin(), new Query("DROP TABLE IF EXISTS gcore_statistics_v8;"
						+ "CREATE TABLE gcore_statistics_v8 LIKE gcore_statistics;"
						+ "INSERT INTO gcore_statistics_v8 SELECT * FROM gcore_statistics;"));
			});
			// statistics
			attemptOperation("converting SQLConnector users NPCs board", BackupBehavior.NONE, () -> {
				ResultSet set = mysql.performGetQuery(getPlugin(), new Query("SELECT * FROM gcore_users"));
				while (set.next()) {
					UUID uuid = null;
					try {
						// read
						uuid = migrateUserInfo(set.getString("id"));
						UserNPCs user = migrateUserData(uuid, set.getString("data"), null, npcsConfig);
						// write
						mysql.performUpdateQuery(getPlugin(), new Query("INSERT INTO gcore_users_npcs_v8(user_uuid,data) VALUES (" + Query.escapeValue(uuid.toString()) + ", " + Query.escapeValue(gson.toJson(user)) + ");"));
						countMod();
					} catch (Throwable exception) {
						error("Couldn't convert saved user NPCs for " + uuid + ", skipping", exception);
					}
				}
				set.close();
			});
			// disconnect
			mysql.close();
		}
		// json
		else {
			// statistics
			attemptOperation("copying JSON statistics", BackupBehavior.NONE, () -> {
				File srcStatistics = new File(getPlugin().getDataFolder().getParentFile() + "/GCore_backup_on_v7/data/statistics.json");
				if (srcStatistics.exists()) {
					File targetRoot = getPlugin().getDataFile("data_v8/statistics/");
					try {
						V7Statistics v7 = gson.fromJson(new FileReader(srcStatistics), V7Statistics.class);
						if (v7.stats != null) {
							for (String stat : v7.stats.keySet()) {
								log("Copying statistic " + stat + "...");
								Map<UUID, Double> converted = new HashMap<>();
								v7.stats.get(stat).forEach((user, value) -> converted.put(migrateUserInfo(user), value.doubleValue()));
								if (!converted.isEmpty()) {
									toJson(converted, new File(targetRoot + "/" + stat.toLowerCase() + ".json"));
								}
							}
						}
					} catch (Throwable exception) {
						error("Couldn't convert saved statistics, skipping", exception);
					}
				}
			});
			// statistics
			attemptOperation("converting JSON users NPCs board", BackupBehavior.NONE, () -> {
				File srcUsers = new File(getPlugin().getDataFolder().getParentFile() + "/GCore_backup_on_v7/userdata");
				if (srcUsers.exists()) {
					File targetUsersNPCs = new File(getPlugin().getDataFolder() + "/data_v8/users_npcs");
					for (File userRoot : srcUsers.listFiles()) {
						File srcUserGCore = new File(userRoot + "/gcore_user.json");
						if (srcUserGCore.exists()) {
							try {
								UserNPCs user = migrateUserData(migrateUserInfo(userRoot.getName()), null, new FileReader(srcUserGCore), npcsConfig);
								toJson(user, new File(targetUsersNPCs + "/" + user.getUniqueId() + ".json"));
								countMod();
							} catch (Throwable exception) {
								error("Couldn't convert saved user NPCs " + srcUserGCore + ", skipping", exception);
							}
						}
					}
				}
			});
		}
	}

	private Gson usersGson = getPlugin().createGsonBuilder().registerTypeAdapter(V7ItemData.class, new V7ItemData.GsonAdapter()).create();
	private Set<Integer> loggedUnknownNPC = new HashSet<>();

	private UserNPCs migrateUserData(UUID uuid, String fromString, FileReader fromFile, Map<Integer, ElementNPC> npcsConfig) throws Throwable {
		V7User v7 = fromString != null ? usersGson.fromJson(fromString, V7User.class) : usersGson.fromJson(fromFile, V7User.class);
		RWHashMap<Integer, UserNPC> npcs = new RWHashMap<>(10, 1f);

		if (v7 != null /* happens somehow */ && v7.npcs != null) {
			for (int npcId : v7.npcs.keySet()) {
				// NPC don't exist anymore
				ElementNPC npcConfig = npcsConfig.get(npcId);
				if (npcConfig == null) {
					if (loggedUnknownNPC.add(npcId)) {
						error("> Unknown NPC " + npcId + ", skipping");
					}
					continue;
				}
				// convert items
				V7UserNpcData npc = v7.npcs.get(npcId);
				ItemStack heldItem = null;
				ItemStack heldItemOff = null;
				ItemStack boots = null;
				ItemStack leggings = null;
				ItemStack chestplate = null;
				ItemStack helmet = null;
				try {
					heldItem = npc.heldItem == null ? null : npc.heldItem.toItemStack();
					heldItemOff = npc.heldItemOff == null ? null : npc.heldItemOff.toItemStack();
					boots = npc.boots == null ? null : npc.boots.toItemStack();
					leggings = npc.leggings == null ? null : npc.leggings.toItemStack();
					chestplate = npc.chestplate == null ? null : npc.chestplate.toItemStack();
					helmet = npc.helmet == null ? null : npc.helmet.toItemStack();
				} catch (Throwable exception) {
					error("> Couldn't convert saved user NPCs items, skipping", exception);
				}
				// save npc if there's non-default data
				UserNPC userNPC = new UserNPC(npcId);
				userNPC.saveNonDefault(npcConfig, Replacer.GENERIC, npc.shown, npc.name, npc.skinData, npc.skinSignature, npc.location, npc.targetDistance, npc.status, heldItem, heldItemOff, boots, leggings, chestplate, helmet);
				if (!userNPC.isEmpty()) {
					npcs.put(npcId, userNPC);
				}
			}
		}

		return new UserNPCs(uuid, npcs);
	}

	private UUID migrateUserInfo(String userInfo) {
		if (userInfo == null) {  // might happen
			return null;
		}
		int index = userInfo.indexOf('_');
		return UUID.fromString(index == -1 ? userInfo : userInfo.substring(0, index));
	}

	private void toJson(Object object, File file) throws IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
		try (FileWriter writer = new FileWriter(file)) {
			prettyGson.toJson(object, writer);
		}
	}

}
