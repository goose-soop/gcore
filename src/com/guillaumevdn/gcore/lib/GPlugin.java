package com.guillaumevdn.gcore.lib;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.chat.JsonMessage;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.command.Command;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.data.Board;
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

/**
 * @author GuillaumeVDN
 */
public abstract class GPlugin<C extends GPluginConfig, P extends PermissionContainer> extends JavaPlugin {

	private final int spigotResourceId;
	private final List<Class<? extends Migration>> migrations;
	private final Class<C> configurationClass;
	private C configuration = null;
	private final Class<P> permissionContainerClass;
	private P permissionContainer = null;
	private final LowerCaseHashMap<TextFile> textFiles = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Board> data = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Command> commands = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Listener> listeners = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Task> tasks = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Logger> loggers = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<GUI> guis = new LowerCaseHashMap<>();
	private final LowerCaseHashMap<Integration> integrations = new LowerCaseHashMap<>();
	private Logger mainLogger = new Logger(this, getName() + "-" + getDescription().getVersion(), true, true, 10000, false);  // define it temporarily for start
	private boolean activated = false;
	private Gson gson = createGsonBuilder().create();
	private Gson prettyGson = createGsonBuilder().setPrettyPrinting().create();

	public GsonBuilder createGsonBuilder() {
		return FileUtils.createGsonBuilder();
	}

	public GPlugin(int spigotResourceId, Class<C> configurationClass, Class<P> permissionContainerClass, Class<? extends Migration>... migrations) {
		this.spigotResourceId = spigotResourceId;
		this.migrations = CollectionUtils.asUnmodifiableList(migrations);
		this.configurationClass = configurationClass;
		this.permissionContainerClass = permissionContainerClass;
	}

	// get
	public int getSpigotResourceId() {
		return spigotResourceId;
	}

	public final Class<C> getConfigurationClass() {
		return configurationClass;
	}

	public final C getConfiguration() {
		return configuration;
	}

	public final Class<P> getPermissionContainerClass() {
		return permissionContainerClass;
	}

	public final P getPermissionContainer() {
		return permissionContainer;
	}

	public final YMLConfiguration loadConfigurationFile(String path) {
		return new YMLConfiguration(this, getDataFile(path));
	}

	public final LowerCaseHashMap<TextFile> getTexts() {
		return textFiles;
	}

	public final LowerCaseHashMap<Board> getData() {
		return data;
	}

	public final File getDataFile(String path) {
		return new File(getDataFolder() + "/" + path);
	}

	public final Map<String, Listener> getListeners() {
		return Collections.unmodifiableMap(listeners);
	}

	public final Map<String, Task> getTasks() {
		return Collections.unmodifiableMap(tasks);
	}

	public final Map<String, Logger> getLoggers() {
		return Collections.unmodifiableMap(loggers);
	}

	public final Map<String, GUI> getGuis() {
		return Collections.unmodifiableMap(guis);
	}

	public final Map<String, Integration> getIntegrations() {
		return Collections.unmodifiableMap(integrations);
	}

	public final Integration getIntegration(String pluginName) {
		return integrations.get(pluginName);
	}

	public final Logger getLogger(String id) {
		return loggers.get(id);
	}

	public final Logger getMainLogger() {
		return mainLogger;
	}

	public final boolean isActivated() {
		return activated;
	}

	public final Gson getGson() {
		return gson;
	}

	public final Gson getPrettyGson() {
		return prettyGson;
	}

	// enable
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

	protected abstract File getDefaultTextsFolder();

	protected void registerData() {
	}

	protected void preEnable() throws Throwable {
	}

	protected void enable() throws Throwable {
	}

	protected void registerAndEnableIntegrations() {
	}

	@Override
	public void onEnable() {
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
			// register types
			registerTypes();
			// register texts
			try {
				registerTexts();
			} catch (Throwable exception) {
				failEnable("Couldn't register texts", exception);
				return;
			}
			// save default configs
			try {
				int savedConfig = new ResourceExtractor(this, new File(getDataFolder() + "/"), "resources/").extract(false, true);
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
					if (defaultFolder.exists()) {
						defaultFolder.delete();
					}
					defaultFolder.mkdirs();
					new ResourceExtractor(this, defaultFolder, "resources/texts").extract(true, true);
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
			// register and enable integrations before configuration if this is not GCore : things in integrations might be needed to load config
			if (!GCore.inst().equals(this)) {
				try {
					registerAndEnableIntegrations();
				} catch (Throwable exception) {
					failEnable("Couldn't enable integrations", exception);
					return;
				}
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
			// register and enable integrations after configuration if this is GCore : position/time frame types need CommonMats to load
			if (GCore.inst().equals(this)) {
				try {
					registerAndEnableIntegrations();
				} catch (Throwable exception) {
					failEnable("Couldn't enable integrations", exception);
					return;
				}
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
			// register main logger
			registerLogger(mainLogger = new Logger(this, getName() + "-" + getDescription().getVersion(), getConfiguration().logMainConsole(), getConfiguration().logMainFile(), 10000));
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
			registerListener(new Listener() {
				@EventHandler(priority = EventPriority.LOWEST)
				public void event(PlayerJoinEvent event) {
					if (permissionContainer.getAdminPermission().has(event.getPlayer())) {
						notifyUpdate(event.getPlayer());
					}
				}
			});
			// start logger tasks
			loggers.values().forEach(Logger::startSaving);
			// initialize data boards
			data.values().forEach(board -> board.initialize(BukkitThread.ASYNC, () -> board.startSaving()));
			// register commands
			commands.values().forEach(command -> getCommand(command.getName()).setExecutor(command));
			// register listeners
			listeners.values().forEach(listener -> {
				Bukkit.getPluginManager().registerEvents(listener, this);
			});
			// start tasks
			tasks.values().forEach(Task::start);
			// mark as activated
			activated = true;
			Bukkit.getConsoleSender().sendMessage("§a[" + getName() + "-" + getDescription().getVersion() + "] Successfully enabled");
		} catch (Throwable exception) {
			failEnable("Couldn't enable", exception);
		}
	}

	protected void failEnable(String log) { failEnable(log, null); }
	protected void failEnable(String log, Throwable exception) {
		activated = false;
		if (log != null) {
			try {
				mainLogger.error(log, exception);
				mainLogger.error("Disabling plugin");
				mainLogger.saveFileIfPersistent();
			} catch (Throwable ignored) {
				ignored.printStackTrace();
			}
		}
		setEnabled(false);
	}

	// disable
	@Override
	public void onDisable() {
		onDisable0(BukkitThread.SYNC, null);
	}

	private void onDisable0(BukkitThread dataSaving, Runnable callback) {
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
			CollectionUtils.clearForEach(commands, (id, command) -> getCommand(command.getName()).setExecutor(null));
			// cancel tasks
			CollectionUtils.clearForEach(tasks.values(), Task::stop);
			Bukkit.getScheduler().cancelTasks(this); // make sure to cancel all tasks, future as well
			// close and unregister GUIs
			CollectionUtils.asList(guis.values()).forEach(gui -> gui.deactivate(true));
			// disable integrations
			CollectionUtils.clearForEach(integrations, (id, integration) -> integration.deactivate());
			// save and cancel loggers
			CollectionUtils.clearForEachThrowableIgnore(loggers.values(), logger -> {
				logger.saveFileIfPersistent();
				logger.stopSaving();
			});
		} catch (Throwable ignored) {}
		// save data and stop saving
		List<String> remainingToSave = data.entrySet().stream().filter(entry -> entry.getValue().mustSaveSomething()).map(entry -> entry.getKey()).collect(Collectors.toList());
		CollectionUtils.clearForEach(data, (id, board) -> {
			board.saveNeeded(dataSaving, () -> {
				// callback if no more data boards to save
				remainingToSave.remove(id);
				if (callback != null && remainingToSave.isEmpty()) {
					callback.run();
				}
			});
			board.stopSaving();
		});
		// clear misc
		textFiles.clear();
		// unload config
		configuration = null;
		// callback instantly if no data boards
		if (callback != null && remainingToSave.isEmpty()) {
			callback.run();
		}
	}

	protected void disable() throws Throwable {
	}

	// reload
	private transient boolean reloading = false;

	public boolean isReloading() {
		return reloading;
	}

	public final boolean reload(ThrowableRunnable callback) {
		if (reloading) {
			return true;
		}
		try {
			reloading = true;
			onDisable0(BukkitThread.ASYNC, () -> {
				try {
					onEnable();
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
			return true;
		} catch (Throwable exception) {
			failEnable("Couldn't reload plugin", exception);
			return false;
		}
	}

	// update notification
	public final void notifyUpdate(CommandSender sender) {
		// local is indev
		Integer local = StringUtils.getUniqueVersionNumber(getDescription().getVersion());
		if (local == null) {
			sender.sendMessage("§dYou're using an in-development version of " + getName() + ", please make sure to update when it's released.");
		}
		// can show update notifications
		else if (getConfiguration().updateNotification() && spigotResourceId > 0 && !equals(GCore.inst())) {
			BukkitThread.ASYNC.operate(() -> {
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
								.append("§d§lupdate").setURL("https://www.spigotmc.org/resources/" + getSpigotResourceId() + "/updates/").build()
								.append(" §dto " + getName() + " v" + response + " :)").build()
								.send((Player) sender);
							} else {
								sender.sendMessage("§dPlease make sure to §d§lupdate §dto " + getName() + " v" + response + " :)");
								sender.sendMessage("§dhttps://www.spigotmc.org/resources/" + getSpigotResourceId() + "/updates/");
							}
						}
						// local is latest (indev)
						else if (local > spigot) {
							sender.sendMessage("§dYou're using an in-development version of " + getName() + ", please make sure to update when it's released.");
						}
					}
				}
			});
		}
	}

	// texts
	public final void registerTextFile(TextFile textFile) {
		textFiles.put(textFile.getFilePath(), textFile);
	}

	private boolean readTexts(String langId) throws Throwable {
		File defaultFolder = getDefaultTextsFolder();
		File langFolder = new File(getDataFolder() + "/texts/" + langId);
		Bukkit.getConsoleSender().sendMessage("§a[" + getName() + "-" + getDescription().getVersion() + "] Loading texts...");
		try {
			if (langFolder.isDirectory()) {
				for (TextFile<?> textFile : textFiles.values()) {
					// read file
					File langFile = new File(langFolder + "/" + textFile.getFilePath());
					LowerCaseHashMap<List<String>> texts = readTextsFromFile(textFile.getValues().keySet(), langFile);
					// mark texts as loaded and get missing texts to add to file
					LowerCaseHashMap<Text> missing = new LowerCaseHashMap<>();
					Set<String> missingDisplay = new HashSet<>();
					for (String textId : textFile.getValues().keySet()) {
						Text text = textFile.getValues().get(textId);
						List<String> loadedLines = texts.get(textId);
						if (loadedLines != null) {
							text.setLines(loadedLines);
						} else {
							missing.put(textId, text);
							missingDisplay.add(textId);
						}
					}
					// load missing texts
					if (!missing.isEmpty()) {
						// read missing texts from file
						String defaultLang = langId;
						File defaultFile = new File(defaultFolder + "/" + defaultLang + "/" + textFile.getFilePath());
						if (!defaultFile.exists()) {
							defaultFile = new File(defaultFolder + "/" + (defaultLang = "en_US") + "/" + textFile.getFilePath());
						}
						if (defaultFile.exists()) {
							// load default texts
							LowerCaseHashMap<List<String>> missingTexts = readTextsFromFile(missing.keySet(), defaultFile);
							if (!missingTexts.isEmpty()) {
								missingTexts.forEach((missingTextId, defaultLines) -> {
									Text text = missing.get(missingTextId);
									text.setLines(defaultLines);
								});
							}
							// log
							mainLogger.warning("Loaded " + StringUtils.pluralizeAmountDesc("missing text", missing.size()) + " from default lang " + defaultLang + " for " + textFile.getFilePath() + (textFile.getFilePath().contains("editor") && ConfigGCore.dontLogMissingEditorTexts ? "" : " : " + StringUtils.toTextString(", ", missingDisplay)));
						}
					}
					// save missing texts
					// - actually don't do it ; it would pollute incomplete text files with english text
					/*if (!missing.isEmpty()) {
						// read missing texts from file
						File defaultFile = new File(defaultFolder + "/" + langId + "/" + textFile.getFilePath());
						if (!defaultFile.exists()) {
							defaultFile = new File(defaultFolder + "/en_US/" + textFile.getFilePath());
						}
						if (defaultFile.exists()) {
							// load default texts and write missing texts to file
							LowerCaseHashMap<List<String>> missingTexts = readTextsFromFile(missing.keySet(), defaultFile);
							if (!missingTexts.isEmpty()) {
								YMLConfiguration config = new YMLConfiguration(this, langFile);
								config.getBackingYML().getBase().addComment(CollectionUtils.asList("", "-------------------- UPDATED LINES --------------------", ""));
								missingTexts.forEach((missingTextId, defaultLines) -> {
									Text text = missing.get(missingTextId);
									text.setLines(defaultLines);
									config.write(missingTextId, defaultLines.size() == 1 ? defaultLines.get(0) : defaultLines);
								});
								config.save();
							}
							// log
							mainLogger.info("Saved " + StringUtils.pluralizeAmountDesc("missing text", missing.size()) + " in " + langFile + " : " + StringUtils.toTextString(", ", missingDisplay));
						}
					}*/
				}
			}
			return true;
		} catch (Throwable exception) {
			YMLError causeYML = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			failEnable(causeYML != null ? "Couldn't load texts from " + langFolder.getPath() + " : " + causeYML.getMessage() : (causeConfig != null ? causeConfig.getMessage() : "Couldn't load texts from " + langFolder.getPath()), causeYML != null || causeConfig != null ? null : exception);
			return false;
		}
	}

	private LowerCaseHashMap<List<String>> readTextsFromFile(Set<String> keys, File file) throws Throwable {
		LowerCaseHashMap<List<String>> texts = new LowerCaseHashMap<>();
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

	// commands
	public final Command registerCommand(Command command) {
		commands.put(command.getName(), command);
		return command;
	}

	// data
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

	// listeners
	public final void registerListener(Listener listener) {
		registerListener(listener.getClass().getName(), listener);
	}

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

	public final Listener stopListener(Class<? extends Listener> clazz) {
		return stopListener(clazz.getName());
	}

	public final Listener stopListener(String id) {
		Listener listener = listeners.remove(id);
		if (listener != null) {
			HandlerList.unregisterAll(listener);
		}
		return listener;
	}

	// tasks
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

	// logger
	public final void registerLogger(final Logger logger) {
		// already registered
		if (loggers.containsKey(logger.getId())) {
			return;
		}
		// register
		loggers.put(logger.getId(), logger);
	}

	// GUI
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

	// integration
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

}
