package be.pyrrh4.pyrcore.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataManager;
import be.pyrrh4.pyrcore.lib.data.DataSingleton;
import be.pyrrh4.pyrcore.lib.data.mysql.Query;
import be.pyrrh4.pyrcore.lib.event.UserDataProfileChangedEvent;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class DataProfiles extends DataSingleton {

	// base
	private Map<UUID, String> profiles = new HashMap<UUID, String>();

	public DataProfiles() {
	}

	public Map<UUID, String> getAll() {
		return Collections.unmodifiableMap(profiles);
	}

	public void clearAll() {
		profiles.clear();
		deleteAsync();
	}

	public String get(UUID player) {
		return profiles.containsKey(player) ? profiles.get(player) : "default";
	}

	public void set(UUID player, String profile) {
		// event
		final String previousProfile = get(player);
		if (previousProfile.equals(profile)) return;
		// change
		profiles.put(player, profile);
		// save
		pushAsync(player.toString(), profile);
		// event
		Bukkit.getPluginManager().callEvent(new UserDataProfileChangedEvent(new PCUser(player, profile), previousProfile));
	}

	// data
	@Override
	public final DataManager getDataManager() {
		return PyrCore.inst().getData();
	}

	private final static class JsonData {
		private final Map<UUID, String> profiles;
		protected JsonData(Map<UUID, String> profiles) {
			this.profiles = profiles;
		}
	}

	@Override
	protected final File getJsonFile() {
		return new File(PyrCore.inst().getUserDataRootFolder() + "/data_profiles.json");
	}

	@Override
	protected final void jsonPull() {
		File file = getJsonFile();
		if (file.exists()) {
			JsonData data = Utils.loadFromGson(JsonData.class, file, true);
			this.profiles.putAll(data.profiles);
		}
	}

	@Override
	protected final void jsonPush() {
		Utils.saveToGson(new JsonData(profiles), getJsonFile());
	}

	@Override
	protected final void jsonDelete() {
		File file = getJsonFile();
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	protected final String getMySQLTable() {
		return "pyrcore_dataprofiles";
	}

	@Override
	protected final Query getMySQLInitQuery() {
		return new Query("CREATE TABLE IF NOT EXISTS `" + getMySQLTable() + "`(" +
				"id CHAR(36) NOT NULL," +
				"profile VARCHAR(50) NOT NULL," +
				"PRIMARY KEY(id)" +
				") ENGINE=InnoDB DEFAULT CHARSET=?;", "utf8");
	}

	@Override
	protected final void mysqlPull() throws SQLException {
		ResultSet set = getDataManager().performMySQLGetQuery(new Query("SELECT * FROM `" + getMySQLTable() + "`;"));
		if (set != null) {
			while (set.next()) {
				UUID player = UUID.fromString(set.getString("id"));
				String profile = set.getString("profile");
				profiles.put(player, profile);
			}
		}
	}

	@Override
	protected final void mysqlPush(Object... params) {
		// decode
		String playerUUID = (String) params[0];
		String profileId = (String) params[1];
		// push
		getDataManager().performMySQLUpdateQuery(new Query("REPLACE INTO `" + getMySQLTable() + "`(`id`,`profile`) VALUES(?, ?);", playerUUID, profileId));
	}

	@Override
	protected final void mysqlDelete(Object... params) {
		if (params != null && params.length > 0) {
			String playerUUID = (String) params[0];
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "` WHERE `id`=?;", playerUUID));
		} else {
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "`;"));
		}
	}

}
