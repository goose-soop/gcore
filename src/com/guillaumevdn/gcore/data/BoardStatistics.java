package com.guillaumevdn.gcore.data;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.data.Query;
import com.guillaumevdn.gcore.lib.data.board.keyed.BiKeyReference;
import com.guillaumevdn.gcore.lib.data.board.keyed.BiKeyedBoardRemote;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyReference;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.statistic.Statistic;

/**
 * @author GuillaumeVDN
 */
public class BoardStatistics extends BiKeyedBoardRemote<Statistic, UUID, Double> {

	private static BoardStatistics instance = null;
	public static BoardStatistics inst() { return instance; }

	public BoardStatistics() {
		super(GCore.inst(), "gcore_statistics_v8", 20 * 60);
		instance = this;
	}

	// get
	public void alterValue(Statistic key, UUID key2, double delta, Runnable onPush, boolean forceFetch, boolean mustCache) {
		fetchValue(key, key2, value -> {
			putValue(key, key2, value + delta, onPush, mustCache);
		}, () -> 0d, forceFetch, mustCache);
	}

	// ----------------------------------------------------------------------------------------------------
	// data
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected Map<UUID, Double> valueFromJson(FileReader reader) {
		Map map = getPlugin().getPrettyGson().fromJson(reader, Map.class);
		Map<UUID, Double> fixed = new HashMap<>();
		map.forEach((key, value) -> fixed.put(UUID.fromString(key.toString()), Double.parseDouble(value.toString())));
		return fixed;
	}

	// ----------------------------------------------------------------------------------------------------
	// json
	// ----------------------------------------------------------------------------------------------------

	// file
	public File getRoot() {
		return GCore.inst().getDataFile("data_v8/statistics/");
	}

	public File getFile(Statistic key) {
		return new File(getRoot(), key.getId().toLowerCase() + ".json");
	}

	public Statistic getKey(File file) {
		return Statistic.safeValueOf(FileUtils.getSimpleName(file));
	}

	// ----------------------------------------------------------------------------------------------------
	// mysql
	// ----------------------------------------------------------------------------------------------------

	// init
	private final String TABLE_NAME = getId();

	@Override
	protected void remoteInitMySQL() throws Throwable {
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), new Query(""
				+ "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "`("
				+ "`key` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
				+ "`statistic` VARCHAR(100) NOT NULL,"
				+ "`user_uuid` CHAR(36) NOT NULL,"
				+ "`value` DECIMAL(30,3) NOT NULL,"
				+ "PRIMARY KEY(`key`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=?;"
				, "utf8"
				));
	}

	// pull
	@Override
	protected void remotePullAllMySQL() throws Throwable {
		remotePullElementsMySQL(new Query("SELECT * FROM `" + TABLE_NAME + "`;"), () -> cache.clear());
	}

	@Override
	protected void remotePullElementsMySQL(Set<BiKeyReference<Statistic, UUID>> references) throws Throwable {
		if (references.isEmpty()) return;
		Query query = new Query("SELECT * FROM `" + TABLE_NAME + "` ");
		int i = -1;
		for (BiKeyReference<Statistic, UUID> ref : references) {
			query.add((++i == 0 ? "WHERE" : "OR") + " (`statistic`=? AND `user_uuid`=?)", ref.getKey(), ref.getKey2());
		}
		query.add(";");
		remotePullElementsMySQL(query, () -> references.forEach(ref -> deleteCacheElement(ref.getKey(), ref.getKey2())));
	}

	@Override
	protected void remotePullKeysMySQL(Set<KeyReference<Statistic>> references) throws Throwable {
		if (references.isEmpty()) return;
		Query query = new Query("SELECT * FROM `" + TABLE_NAME + "` ");
		int i = -1;
		for (KeyReference<Statistic> ref : references) {
			query.add((++i == 0 ? "WHERE" : "OR") + " (`statistic`=?)", ref.getKey());
		}
		query.add(";");
		remotePullElementsMySQL(query, () -> references.forEach(ref -> cache.remove(ref.getKey())));
	}

	private void remotePullElementsMySQL(Query query, Runnable beforeSetProcessing) throws Throwable {
		GCore.inst().getMySQLConnector().performGetQuery(getPlugin(), query,
				set -> {
					if (beforeSetProcessing != null) {
						beforeSetProcessing.run();
					}
					Set<String> skippedStats = new HashSet<>();
					while (set.next()) {
						String id = set.getString("statistic");
						Statistic stat = Statistic.safeValueOf(id);
						if (stat == null) {
							if (skippedStats.add(id)) {
								getLogger().warning("Found unknown statistic '" + id + "' in database, skipped it");
							}
							continue;
						}
						String rawUUID = set.getString("user_uuid");
						UUID uuid = ObjectUtils.uuidOrNull(rawUUID);
						if (uuid == null) {
							getLogger().warning("Found invalid user UUID '" + rawUUID + "' in database, skipped it");
							continue;
						}
						double value = set.getDouble("value");
						cache.computeIfAbsent(stat, __ -> new HashMap<>()).put(uuid, value);
					}
				});
	}

	// push
	@Override
	protected void remotePushElementsMySQL(Set<BiKeyReference<Statistic, UUID>> refs) throws Throwable {
		if (refs.isEmpty()) return;  // let's avoid deleting the whole table just because there's no WHERE clause
		for (List<BiKeyReference<Statistic, UUID>> references : CollectionUtils.split(refs, 999)) {  // multiple VALUES are limited to 1000 elements ; https://stackoverflow.com/questions/452859/inserting-multiple-rows-in-a-single-sql-query#comment22032805_452934
			Query query = buildRemoteDeleteElementsMySQLQuery(references);
			query.add("INSERT INTO `" + TABLE_NAME + "`(`statistic`,`user_uuid`,`value`) VALUES ");
			int i = -1;
			for (BiKeyReference<Statistic, UUID> reference : references) {
				String comma = (++i + 1 < references.size() ? "," : "");
				query.add("(?,?,?)" + comma, reference.getKey(), reference.getKey2(), getCachedValue(reference.getKey(), reference.getKey2()));
			}
			query.add(";");
			GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), query);
		}
	}

	// delete
	@Override
	protected void remoteDeleteElementsMySQL(Set<BiKeyReference<Statistic, UUID>> references) throws Throwable {
		if (references.isEmpty()) return; // let's avoid deleting the whole table just because there's no WHERE clause
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), buildRemoteDeleteElementsMySQLQuery(references));
	}

	private Query buildRemoteDeleteElementsMySQLQuery(Collection<BiKeyReference<Statistic, UUID>> references) {
		if (references.isEmpty()) return null; // let's avoid deleting the whole table just because there's no WHERE clause ; this shouldn't happen since this method is private but better be juuust a little more sure
		Query query = new Query("DELETE FROM `" + TABLE_NAME + "` ");
		int i = -1;
		for (BiKeyReference<Statistic, UUID> reference : references) {
			query.add((++i == 0 ? "WHERE" : "OR") + " (`statistic`=? AND `user_uuid`=?)", reference.getKey(), reference.getKey2());
		}
		query.add(";");
		return query;
	}

}
