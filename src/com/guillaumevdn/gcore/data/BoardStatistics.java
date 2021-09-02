package com.guillaumevdn.gcore.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.data.Query;
import com.guillaumevdn.gcore.lib.data.board.keyed.BiKeyReference;
import com.guillaumevdn.gcore.lib.data.board.keyed.BiKeyedBoardRemote;
import com.guillaumevdn.gcore.lib.data.board.keyed.KeyReference;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
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

	// ----- get
	public void alterValue(Statistic key, UUID key2, double delta, Runnable onPush, boolean forceFetch, boolean mustCache) {
		fetchValue(key, key2, value -> {
			putValue(key, key2, value + delta, onPush, mustCache);
		}, () -> 0d, forceFetch, mustCache);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- load
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected void onInitialized() {
		for (Player player : PlayerUtils.getOnline()) {
			Statistic.values().forEach(statistic -> BoardStatistics.inst().fetchValue(statistic, player.getUniqueId(), null, null, true, true));
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- data
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected RWHashMap<UUID, Double> valueFromJson(FileReader reader) {
		RWHashMap<UUID, Double> fixed = new RWHashMap<>(10, 1f);
		Map map = getPlugin().getPrettyGson().fromJson(reader, Map.class);
		if (map != null) {  // there's a null issue somewhere around here #1339
			map.forEach((key, value) -> {
				Double dbl = NumberUtils.doubleOrNull(value.toString());
				if (dbl != null) {
					fixed.put(UUID.fromString(key.toString()), dbl);
				}
			});
		}
		return fixed;
	}

	@Override
	protected void valueToJson(RWHashMap<UUID, Double> value, FileWriter writer) {
		HashMap<UUID, Double> copy = value.copy();  // otherwise can't write to json
		getPlugin().getPrettyGson().toJson(copy, copy.getClass(), writer);
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- json
	// ----------------------------------------------------------------------------------------------------

	// ----- file
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
	// ----- mysql
	// ----------------------------------------------------------------------------------------------------

	// ----- init
	private final String TABLE_NAME = getId();

	@Override
	protected void remoteInitMySQL() throws Throwable {
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(),
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ "`key` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
						+ "statistic VARCHAR(100) NOT NULL,"
						+ "user_uuid CHAR(36) NOT NULL,"
						+ "value DECIMAL(30,3) NOT NULL,"
						+ "PRIMARY KEY(`key`)"
						+ ") ENGINE=InnoDB DEFAULT CHARSET = 'utf8';"
				);
	}

	// ----- pull
	@Override
	protected void remotePullAllMySQL() throws Throwable {
		remotePullElementsMySQL(new Query("SELECT * FROM " + TABLE_NAME + ";"), () -> cache.clear());
	}

	@Override
	protected void remotePullElementsMySQL(Set<BiKeyReference<Statistic, UUID>> references) throws Throwable {
		if (references.isEmpty()) return;
		String query = "SELECT * FROM " + TABLE_NAME + " ";
		int i = -1;
		for (BiKeyReference<Statistic, UUID> ref : references) {
			query += (++i == 0 ? "WHERE" : "OR") + " (statistic = " + Query.escapeValue(ref.getKey().toString()) + " AND user_uuid = " + Query.escapeValue(ref.getKey2().toString()) + ")";
		}
		query += ";";
		remotePullElementsMySQL(new Query(query), () -> references.forEach(ref -> deleteCacheElement(ref.getKey(), ref.getKey2())));
	}

	@Override
	protected void remotePullKeysMySQL(Set<KeyReference<Statistic>> references) throws Throwable {
		if (references.isEmpty()) return;
		Query query = Query.buildSelectKeysIn(TABLE_NAME, "statistic", references);
		remotePullElementsMySQL(query, () -> references.forEach(ref -> cache.remove(ref.getKey())));
	}

	private void remotePullElementsMySQL(Query query, Runnable beforeSetProcessing) throws Throwable {
		GCore.inst().getMySQLConnector().performGetQuery(getPlugin(),
				getLogger(),
				query,
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
						cache.computeIfAbsent(stat, __ -> new RWHashMap<>(10, 1f)).put(uuid, value);
					}
				});
	}

	// ----- push
	@Override
	protected void remotePushElementsMySQL(Set<BiKeyReference<Statistic, UUID>> refs) throws Throwable {
		if (refs.isEmpty()) return;  // let's avoid deleting the whole table just because there's no WHERE clause
		for (Collection<? extends BiKeyReference<Statistic, UUID>> references : CollectionUtils.splitCollection(refs, 999)) {  // multiple VALUES are limited to 1000 elements ; https://stackoverflow.com/questions/452859/inserting-multiple-rows-in-a-single-sql-query#comment22032805_452934
			Query query = buildRemoteDeleteElementsMySQLQuery(references);
			query.add("INSERT INTO " + TABLE_NAME + " (statistic, user_uuid,value) VALUES ");
			int i = -1;
			for (BiKeyReference<Statistic, UUID> reference : references) {
				query.add((++i != 0 ? "," : "") + "(" + Query.escapeValue(reference.getKey().toString()) + "," + Query.escapeValue(reference.getKey2().toString()) + "," + getCachedValue(reference.getKey(), reference.getKey2()).toString() + ")");
			}
			query.add(";");
			GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(), query);
		}
	}

	// ----- delete
	@Override
	protected void remoteDeleteElementsMySQL(Set<BiKeyReference<Statistic, UUID>> references) throws Throwable {
		if (references.isEmpty()) return; // let's avoid deleting the whole table just because there's no WHERE clause
		GCore.inst().getMySQLConnector().performUpdateQuery(getPlugin(), getLogger(), buildRemoteDeleteElementsMySQLQuery(references));
	}

	private Query buildRemoteDeleteElementsMySQLQuery(Collection<? extends BiKeyReference<Statistic, UUID>> references) {
		if (references.isEmpty()) return null; // let's avoid deleting the whole table just because there's no WHERE clause ; this shouldn't happen since this method is private but better be juuust a little more sure
		Query query = new Query("DELETE FROM " + TABLE_NAME + " ");
		int i = -1;
		for (BiKeyReference<Statistic, UUID> reference : references) {
			query.add((++i == 0 ? "WHERE" : "OR") + " (statistic = " + Query.escapeValue(reference.getKey().toString()) + " AND user_uuid = " + Query.escapeValue(reference.getKey2().toString()) + ")");
		}
		query.add(";");
		return query;
	}

}
