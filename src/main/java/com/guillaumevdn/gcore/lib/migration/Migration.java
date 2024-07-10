package com.guillaumevdn.gcore.lib.migration;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.function.ThrowableBiConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.function.ThrowableTriConsumer;
import com.guillaumevdn.gcore.lib.logging.Logger;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.migration.YMLMigrationReading;
import com.guillaumevdn.gcore.migration.YMLMigrationWriting;

/**
 * @author GuillaumeVDN
 */
public abstract class Migration {

	private final GPlugin plugin;
	private final boolean mustBackup;
	private final String migrationName;
	private final File pluginFolder, backupFolder, successFile, hardlockFile;
	private final Logger logger;
	private int modCount = 0;

	public Migration(GPlugin plugin, String backupName, String migrationName, String successFilePath) {
		this(plugin, backupName != null, migrationName, plugin.getDataFolder(),
				new File(plugin.getDataFolder().getParentFile() + "/" + plugin.getName() + "_backup_on_" + backupName),
				new File(plugin.getDataFolder() + "/" + successFilePath), new File(plugin.getDataFolder() + "/hardlock"));
	}

	public Migration(GPlugin plugin, boolean mustBackup, String migrationName, File pluginFolder, File backupFolder, File successFile, File hardlockFile) {
		this.plugin = plugin;
		this.mustBackup = mustBackup;
		this.migrationName = migrationName;
		this.pluginFolder = pluginFolder;
		this.backupFolder = backupFolder;
		this.successFile = successFile;
		this.hardlockFile = hardlockFile;
		this.logger = new Logger(plugin, plugin.getName() + "-migration", true, false);
	}

	// ----- get
	public final GPlugin getPlugin() {
		return plugin;
	}

	public final boolean mustBackup() {
		return mustBackup;
	}

	public final String getMigrationName() {
		return migrationName;
	}

	public final File getPluginFolder() {
		return pluginFolder;
	}

	public final File getPluginFile(String path) {
		return new File(pluginFolder + "/" + path);
	}

	public final File getBackupFolder() {
		return backupFolder;
	}

	public final int getModCount() {
		return modCount;
	}

	// ----- set
	public final void countMod() {
		++modCount;
	}

	// ----- log
	public final void log(String line) {
		logger.warning(line);
	}

	public final void success(String line) {
		logger.info(line);
	}

	public final void error(String line) {
		error(line, null);
	}

	public final void debug(String line) {
		logger.debug(line);
	}

	public final void error(String line, Throwable cause) {
		logger.error(line, cause);
	}

	public final void fail(String error, Throwable cause, BackupBehavior backup) {
		fail(CollectionUtils.asList(error), cause, backup);
	}

	public final void fail(List<String> errors, Throwable cause, BackupBehavior backup) {
		// handle backup
		boolean backupError = false;
		Throwable backupStackTrace = null;
		if (mustBackup) {
			try {
				if (backup.equals(BackupBehavior.DELETE)) {
					backupError = FileUtils.delete(backupFolder);
				} else if (backup.equals(BackupBehavior.RESTORE)) {
					FileUtils.delete(pluginFolder);
					FileUtils.copy(backupFolder, pluginFolder);
					FileUtils.delete(backupFolder);
				}
			} catch (Throwable err) {
				backupError = true;
				backupStackTrace = err;
			}
		}
		boolean hardlock = backupError && backup.equals(BackupBehavior.RESTORE);
		if (hardlock) {
			FileUtils.reset(hardlockFile);
		}
		// print report
		error("---------- ERROR REPORT FOR " + migrationName + " -----------");
		error("Something went wrong. Please eventually follow instructions below, or send this report to the developer if you see any stacktrace or if you don't know what to do.");
		error("DETAIL :");
		errors.forEach(l -> logger.error("> " + l));
		if (cause != null) {
			logger.error("STACKTRACE :", cause);
		}
		if (mustBackup) {
			logger.error("BACKUP STATE : " + (backupError
					? "wanted to " + backup.name().toLowerCase() + ", but couldn't ; "
							+ (hardlock ? "the plugin won't try to migrate again until you fix it manually" : "please do it manually")
					: backup.name().toLowerCase() + "d"));
			if (backupStackTrace != null) {
				logger.error("BACKUP STACKTRACE :", backupStackTrace);
			}
		}
		debug("=============================================================");
	}

	// ----- migrate
	public boolean wasMade() {
		return successFile.exists();
	}

	public boolean markMade() {
		return FileUtils.ensureExistence(successFile);
	}

	public boolean markNotMade() {
		return FileUtils.delete(successFile);
	}

	/** @return true if the migration should proceed */
	public boolean mustMigrate() {
		return pluginFolder.exists();
	}

	/** @return false if the migration should silently fail because logs already were sent */
	protected abstract void doMigrate() throws Throwable;

	public final boolean process() {
		// hardlock
		if (hardlockFile.exists()) {
			error("A migration failed recently and that wasn't fixed. Before attempting to migrate, please restore old state and either fix errors mentioned in the fail report or send the report to the developper.");
			return false;
		}
		// already made
		if (wasMade()) {
			success("Already processed " + migrationName);
			return true;
		}
		// musn't migrate
		if (!mustMigrate()) {
			success("Nothing found to migrate for " + migrationName);
			return true;
		}
		// log start
		debug("=============== PROCESSING " + migrationName + " =================");
		// backup
		if (mustBackup) {
			try {
				FileUtils.delete(backupFolder);
				FileUtils.copy(pluginFolder, backupFolder, CollectionUtils.asSet("plugin_log.log", "logs")); // don't copy plugin log and log files
			} catch (Throwable exception) {
				fail("Couldn't create backup", exception, BackupBehavior.DELETE);
				return false;
			}
		}
		// migrate
		try {
			doMigrate();
		} catch (Throwable exception) {
			if (exception instanceof SilentFail) {
				return false;
			}
			fail("An unknown exception occured", exception, BackupBehavior.RESTORE);
			return false;
		}
		if (!markMade()) {
			fail("Couldn't mark migration " + migrationName + " as completed", null, BackupBehavior.RESTORE);
			return false;
		}
		// success
		success("------------ SUCCESS OF " + migrationName + " -------------");
		success("MOD COUNT : " + modCount);
		success("BACKUP STATE : not deleting, just in case something went wrong");
		debug("=============================================================");
		return true;
	}

	// ----- operation
	public final void attemptOperation(String operation, BackupBehavior backupOnFail, ThrowableRunnable runnable) throws Throwable {
		log("Attempting operation : " + operation);
		try {
			int count = modCount;
			runnable.run();
			if (count != modCount) {
				log("> ... made " + StringUtils.pluralizeAmountDesc("modification", modCount - count));
			}
		} catch (Throwable exception) {
			if (exception instanceof SilentFail) {
				throw exception;
			}
			fail("Couldn't perform operation : " + operation, exception, backupOnFail);
			throw new SilentFail();
		}
	}

	protected final void attemptYMLFilesOperation(String elementTypeName, BackupBehavior backupOnFail, String srcFolderPath, String targetFolderPath,
			ThrowableBiConsumer<YMLMigrationReading, YMLMigrationWriting> processor, String... ignoreElements) throws Throwable {
		attemptYMLFilesOperation("converting " + elementTypeName + "s", elementTypeName, backupOnFail, new File(getBackupFolder() + "/" + srcFolderPath),
				element -> new File(getPluginFolder() + "/" + targetFolderPath + "/" + element.toLowerCase() + ".yml"), processor, ignoreElements);
	}

	protected final void attemptYMLFilesOperation(String operation, String elementTypeName, BackupBehavior backupOnFail, File src,
			Function<String, File> targetSupplier, ThrowableBiConsumer<YMLMigrationReading, YMLMigrationWriting> processor, String... ignoreElements)
			throws Throwable {
		attemptOperation(operation, backupOnFail, () -> {
			doFileOperation(operation, elementTypeName, src, backupOnFail, filterYMLIfNotOneOf(ignoreElements), file -> {
				YMLMigrationReading elementSrc = new YMLMigrationReading(getPlugin(), file);
				YMLMigrationWriting target = new YMLMigrationWriting(getPlugin(), targetSupplier.apply(FileUtils.getSimpleName(file)));
				int count = modCount;
				processor.accept(elementSrc, target);
				if (count != modCount) {
					target.save();
				}
			});
		});
	}

	public final void attemptDirectYMLFilesOperation(String operation, String elementTypeName, BackupBehavior backupOnFail, File src,
			ThrowableConsumer<YMLConfiguration> processor, String... ignoreElements) throws Throwable {
		attemptOperation(operation, backupOnFail, () -> {
			doFileOperation(operation, elementTypeName, src, backupOnFail, filterYMLIfNotOneOf(ignoreElements), file -> {
				YMLConfiguration config = new YMLConfiguration(getPlugin(), file);
				int count = modCount;
				processor.accept(config);
				if (count != modCount) {
					config.save();
				}
			});
		});
	}

	protected final void attemptFilesOperation(String operation, String elementTypeName, BackupBehavior backupOnFail, File src, Predicate<File> filter,
			ThrowableConsumer<File> processor) throws Throwable {
		attemptOperation(operation, backupOnFail, () -> {
			doFileOperation(operation, elementTypeName, src, backupOnFail, filter, processor);
		});
	}

	private final void doFileOperation(String operation, String elementTypeName, File src, BackupBehavior backupOnFail, Predicate<File> filter,
			ThrowableConsumer<File> processor) throws Throwable {
		if (!src.exists()) {
			return;
		}
		if (src.isDirectory()) {
			for (File f : src.listFiles()) {
				doFileOperation(operation, elementTypeName, f, backupOnFail, filter, processor);
			}
		} else if (filter.test(src)) {
			String element = FileUtils.getSimpleName(src);
			try {
				int count = modCount;
				processor.accept(src);
				if (count != modCount) {
					log("> ... made " + StringUtils.pluralizeAmountDesc("modification", modCount - count) + " for " + elementTypeName + " " + element);
				}
			} catch (Throwable exception) {
				if (exception instanceof SilentFail) {
					throw exception;
				}
				fail("Couldn't perform operation : " + operation + ", on element " + element, exception, backupOnFail);
				throw new SilentFail();
			}
		}
	}

	protected final void attemptFromBackupSingleYMLFileToMultiple(String elementTypeName, BackupBehavior backupOnFail, String srcFilePath, String srcPath,
			String targetFolderPath, ThrowableTriConsumer<YMLMigrationReading, String, YMLMigrationWriting> processor, String... ignoreElements)
			throws Throwable {
		attemptSingleYMLFileToMultiple("converting " + elementTypeName + "s", elementTypeName, backupOnFail, new File(getBackupFolder() + "/" + srcFilePath),
				srcPath, element -> new File(getPluginFolder() + "/" + targetFolderPath + "/" + element.toLowerCase() + ".yml"), processor, ignoreElements);
	}

	protected final void attemptSingleYMLFileToMultiple(String operation, String elementTypeName, BackupBehavior backupOnFail, File src, String path,
			Function<String, File> targetSupplier, ThrowableTriConsumer<YMLMigrationReading, String, YMLMigrationWriting> processor, String... ignoreElements)
			throws Throwable {
		attemptOperation(operation, backupOnFail, () -> {
			if (!src.exists()) {
				return;
			}
			log("Attempting single YML file to multiple operation : " + operation);
			YMLMigrationReading config = new YMLMigrationReading(getPlugin(), src);
			for (String element : config.readKeysForSection(path)) {
				if (Stream.of(ignoreElements).anyMatch(ignore -> ignore.equalsIgnoreCase(element))) {
					continue;
				}
				try {
					YMLMigrationWriting target = new YMLMigrationWriting(getPlugin(), targetSupplier.apply(element));
					int count = modCount;
					processor.accept(config, path + "." + element, target);
					if (count != modCount) {
						target.save();
						log("> ... made " + StringUtils.pluralizeAmountDesc("modification", modCount - count) + " for " + elementTypeName + " " + element);
					}
				} catch (Throwable exception) {
					if (exception instanceof SilentFail) {
						throw exception;
					}
					fail("Couldn't perform operation : " + operation + ", on element " + element, exception, backupOnFail);
					throw new SilentFail();
				}
			}
		});
	}

	protected static final Predicate<File> FILTER_YML = file -> file.getName().toLowerCase().endsWith(".yml");

	protected static Predicate<File> filterYMLIfNotOneOf(String... ignore) {
		return file -> FILTER_YML.test(file) && !Stream.of(ignore).anyMatch(name -> file.getName().equalsIgnoreCase(name));
	}

}
