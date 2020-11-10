package com.guillaumevdn.gcore.lib.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class Logger {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd'/'MM HH':'mm':'ss");
	protected static final DateTimeFormatter LOCALDATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'-'HH'-'mm'-'ss");

	private GPlugin plugin;
	private String id;
	private boolean logConsole, logFile, antiSpam;
	private int fileLineLimit;
	private List<String> linesToSave = new ArrayList<>();

	public Logger(GPlugin plugin, String id, boolean logConsole, boolean logFile, int fileLineLimit) {
		this(plugin, id, logConsole, logFile, fileLineLimit, true);
	}

	public Logger(GPlugin plugin, String id, boolean logConsole, boolean logFile, int fileLineLimit, boolean antiSpam) {
		this.plugin = plugin;
		this.id = id;
		this.logConsole = logConsole;
		this.logFile = logFile;
		this.antiSpam = antiSpam;
		this.fileLineLimit = fileLineLimit;
	}

	// get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final String getId() {
		return id;
	}

	public File getFile() {
		return new File(plugin.getDataFolder() + "/logs/" + id + ".log");
	}

	public File getArchiveFile(LocalDateTime date) {
		return new File(plugin.getDataFolder() + "/logs_archives/" + id + "-" + date.format(LOCALDATETIME_FORMAT) + ".log");
	}

	public boolean isLogConsole() {
		return logConsole;
	}

	public final boolean isLogFile() {
		return logFile;
	}

	// methods
	public void info(String line) {
		info(line, false);
	}

	public void info(String line, boolean ignoreAntiSpam) {
		log(LogLevel.INFO, line, true, ignoreAntiSpam, null);
	}

	public void warning(String line) {
		warning(line, false);
	}

	public void warning(String line, boolean ignoreAntiSpam) {
		log(LogLevel.WARNING, line, true, ignoreAntiSpam, null);
	}

	public void error(String line) {
		error(line, null, false);
	}

	public void error(String line, boolean ignoreAntiSpam) {
		error(line, null, ignoreAntiSpam);
	}

	public void error(String line, Throwable cause) {
		error(line, cause, false);
	}

	public void error(String line, Throwable cause, boolean ignoreAntiSpam) {
		log(LogLevel.ERROR, line, true, ignoreAntiSpam, cause);
	}

	public void debug(String line) {
		log(LogLevel.DEBUG, line, true, true, null);
	}

	private transient Map<String, Long> lastLogged = new HashMap<>();

	public void log(LogLevel level, String line, boolean printIdInConsole, Throwable trace) {
		log(level, line, printIdInConsole, true, trace);
	}

	public void log(LogLevel level, String line, boolean printIdInConsole, boolean ignoreAntiSpam, Throwable trace) {
		// already logged recently
		if (antiSpam && !ignoreAntiSpam) {
			if (System.currentTimeMillis() - lastLogged.computeIfAbsent(line, __ -> 0L) < 5000L) {
				return;
			}
			lastLogged.put(line, System.currentTimeMillis());
		}
		// log to console
		if (isLogConsole() || level.equals(LogLevel.ERROR)) {
			Bukkit.getConsoleSender().sendMessage(level.getConsoleColor() + (printIdInConsole ? "[" + id + "] " : "") + (trace != null && trace instanceof ConfigError ? line + ", " + trace.getMessage() : line));
			if (trace != null && !(trace instanceof ConfigError)) {
				trace.printStackTrace();
			}
		}
		// log to file
		if (logFile) {
			linesToSave.add("[" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] [" + level.getFilePrefix() + "] " + line);
			if (trace != null && !(trace instanceof ConfigError)) {
				StringWriter writer = new StringWriter();
				trace.printStackTrace(new PrintWriter(writer));
				linesToSave.add(writer.toString());
			}
		}
	}

	// saving
	public final void startSaving() {
		stopSaving();
		if (logFile) {
			plugin.registerTask("logger_filesave_" + id.toLowerCase(), true, 20 * 30, () -> saveFileIfPersistent());
		}
	}

	public final void stopSaving() {
		plugin.stopTask("logger_filesave_" + id.toLowerCase());
	}

	public void saveFileIfPersistent() throws IOException {
		if (logFile && !linesToSave.isEmpty()) {
			File file = getFile();
			try {
				// get new final line count
				int newLineCount = 0;
				if (file.exists()) {
					try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
						while (reader.readLine() != null);
						newLineCount = reader.getLineNumber();
					}
				}
				newLineCount += linesToSave.size();
				// write lines
				FileUtils.ensureExistence(file);
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
				CollectionUtils.clearForEachThrowable(linesToSave, line -> writer.write(line + "\n"));
				writer.close();
				// maybe file has too many lines, so archive it
				if (newLineCount > fileLineLimit) {
					File archiveFile = getArchiveFile(LocalDateTime.now());
					FileUtils.delete(archiveFile);
					file.renameTo(archiveFile);
				}
			} catch (Throwable exception) {
				throw new IOException("couldn't save lines for logger " + getId() + ", plugin " + plugin.getName(), exception);
			}
			try {
				// logger file is too big, move it
				if (FileUtils.countLines(file) > 5000) {
					File dest = null;
					int count = 0;
					do {
						String countStr = String.valueOf(++count);
						dest = new File(file.getParentFile(), file.getName() + "_archive_" + StringUtils.repeatString("0", 4 - countStr.length()) + countStr);
					} while (dest.exists());
					file.renameTo(dest);
				}
			} catch (Throwable exception) {
				throw new IOException("couldn't save lines for logger " + getId() + ", plugin " + plugin.getName(), exception);
			}
		}
	}

}
