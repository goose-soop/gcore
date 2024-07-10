package com.guillaumevdn.gcore.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.data.board.keyed.bi.BiKeyedBoardRemote;
import com.guillaumevdn.gcore.lib.data.board.keyed.bi.ConnectorBiKeyedJson;
import com.guillaumevdn.gcore.lib.data.board.keyed.bi.ConnectorBiKeyedSQL;
import com.guillaumevdn.gcore.lib.data.sql.MySQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLHandler;
import com.guillaumevdn.gcore.lib.data.sql.SQLiteHandler;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.statistic.Statistic;

/**
 * @author GuillaumeVDN
 */
public class BoardStatistics extends BiKeyedBoardRemote<Statistic, UUID, Double> {

	private static BoardStatistics instance = null;

	public static BoardStatistics inst() {
		return instance;
	}

	public BoardStatistics() {
		super(GCore.inst(), "gcore_statistics_v8", Double.class, 20 * 60);
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
			Statistic.values().forEach(statistic -> fetchValue(statistic, player.getUniqueId(), null, null, true, true));
		}
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- json
	// ----------------------------------------------------------------------------------------------------

	@Override
	protected ConnectorBiKeyedJson<Statistic, UUID, Double> createConnectorJson() {
		return new ConnectorBiKeyedJson<Statistic, UUID, Double>(this) {

			@Override
			public File getRoot() {
				return GCore.inst().getDataFile("data_v8/statistics/");
			}

			@Override
			public File getFile(Statistic key) {
				return GCore.inst().getDataFile("data_v8/statistics/" + key + ".json");
			}

			@Override
			public Statistic getKey(File file) {
				return Statistic.safeValueOf(FileUtils.getSimpleName(file));
			}

			@Override
			protected Map<UUID, Double> secondaryAndValuesFromJson(FileReader reader) {
				Map<UUID, Double> fixed = new HashMap<>();
				Map map = getPlugin().getPrettyGson().fromJson(reader, Map.class);
				if (map != null) { // there's a null issue somewhere around here #1339
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
			protected void secondaryAndValuesToJson(Map<UUID, Double> values, FileWriter writer) {
				board.getPluginGson().toJson(values, values.getClass(), writer);
			}

		};
	}

	// ----------------------------------------------------------------------------------------------------
	// ----- mysql
	// ----------------------------------------------------------------------------------------------------

	private ConnectorBiKeyedSQL<Statistic, UUID, Double> createConnectorSQL(SQLHandler handler) {
		return new ConnectorBiKeyedSQL<Statistic, UUID, Double>(this, handler) {

			@Override
			public String keyName() {
				return "statistic";
			}

			@Override
			public String key2Name() {
				return "user_uuid";
			}

			@Override
			protected Statistic decodeKey(String raw) {
				return Statistic.safeValueOf(raw);
			}

			@Override
			protected UUID decodeKey2(String raw) {
				return UUID.fromString(raw); // row can't contain an invalid UUID, since the query was built from a valid UUID object
			}

			@Override
			protected Double getValue(ResultSet set) throws SQLException {
				return set.getDouble(valueName());
			}

		};
	}

	@Override
	protected ConnectorBiKeyedSQL<Statistic, UUID, Double> createConnectorMySQL() {
		return createConnectorSQL(new MySQLHandler(ConfigGCore.createMySQLConnector(getPlugin())));
	}

	@Override
	protected ConnectorBiKeyedSQL<Statistic, UUID, Double> createConnectorSQLite() {
		return createConnectorSQL(new SQLiteHandler(getPlugin(), GCore.inst().getDataFile("data_v8/statistics.sqlite.db")));
	}

}
