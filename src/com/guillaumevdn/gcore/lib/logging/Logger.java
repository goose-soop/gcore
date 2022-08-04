package com.guillaumevdn.gcore.lib.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ConcurrentModificationException;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;

/**
 * @author GuillaumeVDN
 */
public class Logger {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd'/'MM HH':'mm':'ss");
	protected static final DateTimeFormatter LOCALDATETIME_FORMAT = DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'-'HH'-'mm'-'ss");
	public static final int REGULAR_LINE_LIMIT = 100000;

	private GPlugin plugin;
	private String id;
	private boolean logConsole, logFile;
	private int fileLineLimit;

	public Logger(GPlugin plugin, String id, boolean logConsole, boolean logFile) {
		this.plugin = plugin;
		this.id = id;
		this.logConsole = logConsole;
		this.logFile = logFile;
		this.fileLineLimit = REGULAR_LINE_LIMIT;
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final String getId() {
		return id;
	}

	public File getFile() {
		return new File(plugin.getDataFolder() + "/logs/" + id + ".log");
	}

	public File getArchiveFile(ZonedDateTime date) {
		return new File(plugin.getDataFolder() + "/logs/archived/" + id + "-" + date.format(LOCALDATETIME_FORMAT) + ".log");
	}

	public boolean isLogConsole() {
		return logConsole;
	}

	public final boolean isLogFile() {
		return logFile;
	}

	// ----- methods
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
		log(LogLevel.DEBUG, line, true, false, null);
	}

	//private transient RWHashMap<String, Long> lastLogged = new RWHashMap<>(10, 1f);  // #925, concurrent mod exception ; // UPDATE : this simply takes too much memory

	public void log(LogLevel level, String line, boolean printIdInConsole, Throwable trace) {
		log(level, line, printIdInConsole, false, trace);
	}

	public void log(LogLevel level, String line, boolean printIdInConsole, boolean ignoreAntiSpam, Throwable trace) {
		try {
			// already logged recently
			/*if (antiSpam && !ignoreAntiSpam) {
				if (System.currentTimeMillis() - lastLogged.computeIfAbsent(line, __ -> 0L) < 1000L) {
					return;
				}
				lastLogged.put(line, System.currentTimeMillis());
			}*/
			// log to console
			if (isLogConsole() || level.equals(LogLevel.ERROR)) {
				Bukkit.getConsoleSender().sendMessage(level.getConsoleColor() + (printIdInConsole ? "[" + id + "] " : "") + (trace != null && trace instanceof ConfigError ? line + ", " + trace.getMessage() : line));
				if (trace != null && !(trace instanceof ConfigError)) {
					trace.printStackTrace();
				}
			}
			// log to file
			if (logFile) {
				String toWrite = "[" + DATE_FORMAT.format(Calendar.getInstance().getTime()) + "] [" + level.getFilePrefix() + "] " + line;
				if (trace != null && !(trace instanceof ConfigError)) {
					toWrite += "\n" + Reflection.stackTraceToString(trace);
				}
				writeLineIfPersistent(toWrite);
			}
		} catch (Throwable logError) {
			if (!(logError instanceof ConcurrentModificationException)) {
				logError.printStackTrace();
			}
		}
	}

	// ----- saving
	public final void startSaving() {
		/*stopSaving();
		if (logFile) {
			plugin.registerTask("logger_filesave_" + id.toLowerCase(), true, 20, () -> saveFileIfPersistent());
		}*/
	}

	public final void stopSaving() {
		//plugin.stopTask("logger_filesave_" + id.toLowerCase());
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static final long LINE_COUNT_CHECK_INTERVAL = 15L * 60L * 1000L;
	private static final long REOPEN_INTERVAL = 15L * 60L * 1000L;
	private transient long lastReopen = System.currentTimeMillis();
	private transient long lastCheckedLineCount = System.currentTimeMillis();
	private transient BufferedWriter writer = null;

	private void writeLineIfPersistent(String line) throws IOException {
		if (!logFile) {
			return;
		}

		File file = getFile();
		try {
			// get new final line count
			if (System.currentTimeMillis() - lastCheckedLineCount > LINE_COUNT_CHECK_INTERVAL) {
				lastCheckedLineCount = System.currentTimeMillis();
				if (file.exists()) {
					int newLineCount = 0;
					try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
						while (reader.readLine() != null) {
							++newLineCount;
						}
					}

					// maybe file has too many lines, so archive it
					if (newLineCount > fileLineLimit) {
						File archiveFile = getArchiveFile(ConfigGCore.timeNow());
						FileUtils.delete(archiveFile);
						file.renameTo(archiveFile);
					}
				}
			}

			// (re)open writer and write line
			try {
				if (writer == null || System.currentTimeMillis() - lastReopen > REOPEN_INTERVAL) {
					lastReopen = System.currentTimeMillis();
					if (writer != null) {
						writer.close();
					}
					FileUtils.ensureExistence(file);
					writer = new BufferedWriter(new FileWriter(file, true));
				}
				try {
					writer.write(line + "\n");
				} catch (IOException ignored) {
					if ("Stream closed".equals(ignored.getMessage())) {
						if (writer == null || System.currentTimeMillis() - lastReopen > REOPEN_INTERVAL) {
							lastReopen = System.currentTimeMillis();
							if (writer != null) {
								writer.close();
							}
							FileUtils.ensureExistence(file);
							writer = new BufferedWriter(new FileWriter(file, true));
							writer.write(line + "\n");
						}
					}
				}
			} catch (IOException ignored) {
				ignored.printStackTrace();
				writer = null;
			}
		} catch (Throwable exception) {
			throw new IOException("couldn't save lines for logger " + getId() + ", plugin " + plugin.getName(), exception);
		}
	}

}
