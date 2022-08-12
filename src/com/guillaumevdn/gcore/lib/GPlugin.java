package com.guillaumevdn.gcore.lib;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.chat.JsonMessage;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.command.Command;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.generic.GenericMigrate;
import com.guillaumevdn.gcore.lib.command.generic.GenericPluginState;
import com.guillaumevdn.gcore.lib.command.generic.GenericReload;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.Bossbar;
import com.guillaumevdn.gcore.lib.concurrency.RWLowerCaseHashMap;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.data.board.Board;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.file.ResourceExtractor;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.logging.Logger;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.permission.PermissionContainer;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.TextFile;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;
import com.guillaumevdn.gcore.libs.com.google.gson.GsonBuilder;
import com.guillaumevdn.gcore.libs.org.bstats.Metrics;

/**
 * @author GuillaumeVDN
 */
public abstract class GPlugin<C extends GPluginConfig, P extends PermissionContainer> extends JavaPlugin {

	private final int spigotResourceId;
	private final String mainCommandName;
	private final String mainCommandHelpAlias;
	private final Class<C> configurationClass;
	private final Class<P> permissionContainerClass;
	private final String dataContainerFolderName;
	private final List<Class<? extends Migration>> migrations;

	private Command mainCommand = null;
	private C configuration = null;
	private P permissionContainer = null;

	private final RWLowerCaseHashMap<TextFile<?>> textFiles = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Board> data = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Command> commands = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Listener> listeners = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Task> tasks = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Logger> loggers = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<GUI> guis = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Bossbar> bossbars = new RWLowerCaseHashMap<>(5, 1f);
	private final RWLowerCaseHashMap<Integration> integrations = new RWLowerCaseHashMap<>(5, 1f);
	private Metrics metrics = null;

	private Logger mainLogger = new Logger(this, getName() + "-" + getDescription().getVersion(), true, false);  // define it temporarily for start ; don't log in file at first, it can break migrations on windows
	private boolean activated = false;
	private Object lifecycleReference = new Object();  // changed on every reload ; allows to create WeakHashMap caches that reset on reload
	private Gson gson = createGsonBuilder().create();
	private Gson prettyGson = createGsonBuilder().setPrettyPrinting().create();

	public GsonBuilder createGsonBuilder() {
		return FileUtils.createGsonBuilder();
	}

	public GPlugin(int spigotResourceId, String mainCommandName, String mainCommandHelpAlias, Class<C> configurationClass, Class<P> permissionContainerClass, String dataContainerFolderName, Class<? extends Migration>... migrations) {
		this.spigotResourceId = spigotResourceId;
		this.mainCommandName = mainCommandName;
		this.mainCommandHelpAlias = mainCommandHelpAlias;
		this.migrations = CollectionUtils.asUnmodifiableList(migrations);
		this.configurationClass = configurationClass;
		this.permissionContainerClass = permissionContainerClass;
		this.dataContainerFolderName = dataContainerFolderName;
	}

	// ----- get
	public final int getSpigotResourceId() {
		return spigotResourceId;
	}

	public final String getMainCommandName() {
		return mainCommandName;
	}

	public final String getMainCommandHelpAlias() {
		return mainCommandHelpAlias;
	}

	public final Class<C> getConfigurationClass() {
		return configurationClass;
	}

	public final Class<P> getPermissionContainerClass() {
		return permissionContainerClass;
	}

	public final String getDataContainerFolderName() {
		return dataContainerFolderName;
	}

	public final List<Class<? extends Migration>> getMigrations() {
		return migrations;
	}

	public final Command getMainCommand() {
		return mainCommand;
	}

	protected Subcommand buildMainCommandBase() {
		return null;
	}

	public final C getConfiguration() {
		return configuration;
	}

	public final P getPermissionContainer() {
		return permissionContainer;
	}

	public final YMLConfiguration loadConfigurationFile(String path) {
		return new YMLConfiguration(this, getDataFile(path));
	}

	public final List<GUI> getGUIs() {
		return Collections.unmodifiableList(guis.copyValues());
	}

	public final RWLowerCaseHashMap<Board> getData() {
		return data;
	}

	public final File getDataFile(String path) {
		return new File(getDataFolder() + "/" + path);
	}

	public final Integration getIntegration(String pluginName) {
		return integrations.get(pluginName);
	}

	public final RWLowerCaseHashMap<Integration> getIntegrations() {
		return integrations;
	}

	public final Logger getLogger(String id) {
		return loggers.get(id);
	}

	public final Logger getMainLogger() {
		return mainLogger;
	}

	public final RWLowerCaseHashMap<Logger> getLoggers() {
		return loggers;
	}

	public final RWLowerCaseHashMap<Task> getTasks() {
		return tasks;
	}

	public final RWLowerCaseHashMap<Listener> getListeners() {
		return listeners;
	}

	public final RWLowerCaseHashMap<Bossbar> getBossbars() {
		return bossbars;
	}

	public final boolean isActivated() {
		return activated;
	}

	public final Object getLifecycleReference() {
		return lifecycleReference;
	}

	public final Gson getGson() {
		return gson;
	}

	public final Gson getPrettyGson() {
		return prettyGson;
	}

	// ----- enable
	private final boolean migrate() throws Throwable {
		for (Class<? extends Migration> cls : migrations) {
			if (!cls.newInstance().process()) {
				return false;
			}
		}
		return true;
	}

	protected void registerTypes() {
	}

	protected void registerTexts() {
	}

	protected void registerData() {
	}

	protected void preEnable() throws Throwable {
	}

	protected void enable() throws Throwable {
	}

	protected void registerAndEnableIntegrations() {
	}

	protected void registerAndEnableIntegrationsPostConfig() {
	}

	protected void onGPluginEnable(GPlugin plugin) {
	}

	@Override
	public void onEnable() {
		lifecycleReference = new Object();
		activated = false;
		try {
			// GCore isn't enabled
			try {
				if (!GCore.inst().equals(this) && !GCore.inst().isEnabled()) {
					failEnable("GCore isn't enabled");
					return;
				}
			} catch (Throwable exception) {
				failEnable("Couldn't check if GCore is enabled", exception);
				return;
			}

			// mark all migrations as done if there's no plugin folder
			try {
				if (!getDataFolder().exists()) {
					for (Class<? extends Migration> cls : migrations) {
						if (!cls.newInstance().markMade()) {
							failEnable("Couldn't mark migration as made");
						}
					}
				}
			} catch (Throwable exception) {
				failEnable("couldn't check for previous migrations", exception);
				return;
			}

			// pre-enable
			try {
				preEnable();
			} catch (Throwable exception) {
				failEnable("couldn't check for previous migrations", exception);
				return;
			}

			// migrate
			try {
				if (!migrate()) {
					failEnable(null);
					return;
				}
			} catch (Throwable exception) {
				failEnable(null);
				return;
			}

			// log version if GCore
			if (GCore.inst().equals(this)) {
				// log version
				if (Version.CURRENT.equals(Version.UNSUPPORTED)) {
					getMainLogger().warning("Couldn't find registered server version ; using UNSUPPORTED with found package " + Version.CURRENT.getNMSPackage());
				} else if (Version.CURRENT.equals(Version.UNKNOWN)) {
					getMainLogger().error("Couldn't find server version");
				} else {
					getMainLogger().info("Found server version " + Version.CURRENT);
				}
			}

			// register types
			registerTypes();

			// trigger other plugins
			// do this before config load, so that integrations from other plugins are registered (integrations are usually type registration and such, so it must be done here, so it loads before)
			// integrations from THIS plugin though might still need a loading delay if said plugins are enabled after config load
			triggerEnableInOtherPlugins();

			// register texts
			try {
				registerTexts();
			} catch (Throwable exception) {
				failEnable("Couldn't register texts", exception);
				return;
			}

			// save default configs
			try {
				int savedConfig = new ResourceExtractor(this, new File(getDataFolder() + "/"), "resources/", extractDefaultConfigIgnoreStartingPaths()).extract(false, true);
				if (savedConfig > 0) mainLogger.info("Saved " + StringUtils.pluralize(savedConfig + " default configuration file", savedConfig));
			} catch (Throwable exception) {
				failEnable("Couldn't extract default config file ", exception);
				return;
			}

			// initialize texts file if has texts
			if (!textFiles.isEmpty()) {
				// extract default text files
				File defaultFolder = getDefaultTextsFolder();
				try {
					FileUtils.delete(defaultFolder);
					defaultFolder.mkdirs();
					new ResourceExtractor(this, defaultFolder, "resources/texts/").extract(false, true);
				} catch (Throwable exception) {
					failEnable("Couldn't extract default text files", exception);
					return;
				}

				// save default text files and load
				try {
					// missing files
					File textsFolder = new File(getDataFolder() + "/texts/");
					textsFolder.mkdirs();
					new ResourceExtractor(this, textsFolder, "resources/texts").extract(false, true);
				} catch (Throwable exception) {
					failEnable("Couldn't extract default text files", exception);
					return;
				}
			}

			// register and enable integrations before configuration
			try {
				registerAndEnableIntegrations();
			} catch (Throwable exception) {
				failEnable("Couldn't enable integrations (pre-config)", exception);
				return;
			}

			// load config
			if (configurationClass != null) {
				try {
					Constructor<C> constructor = configurationClass.getDeclaredConstructor();
					if (!constructor.isAccessible()) {
						constructor.setAccessible(true);
					}
					configuration = constructor.newInstance();
					configuration.load();
				} catch (Throwable exception) {
					if (exception instanceof ConfigError || exception instanceof YMLError) {
						failEnable(exception.getMessage());
					} else {
						failEnable("Couldn't load configuration :", exception);
					}
					return;
				}
			}

			// read texts
			if (!readTexts(ConfigGCore.langId)) {
				return;
			}

			// register and enable integrations after configuration
			try {
				registerAndEnableIntegrationsPostConfig();
			} catch (Throwable exception) {
				failEnable("Couldn't enable integrations (post-config)", exception);
				return;
			}

			// load permission container
			if (permissionContainerClass != null) {
				try {
					Constructor<P> constructor = permissionContainerClass.getDeclaredConstructor();
					if (!constructor.isAccessible()) {
						constructor.setAccessible(true);
					}
					permissionContainer = constructor.newInstance();
				} catch (Throwable exception) {
					failEnable("Couldn't load permission container " + permissionContainerClass.getName(), exception);
					return;
				}
			}

			// initialize main command
			mainCommand = registerCommand(new Command(this, mainCommandName, mainCommandName, buildMainCommandBase()));
			mainCommand.setSubcommand(new GenericReload(this));
			mainCommand.setSubcommand(new GenericMigrate(this));
			mainCommand.setSubcommand(new GenericPluginState(this));

			// register main logger
			registerLogger(mainLogger = new Logger(this, getName() + "-" + getDescription().getVersion(), getConfiguration().logMainConsole(), getConfiguration().logMainFile()));

			// register data
			try {
				registerData();
			} catch (Throwable exception) {
				failEnable("Couldn't register data", exception);
				return;
			}

			// enable
			try {
				enable();
			} catch (Throwable exception) {
				if (exception.getMessage() == null || !exception.getMessage().equalsIgnoreCase("ignore")) {
					failEnable("Couldn't enable", exception);
				}
				return;
			}

			// notify update and notify update listeners
			notifyUpdate(Bukkit.getConsoleSender());
			registerListener("join_update", new Listener() {
				@EventHandler(priority = EventPriority.LOWEST)
				public void event(PlayerJoinEvent event) {
					if (permissionContainer.getAdminPermission().has(event.getPlayer())) {
						notifyUpdate(event.getPlayer());
					}
				}
			});

			// bossbar listeners ; centralize them all here because when bossbars are started/stopped, the registration + HandlerList.unregisterAll(listener) things take an unholy amount of time to execute (about half of the time of the whole bossbar sending process) #1734
			registerListener("bossbar", new Listener() {
				@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
				public void event(PlayerQuitEvent event) {
					bossbars.iterateAndModify((id, bossbar, iter) -> {
						bossbar.handleDisconnect(event.getPlayer());
						if (bossbar.getPlayers().isEmpty()) {
							iter.remove();
						}
					});
				}
				@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
				public void event(PlayerTeleportEvent event) {
					bossbars.iterateAndModify((id, bossbar, iter) -> bossbar.handleTeleport(event.getPlayer()));
				}
				@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
				public void event(PlayerRespawnEvent event) {
					bossbars.iterateAndModify((id, bossbar, iter) -> bossbar.handleTeleport(event.getPlayer()));
				}
			});

			// start logger tasks
			loggers.forEach((__, logger) -> logger.startSaving());

			// initialize data boards
			data.forEach((__, board) -> board.initialize(BukkitThread.ASYNC, () -> board.startSaving()));

			// register commands
			commands.forEach((__, command) -> getCommand(command.getName()).setExecutor(command));

			// register listeners
			listeners.forEach((__, listener) -> {
				Bukkit.getPluginManager().registerEvents(listener, this);
			});

			// start tasks
			tasks.forEach((__, task) -> {
				if (!task.isActive()) {
					task.start();
				}
			});

			// enable metrics (do not re-do this on plugin reload)
			try {
				if (metrics == null) {
					metrics = new Metrics(this);
				}
			} catch (Throwable exception) {
				getMainLogger().error("Could not initialize metrics", exception);
			}

			// mark as activated
			activated = true;
			Bukkit.getConsoleSender().sendMessage("§a[" + getName() + "-" + getDescription().getVersion() + "] Successfully enabled");

		} catch (Throwable exception) {
			failEnable("Couldn't enable", exception);
		}
	}

	protected List<String> extractDefaultConfigIgnoreStartingPaths() {
		return null;
	}

	private void triggerEnableInOtherPlugins() {
		for (GPlugin plugin : PluginUtils.getGPlugins()) {
			if (plugin.isActivated() && !plugin.equals(this)) {
				try {
					plugin.onGPluginEnable(this);  // ez, to avoid having to register an integration just for this
					Integration integ = plugin.getIntegration(getName());
					if (integ != null) {
						if (integ.isActivated()) {
							integ.deactivate();
						}
						integ.activate();
					}
				} catch (Throwable exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	protected void failEnable(String log) { failEnable(log, null); }
	protected void failEnable(String log, Throwable exception) {
		activated = false;
		if (log != null) {
			try {
				mainLogger.error(log, exception);
				mainLogger.error("Disabling plugin - you can re-try with /" + mainCommandName);
				getCommand(mainCommandName).setExecutor(new CommandExecutor() {
					@Override
					public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
						if (retry()) {
							TextGCore.messagePluginReloaded.replace("{plugin}", () -> getName()).send(sender);
						} else {
							sender.sendMessage("§cCould not enable " + getName() + ", please check the console output.");
						}
						return true;
					}
				});
				//mainLogger.saveFileIfPersistent();
				onDisable();
			} catch (Throwable ignored) {
				ignored.printStackTrace();
			}
		}
		//setEnabled(false);
	}

	public final boolean retry() {
		if (!activated) {
			onEnable();
		}
		return activated;
	}

	// ----- disable
	@Override
	public void onDisable() {
		onDisable0(null);
	}

	private void onDisable0(Runnable callback) {
		activated = false;

		// disable plugin
		try {
			disable();
		} catch (Throwable exception) {
			mainLogger.error("Couldn't disable plugin", exception);
		}

		try {
			// unregister listeners
			listeners.clear();
			HandlerList.unregisterAll(this);

			// unregister commands
			commands.forEach((id, command) -> getCommand(command.getName()).setExecutor(null));
			commands.clear();

			// cancel tasks
			tasks.forEach((__, task) -> task.stop());
			tasks.clear();
			Bukkit.getScheduler().cancelTasks(this);  // make sure to cancel all tasks, future as well

			// close and unregister GUIs
			guis.copyValues().forEach(gui -> gui.deactivate(true));

			// unregister bossbars
			bossbars.copyValues().forEach(Bossbar::stop);

			// disable integrations
			integrations.forEach((id, integration) -> integration.deactivate());
			integrations.clear();

			// save and cancel loggers
			loggers.forEachThrowable((__, logger) -> {
				//logger.saveFileIfPersistent();
				logger.stopSaving();
			});
			loggers.clear();
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}

		// save data and stop saving
		data.forEach((id, board) -> {
			board.saveNeeded(BukkitThread.current());
			board.stopSaving();
			board.shutdown();
		});
		data.clear();

		// clear misc
		textFiles.clear();

		// unload config
		configuration = null;

		// callback
		if (callback != null) {
			callback.run();
		}
	}

	protected void disable() throws Throwable {
	}

	// ----- reload
	private transient boolean reloading = false;

	public boolean isReloading() {
		return reloading;
	}

	public final boolean reload(ThrowableRunnable callback) {
		return reload(callback, false);
	}

	private final boolean reload(ThrowableRunnable callback, boolean fromGCore) {
		if (reloading) {
			return true;
		}
		try {
			reloading = true;
			onDisable0(() -> {
				try {
					onEnable();
					if (equals(GCore.inst())) {
						PluginUtils.getGPlugins().forEach(plugin -> {
							try {
								if (!plugin.equals(GCore.inst())) {
									plugin.reload(null, true);
								}
							} catch (Throwable ignored) {}
						});
					} else {
						triggerEnableInOtherPlugins();
					}
					reloading = false;
					if (callback != null) {
						callback.run();
					}
				} catch (Throwable exception) {
					exception.printStackTrace();
					failEnable("Couldn't reload plugin", exception);
					return;
				}
			});
			reloaded(fromGCore);
			return true;
		} catch (Throwable exception) {
			failEnable("Couldn't reload plugin", exception);
			return false;
		}
	}

	protected void reloaded(boolean dueToGCore) {
	}

	// ----- update notification
	public final void notifyUpdate(CommandSender sender) {
		// local is indev
		Integer local = StringUtils.getUniqueVersionNumber(getDescription().getVersion());
		if (local == null) {
			sender.sendMessage("§dYou're using an in-development version of " + getName() + ", please make sure to update when it releases.");
		}
		// can show update notifications
		else if (getConfiguration().updateNotification() && spigotResourceId > 0 && !equals(GCore.inst())) {
			operateAsync(() -> {
				if (Bukkit.isPrimaryThread()) {
					return;  // apparently can't be async, maybe the plugin is stopping or something
				}

				String response = PluginUtils.getOfficialVersion(this);
				// unknown server response
				if (response.isEmpty() || response.equals("unknown_server") || response.equals("Invalid resource") || response.contains("?resource=id")) {
					sender.sendMessage("§cCouldn't fetch the official version of " + getName() + " :(");
				} else {
					// can't parse official
					Integer spigot = StringUtils.getUniqueVersionNumber(response);
					if (spigot == null) {
						sender.sendMessage("§cCouldn't parse the official version '" + response + "' of " + getName() + " :(");
					} else {
						// spigot is latest
						if (spigot > local) {
							if (sender instanceof Player) {
								new JsonMessage()
								.append("§dPlease make sure to ").build()
								.append("§l§5update").setURL("https://www.spigotmc.org/resources/" + getSpigotResourceId() + "/updates/").build()
								.append("§d to " + getName() + " v" + response + " :)").build()
								.send((Player) sender);
							} else {
								sender.sendMessage("§dPlease make sure to §l§5update §dto " + getName() + " v" + response + " :)");
								sender.sendMessage("§dhttps://www.spigotmc.org/resources/" + getSpigotResourceId() + "/updates/");
							}
						}
						// local is latest (indev)
						else if (local > spigot) {
							sender.sendMessage("§dYou're using an in-development version of " + getName() + ", please make sure to update when it releases.");
						}
					}
				}
			});
		}
	}

	// ----- texts
	public final File getDefaultTextsFolder() {
		return getDataFile("/" + dataContainerFolderName + "/default_texts/");
	}

	public final void registerTextFile(TextFile textFile) {
		textFiles.put(textFile.getFilePath(), textFile);
	}

	private boolean readTexts(String langId) throws Throwable {
		final File defaultFolder = getDefaultTextsFolder();
		final File langFolder = new File(getDataFolder() + "/texts/" + langId);
		Bukkit.getConsoleSender().sendMessage("§a[" + getName() + "-" + getDescription().getVersion() + "] Loading texts...");
		try {
			textFiles.forEachThrowable((String __, TextFile<?> textFile) -> {
				// read file
				final File langFile = new File(langFolder + "/" + textFile.getFilePath());
				final LowerCaseHashMap<List<String>> texts = readTextsFromFile(textFile.getValues().keySet(), langFile);

				// mark texts as loaded and get missing texts to add to file
				final LowerCaseHashMap<Text> missing = new LowerCaseHashMap<>(10, 0.75f);
				final Set<String> missingDisplay = new HashSet<>();
				for (String textId : textFile.getValues().keySet()) {
					final Text text = textFile.getValues().get(textId);
					final List<String> loadedLines = texts.get(textId);
					if (loadedLines != null) {
						text.setLines(loadedLines);
					} else {
						missing.put(textId, text);
						missingDisplay.add(textId);
					}
				}

				// load missing texts
				if (!missing.isEmpty()) {
					// read missing texts from defaults (lang + en_US)
					final File defaultFile = new File(defaultFolder + "/" + langId + "/" + textFile.getFilePath());
					final File defaultFileEng = new File(defaultFolder + "/en_US/" + textFile.getFilePath());
					final LowerCaseHashMap<List<String>> missingTexts = readTextsFromFile(missing.keySet(), defaultFile);
					final LowerCaseHashMap<List<String>> missingTextsEng = readTextsFromFile(missing.keySet(), defaultFileEng);

					// load default texts
					missing.forEach((textId, text) -> {
						final List<String> lines = missingTexts.getOrDefault(textId, missingTextsEng.get(textId));
						if (lines != null) {
							text.setLines(lines);
						} else {
							mainLogger.warning("Text " + textId + " doesn't exist in default files (not even in english)");
						}
					});

					// log
					mainLogger.warning("Loaded " + StringUtils.pluralizeAmountDesc("missing text", missing.size()) + " from default lang for " + textFile.getFilePath() + (textFile.getFilePath().contains("editor") && ConfigGCore.dontLogMissingEditorTexts ? "" : " : " + StringUtils.toTextString(", ", missingDisplay)));
				}
			});

			return true;
		} catch (Throwable exception) {
			YMLError causeYML = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			failEnable(causeYML != null ? "Couldn't load texts from " + langFolder.getPath() + " : " + causeYML.getMessage() : (causeConfig != null ? causeConfig.getMessage() : "Couldn't load texts from " + langFolder.getPath()), causeYML != null || causeConfig != null ? null : exception);
			return false;
		}
	}

	private LowerCaseHashMap<List<String>> readTextsFromFile(Set<String> keys, File file) throws Throwable {
		LowerCaseHashMap<List<String>> texts = new LowerCaseHashMap<>(10, 0.75f);
		if (file.exists()) {
			YMLConfiguration config = new YMLConfiguration(this, file);
			for (String key : keys) {
				List<String> value = config.readStringList(key, null);
				if (value != null) {
					texts.put(key, value);
				}
			}
		}
		return texts;
	}

	// ----- commands
	public final Command registerCommand(Command command) {
		commands.put(command.getName(), command);
		return command;
	}

	// ----- data
	public final <T extends Board> T registerDataBoard(T board) {
		// already registered
		if (data.containsKey(board.getId())) {
			try {
				return (T) data.get(board.getId());
			} catch (Throwable ignored) {}
		}
		// register
		data.put(board.getId(), board);
		return board;
	}

	// ----- listeners
	public final void registerListener(String id, Listener listener) {
		// unregistered
		if (listeners.containsKey(id)) {
			HandlerList.unregisterAll(listeners.remove(id));
		}
		// register
		listeners.put(id, listener);
		// start
		if (isActivated()) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}

	public final Listener stopListener(String id) {
		Listener listener = listeners.remove(id);
		if (listener != null) {
			HandlerList.unregisterAll(listener);
		}
		return listener;
	}

	// ----- tasks
	public final Task registerTask(String id, boolean async, int ticksPeriod, ThrowableRunnable runner) {
		// unregistered
		if (tasks.containsKey(id)) {
			tasks.remove(id).stop();
		}
		// register
		Task task = new Task(this, id, async, ticksPeriod, runner);
		tasks.put(id, task);
		// start
		if (isActivated()) {
			task.start();
		}
		// done
		return task;
	}

	public final Task stopTask(String id) {
		Task task = tasks.remove(id);
		if (task != null) {
			task.stop();
		}
		return task;
	}

	// ----- logger
	public final void registerLogger(final Logger logger) {
		// already registered
		if (loggers.containsKey(logger.getId())) {
			return;
		}
		// register
		loggers.put(logger.getId(), logger);
	}

	// ----- GUI
	public final void registerGUI(GUI gui) {
		// already registered
		GUI existing = guis.remove(gui.getId());
		if (existing != null) {
			if (existing.equals(gui)) {
				return;
			}
			existing.deactivate(true);
		}
		// register
		guis.put(gui.getId(), gui);
	}

	public final void unregisterGUI(GUI gui) {
		guis.remove(gui.getId());
	}

	// ----- bossbar
	public final void registerBossbar(Bossbar bar) {
		// already registered
		Bossbar existing = bossbars.remove(bar.getId());
		if (existing != null) {
			if (existing.equals(bar)) {
				return;
			}
			existing.stop();
		}
		// register
		bossbars.put(bar.getId(), bar);
	}

	public final void unregisterBossbar(Bossbar bar) {
		bossbars.remove(bar.getId());
	}

	// ----- integration
	public final Integration registerAndEnableIntegration(Integration integration) {
		// already registered
		if (integrations.containsKey(integration.getPluginName())) {
			integrations.remove(integration.getPluginName()).deactivate();
		}
		// register
		integrations.put(integration.getPluginName(), integration);
		// activate
		try {
			integration.activate();
		} catch (Throwable error) {
			getMainLogger().error("Couldn't enable integration for " + integration.getPluginName(), error);
		}
		return integration;
	}

	public final void unregisterAndDeactivateIntegration(Integration integration) {
		// unregister
		integrations.remove(integration.getPluginName());
		// deactivate
		integration.deactivate();
	}

	// ----- utils

	public final void operate(BukkitThread thread, ThrowableRunnable callable) {
		thread.operate(this, callable);
	}

	public final void operate(BukkitThread thread, ThrowableRunnable callable, Consumer<Throwable> onError) {
		thread.operate(this, callable, onError);
	}

	public final void operateLater(BukkitThread thread, ThrowableRunnable callable, long ticks) {
		thread.operateLater(this, callable, ticks);
	}

	public final void operateLater(BukkitThread thread, ThrowableRunnable callable, Consumer<Throwable> onError, long ticks) {
		thread.operateLater(this, callable, onError, ticks);
	}

	public final void operateSync(ThrowableRunnable callable) {
		operate(BukkitThread.SYNC, callable);
	}

	public final void operateSync(ThrowableRunnable callable, Consumer<Throwable> onError) {
		operate(BukkitThread.SYNC, callable, onError);
	}

	public final void operateSyncLater(ThrowableRunnable callable, long ticks) {
		operateLater(BukkitThread.SYNC, callable, ticks);
	}

	public final void operateSyncLater(ThrowableRunnable callable, Consumer<Throwable> onError, long ticks) {
		operateLater(BukkitThread.SYNC, callable, onError, ticks);
	}

	public final void operateAsync(ThrowableRunnable callable) {
		operate(BukkitThread.ASYNC, callable);
	}

	public final void operateAsync(ThrowableRunnable callable, Consumer<Throwable> onError) {
		operate(BukkitThread.ASYNC, callable, onError);
	}

	public final void operateAsyncLater(ThrowableRunnable callable, long ticks) {
		operateLater(BukkitThread.ASYNC, callable, ticks);
	}

	public final void operateAsyncLater(ThrowableRunnable callable, Consumer<Throwable> onError, long ticks) {
		operateLater(BukkitThread.ASYNC, callable, onError, ticks);
	}

}
