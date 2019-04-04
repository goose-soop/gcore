package com.guillaumevdn.gcore.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.DataSingleton;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.util.Utils;

public class StatisticsBoard extends DataSingleton {

	// base
	private Map<String, Map<String, Integer>> stats = new HashMap<String, Map<String, Integer>>();

	public StatisticsBoard() {
	}

	public Set<String> getAll() {
		return Collections.unmodifiableSet(stats.keySet());
	}

	public Map<String, Integer> getAll(String stat) {
		return Collections.unmodifiableMap(stats.containsKey(stat) ? stats.get(stat) : new HashMap<String, Integer>());
	}

	public void clearAll() {
		stats.clear();
		deleteAsync();
	}

	public int get(String stat, UUID player) {
		return get(stat, player, getDataManager().getDataProfiles().get(player));
	}

	public int get(String stat, UUID player, String profile) {
		if (stats.containsKey(stat)) {
			Map<String, Integer> values = stats.get(stat);
			String key = player.toString() + "_" + profile;
			if (values.containsKey(key)) {
				return values.get(key);
			}
		}
		return 0;
	}

	public void add(final String stat, final UUID player, int delta) {
		set(stat, player, get(stat, player) + delta);
	}

	public void add(final String stat, final UUID player, String profile, int delta) {
		set(stat, player, profile, get(stat, player) + delta);
	}

	public void set(final String stat, final UUID player, int value) {
		set(stat, player, getDataManager().getDataProfiles().get(player), value);
	}

	public void set(final String stat, final UUID player, String profile, int value) {
		if (!stats.containsKey(stat)) {
			stats.put(stat, new HashMap<String, Integer>());
		}
		Map<String, Integer> values = stats.get(stat);
		String key = player.toString() + "_" + profile;
		values.put(key, value);
		// save
		pushAsync(stat, key, value);
	}

	// data
	@Override
	public PCDataManager getDataManager() {
		return GCore.inst().getData();
	}

	private final static class JsonData {
		private final Map<String, Map<String, Integer>> stats;
		public JsonData(Map<String, Map<String, Integer>> stats) {
			this.stats = stats;
		}
	}

	@Override
	public final File getJsonFile() {
		return new File(GCore.inst().getUserDataRootFolder() + "/statistics.json");
	}

	@Override
	public final void jsonPull() {
		File file = getJsonFile();
		if (file.exists()) {
			JsonData data = Utils.loadFromGson(JsonData.class, file, true);
			this.stats.putAll(data.stats);
		}
	}

	@Override
	public final void jsonPush() {
		Utils.saveToGson(new JsonData(stats), getJsonFile());
	}

	@Override
	public final void jsonDelete() {
		File file = getJsonFile();
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public final String getMySQLTable() {
		return "gcore_statistics";
	}

	@Override
	public final Query getMySQLInitQuery() {
		return new Query("CREATE TABLE IF NOT EXISTS `" + getMySQLTable() + "`(" +
				"`key` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
				"`id` VARCHAR(50) NOT NULL," +
				"`userid` VARCHAR(100) NOT NULL," +
				"`value` INT NOT NULL," +
				"PRIMARY KEY(`key`)" +
				") ENGINE=InnoDB DEFAULT CHARSET=?;", "utf8");
	}

	@Override
	public final void mysqlPull() throws SQLException {
		ResultSet set = getDataManager().performMySQLGetQuery(new Query("SELECT * FROM `" + getMySQLTable() + "`;"));
		if (set != null) {
			while (set.next()) {
				String id = set.getString("id");
				String userId = set.getString("userid");
				int value = set.getInt("value");
				if (!stats.containsKey(id)) {
					stats.put(id, new HashMap<String, Integer>());
				}
				if (!stats.containsKey(userId) || stats.get(userId) == null) {
					stats.put(userId, new HashMap<String, Integer>());
				}
				Map<String, Integer> values = stats.get(userId);
				values.put(userId, value);
			}
		}
	}

	@Override
	public final void mysqlPush(Object... params) {
		// decode
		String id = (String) params[0];
		String userId = (String) params[1];
		int value = (int) params[2];
		// push
		getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "` WHERE `id`=? AND `userid`=?;", id, userId));
		if (value != 0) getDataManager().performMySQLUpdateQuery(new Query("INSERT INTO `" + getMySQLTable() + "`(`id`,`userid`,`value`) VALUES(?,?," + value + ");", id, userId));
	}

	@Override
	public void mysqlDelete(Object... params) {
		if (params != null && params.length > 0) {
			String id = (String) params[0];
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "` WHERE `id`=?;", id));
		} else {
			getDataManager().performMySQLUpdateQuery(new Query("DELETE FROM `" + getMySQLTable() + "`;"));
		}
	}

}
