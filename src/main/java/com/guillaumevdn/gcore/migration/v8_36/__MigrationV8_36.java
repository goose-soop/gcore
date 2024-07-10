package com.guillaumevdn.gcore.migration.v8_36;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.migration.MigrationNextMinor;

/**
 * @author GuillaumeVDN
 */
public final class __MigrationV8_36 extends MigrationNextMinor {

	public __MigrationV8_36() {
		super(GCore.inst(), "data_v8", 8, 36);
	}

	@Override
	protected void doMigrate() throws Throwable {
		/*
		 * attemptOperation("converting JSON statistics to SQLite", BackupBehavior.RESTORE, () -> { YMLConfiguration config =
		 * getPlugin().loadConfigurationFile("config.yml"); if (config.getFile().exists()) { if
		 * (config.readString("data_backend.gcore_statistics_v8", "SQLITE").equals("JSON")) {
		 * log("Board is set to JSON, converting");
		 * 
		 * // change back-end to SQLITE in config config.write("data_backend.gcore_statistics_v8", "SQLITE"); config.save();
		 * countMod(); log("Changed back-end to SQLITE in config.yml");
		 * 
		 * // init temporary config and statistics board ReflectionObject pl = ReflectionObject.of(getPlugin());
		 * pl.invokeMethod("getClassLoader").invokeMethod("loadClass", "com.guillaumevdn.gcore.lib.data.sql.Query");
		 * pl.setField("configuration", new ConfigGCore());
		 * 
		 * BoardStatistics board = new BoardStatistics(); board.initialize(BukkitThread.current(), null);
		 * 
		 * // convert statistics for (File file : getPluginFile("data_v8/statistics").listFiles()) { String statisticName =
		 * FileUtils.getSimpleName(file); log("Converting statistic " + statisticName + "...");
		 * 
		 * Statistic statistic = Statistic.register(statisticName); try (FileReader reader = new FileReader(file)) { Map map =
		 * getPlugin().getPrettyGson().fromJson(reader, Map.class); if (map != null) { // there's a null issue somewhere around
		 * here #1339 map.forEach((rawKey, rawValue) -> { Double value = NumberUtils.doubleOrNull(rawValue.toString()); if
		 * (value != null) { UUID uuid = UUID.fromString(rawKey.toString()); Pair<Statistic, UUID> ref = Pair.of(statistic,
		 * uuid);
		 * 
		 * board.putInCache(ref, value); board.addCachedToSave(ref); countMod(); } }); } } }
		 * 
		 * // save board log("Saving new board..."); board.saveNeeded(BukkitThread.current());
		 * 
		 * SQLiteHandler handler = ReflectionObject.of(board).getField("connector").getField("handler").get();
		 * handler.performGetQuery(getPlugin(), null, new Query("SELECT * FROM " + board.getId()), (ResultSet set) -> { int rows
		 * = 0; while (set.next()) { ++rows; } log("... inserted " + rows + " row" + (rows > 1 ? "s" : "")); });
		 * board.shutdown();
		 * 
		 * // unregister temp things Statistic.unregisterAll(); pl.setField("configuration", null); } else {
		 * log("Board is not set to JSON, conversion will not be made (even if there are JSON statistics)"); } } });
		 */
	}

}
