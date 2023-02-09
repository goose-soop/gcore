package com.guillaumevdn.gcore.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.data.DataBackEnd;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class GPluginConfig {

	private boolean logMainConsole = true;
	private boolean logMainFile = true;
	private boolean logMainSQL = true;
	private List<String> logDataConsole = null;
	private List<String> logDataFile = null;
	private List<String> logDataSQL = null;
	private final Map<String, DataBackEnd> dataBackEnds = new HashMap<>(3);
	private boolean updateNotification = true;

	// ----- get
	public final boolean logMainConsole() {
		return logMainConsole;
	}

	public final boolean logMainFile() {
		return logMainFile;
	}

	public final boolean logMainSQL() {
		return logMainSQL;
	}

	public final boolean logDataConsole(Board board) {
		return logDataConsole == null || logDataConsole.contains(board.getId().toLowerCase());
	}

	public boolean logDataFile(Board board) {
		return logDataFile == null || logDataFile.contains(board.getId().toLowerCase());
	}

	public final boolean logDataSQL(Board board) {
		return logDataSQL == null || logDataSQL.contains(board.getId().toLowerCase());
	}

	public final DataBackEnd dataBackEnd(Board board) {
		String id = board.getId().toLowerCase();
		return dataBackEnds.getOrDefault(id, DataBackEnd.JSON);
	}

	public final boolean updateNotification() {
		return updateNotification;
	}

	// ----- load
	public final void load() throws Throwable {
		try {
			YMLConfiguration config = doLoad();
			if (config != null) {
				logMainConsole = config.readBoolean("log.main.console", true);
				logMainFile = config.readBoolean("log.main.file", true);
				logMainSQL = config.readBoolean("log.main.sql", true);
				logDataConsole = new ArrayList<>();
				logDataFile = new ArrayList<>();
				logDataSQL = new ArrayList<>();
				for (String boardId : config.readKeysForSection("log.data")) {
					if (config.readBoolean("log.data." + boardId + ".console", true)) logDataConsole.add(boardId.toLowerCase());
					if (config.readBoolean("log.data." + boardId + ".file", true)) logDataFile.add(boardId.toLowerCase());
					if (config.readBoolean("log.data." + boardId + ".sql", true)) logDataSQL.add(boardId.toLowerCase());
				}
				for (String boardId : config.readKeysForSection("data_backend")) {
					dataBackEnds.put(boardId.toLowerCase(), config.readEnum("data_backend." + boardId, DataBackEnd.JSON, DataBackEnd.class));
				}
				updateNotification = config.readBoolean("update_notification", true);
			}
		} catch (Throwable exception) {
			ConfigError configError = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (configError != null) throw configError;
			YMLError ymlError = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			throw ymlError != null ? ymlError : exception;
		}
	}

	/** @return a configuration file to load the loggers and data from, or null if none */
	protected abstract YMLConfiguration doLoad() throws Throwable;

}
