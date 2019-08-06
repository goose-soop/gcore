package com.guillaumevdn.gcore.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.Logger.Level;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.CommandRoot;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.integration.PluginIntegration;
import com.guillaumevdn.gcore.lib.messenger.Messenger;
import com.guillaumevdn.gcore.lib.messenger.Text;
import com.guillaumevdn.gcore.lib.parseable.container.CPItem;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.libs.org.bstats.Metrics;

public abstract class GPlugin extends JavaPlugin implements Listener {

	// ----------------------------------------------------------------------
	// Abstract methods
	// ----------------------------------------------------------------------

	protected abstract boolean preEnable();
	protected abstract boolean innerReload();
	protected abstract boolean enable();
	protected abstract void disable();
	protected abstract void unregisterData();
	public abstract void resetData();
	public abstract YMLConfiguration getConfiguration();

	// ----------------------------------------------------------------------
	// Misc methods
	// ----------------------------------------------------------------------

	// misc
	public boolean isCore() {
		return getName().equals("GCore");
	}

	@Override
	public String toString() {
		return getName();
	}

	// log
	public void log(String message) {
		log(Level.INFO, message);
	}

	public void warning(String message) {
		log(Level.WARNING, message);
	}

	public void error(String message) {
		log(Level.SEVERE, message);
	}

	public void success(String message) {
		log(Level.SUCCESS, message);
	}

	public void debug(String message) {
		log(Level.DEBUG, message);
	}

	public void log(Level level, String message) {
		Logger.log(level, this, message);
	}

	public void messageInfo(Player player, String message) {
		message(player, Messenger.Level.NORMAL_INFO, message);
	}

	public void messageSuccess(Player player, String message) {
		message(player, Messenger.Level.NORMAL_SUCCESS, message);
	}

	public void messageWarning(Player player, String message) {
		message(player, Messenger.Level.SEVERE_INFO, message);
	}

	public void messageError(Player player, String message) {
		message(player, Messenger.Level.SEVERE_ERROR, message);
	}

	public void message(Player player, Messenger.Level level, String message) {
		Messenger.send(player, level, getName(), message);
	}

	// ----------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------

	// settings
	protected boolean silentEnable = false;
	protected boolean authorizationCheck = false;
	protected int spigotResourceId = 0;

	// fields
	private boolean errorEnable = false;
	private Metrics metrics;
	private Map<String, CommandRoot> commands = new HashMap<String, CommandRoot>();
	private List<PluginIntegration> pluginIntegration = new ArrayList<PluginIntegration>();
	private Map<String, Class<? extends PluginIntegration>> awaitingPluginIntegration = new HashMap<String, Class<? extends PluginIntegration>>();

	// get
	public boolean errorEnable() {
		return errorEnable;
	}

	protected void errorEnable(boolean errorEnable) {
		this.errorEnable = errorEnable;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public Map<String, CommandRoot> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	public List<PluginIntegration> getPluginIntegration() {
		return Collections.unmodifiableList(pluginIntegration);
	}

	// ----------------------------------------------------------------------
	// Commands registration and plugin integration
	// ----------------------------------------------------------------------

	public void registerCommand(final CommandRoot command, final Perm adminPermission) {
		registerCommand(command, adminPermission, false);
	}

	public void registerCommand(final CommandRoot command, final Perm adminPermission, final boolean secondary) {
		String commandName = command.getAliases().get(0).toLowerCase();
		if (!commands.containsKey(commandName)) {
			if (!secondary) {
				// add default reload command if doesn't exist
				if (command.getChild("reload") == null) {
					command.addChild(new CommandArgument(this, Utils.asList("reload", "rl"), "reload the plugin", adminPermission, false) {
						@Override
						protected void perform(CommandCall call) {
							long start = System.currentTimeMillis();
							reload();
							GLocale.MSG_GENERIC_RELOAD.send(call.getSender(), "{plugin}", getName(), "{time}", (System.currentTimeMillis() - start) + "ms");
						}
					});
				}
				// add default support command if doesn't exist
				if (command.getChild("support") == null) {
					command.addChild(new CommandArgument(this, Utils.asList("support", "bug", "bugreport", "report", "suggest", "discord"), "get support", adminPermission, false) {
						@Override
						protected void perform(CommandCall call) {
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_INFO, "GCore", "You can get help, report bugs and suggest features on my discord server ! :D");
							Messenger.send(call.getSender(), Messenger.Level.NORMAL_INFO, "GCore", "http://www.guillaumevdn.com/plugins/discord/");
						}
					});
				}
			}
			// register
			PluginCommand cmd = getCommand(commandName);
			cmd.setExecutor(command);
			cmd.setTabCompleter(command);
			commands.put(commandName, command);
		}
	}

	/**
	 * Try to register and enable a plugin integration
	 * @param pluginName the plugin name
	 * @param integrationClass the integration class that will be instanciated (must have a constructor with a single String param)
	 * @return the plugin integration instance, or null if didn't succeed
	 */
	public <T extends PluginIntegration> boolean registerPluginIntegration(String pluginName, Class<T> integrationClass) {
		return registerPluginIntegration(pluginName, integrationClass, null, null);
	}

	/**
	 * Try to register and enable a plugin integration
	 * @param pluginName the plugin name
	 * @param integrationClass the integration class that will be instanciated (must have a constructor with a single String param)
	 * @param minVersion the minimum required version, or null if none
	 * @param maxVersion the maximum allowed version, or null if none
	 * @return the plugin integration instance, or null if didn't succeed
	 */
	public <T extends PluginIntegration> boolean registerPluginIntegration(String pluginName, Class<T> integrationClass, ServerVersion minVersion, ServerVersion maxVersion) {
		// min version
		if (minVersion != null && ServerVersion.CURRENT.isLessThan(minVersion)) {
			debug("Not integrating " + pluginName + " (minimum required version is " + minVersion.getName() + ")");
			return false;
		}
		// max version
		if (maxVersion != null && ServerVersion.CURRENT.isGreaterThan(maxVersion)) {
			debug("Not integrating " + pluginName + " (maximum allowed version is " + maxVersion.getName() + ")");
			return false;
		}
		// not installed
		Plugin plugin = Utils.getPlugin(pluginName);
		if (plugin == null) {
			debug("Not integrating " + pluginName + " (not installed)");
			return false;
		}
		// register
		success("Registered " + pluginName + " integration");
		// attempt to activate
		try {
			enableIntegrationNoCatch(pluginName, integrationClass);
		}
		// register to attempt when it's activated
		catch (Throwable exception) {
			exception.printStackTrace();
			warning("Couldn't integrate " + pluginName + ", a new attempt will be made when it's enabled/reloaded");
			if (getName().equals("QuestCreator")) warning("If this causes quests to not load on plugin activation, try to enable the delayed activation in config.yml");
			awaitingPluginIntegration.put(pluginName, integrationClass);
		}
		return true;
	}

	private boolean enableIntegration(String pluginName, Class<? extends PluginIntegration> integrationClass) {
		try {
			enableIntegrationNoCatch(pluginName, integrationClass);
			return true;
		} catch (Throwable exception) {
			exception.printStackTrace();
			error("Couldn't enable " + pluginName + " integration");
			return false;
		}
	}

	private void enableIntegrationNoCatch(String pluginName, Class<? extends PluginIntegration> integrationClass) throws Throwable {
		PluginIntegration integration = integrationClass.getConstructor(String.class).newInstance(pluginName);
		integration.enable();
		pluginIntegration.add(integration);
		success("Integrated " + pluginName);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PluginDisableEvent event) {
		if (!event.getPlugin().equals(this)) {
			for (PluginIntegration integration : pluginIntegration) {
				if (integration.getPluginName().equalsIgnoreCase(event.getPlugin().getName())) {
					integration.disable();
					pluginIntegration.remove(integration);
					awaitingPluginIntegration.put(integration.getPluginName(), integration.getClass());
					success("Disabled " + integration.getPluginName() + " integration");
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PluginEnableEvent event) {
		if (!event.getPlugin().equals(this)) {
			for (String plugin : awaitingPluginIntegration.keySet()) {
				if (plugin.equalsIgnoreCase(event.getPlugin().getName())) {
					if (enableIntegration(plugin, awaitingPluginIntegration.get(plugin))) {
						awaitingPluginIntegration.remove(plugin);
					}
					break;
				}
			}
		}
	}

	// ----------------------------------------------------------------------
	// On enable
	// ----------------------------------------------------------------------

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		// error while enabling core
		if (GCore.inst().errorEnable()) {
			cancelEnable("An error occured while enabling GCore");
			return;
		}

		// check if all dependencies are enabled
		for (String depend : getDescription().getDepend()) {
			if (!Utils.isPluginEnabled(depend)) {
				cancelEnable("Plugin " + depend + " is required but isn't enabled");
				return;
			}
		}

		// pre enable
		if (!preEnable()) {
			return;
		}

		// ensure authorization
		if (authorizationCheck) {
			if (!Utils.ensureAuthorization(this)) {
				cancelEnable(null);
				return;
			}
		}

		// enable
		try {
			enable();
			Bukkit.getPluginManager().registerEvents(this, this);
			debug("Successfully enabled");
		} catch (Throwable exception) {
			exception.printStackTrace();
			cancelEnable("an unknown error occured while enabling");
			return;
		}

		// metrics
		try {
			metrics = new Metrics(this);
			debug("Using metrics : yes");
		} catch (Throwable exception) {
			exception.printStackTrace();
			error("Could not initialize metrics");
		}

		// log
		success(getDescription().getName() + " v" + getDescription().getVersion() + " is now enabled (took " + (System.currentTimeMillis() - start) + "ms) !" + (spigotResourceId > 0 && !isCore() ? " If you appreciate this plugin, you can support the author by rating the plugin 5-stars at www.spigotmc.org, being active on the community discord or share the plugin page to your friends ! <3" : ""));
	}

	protected void cancelEnable(String reason) {
		if (reason != null) error("Aborting plugin activation : " + reason);
		errorEnable = true;
		Bukkit.getPluginManager().disablePlugin(this);
	}

	// ----------------------------------------------------------------------
	// Reload
	// ----------------------------------------------------------------------

	public void reload() {
		// clear all item cache
		CPItem.clearAllCache();
		// integration
		for (PluginIntegration integration : pluginIntegration) {
			try {
				integration.disable();
				integration.enable();
				success("Reloaded " + integration.getPluginName() + " integration");				
			} catch (Throwable exception) {
				exception.printStackTrace();
				error("Couldn't reload " + integration.getPluginName() + " integration");
			}
		}
		// reload inner
		innerReload();
	}

	public void reloadLocale(File localeFile) {
		reloadLocale(Text.values(localeFile), localeFile);
	}

	public void reloadLocale(List<Text> values, File localeFile) {
		// load locale messages
		YMLConfiguration locale = new YMLConfiguration(this, localeFile, null, true, true);
		for (Text msg : values) {
			for (String lang : locale.getKeysForSection(msg.getId(), false)) {
				msg.set(lang, locale.getObject(msg.getId() + "." + lang, null));
			}
		}
		// save locale messages
		locale = new YMLConfiguration(this, localeFile, null, true, false);
		for (Text msg : values) {
			Map<String, List<String>> langs = msg.getLangs();
			for (String lang : Utils.asSortedList(langs.keySet(), String.CASE_INSENSITIVE_ORDER)) {
				List<String> lines = langs.get(lang);
				if (lines.isEmpty()) {
					warning("Couldn't initialize locale message " + msg.getId() + "." + lang + " because it's empty");
				} else {
					locale.set(msg.getId() + "." + lang, lines.size() > 1 ? Utils.replaceAll(lines, "§", "&") : lines.get(0).replace("§", "&"));
				}
			}
		}
		locale.save();
		success("Loaded " + values.size() + " text" + Utils.getPlural(values.size()));
	}

	// ----------------------------------------------------------------------
	// On disable
	// ----------------------------------------------------------------------

	@Override
	public void onDisable() {
		// error while enabling
		if (errorEnable) {
			return;
		}

		// disable
		try {
			disable();
			debug("Successfully disabled");
		} catch (Throwable exception) {
			exception.printStackTrace();
			error("An unknown error occured while disabling");
		}

		// events
		HandlerList.unregisterAll((JavaPlugin) this);

		// cancel all tasks
		Bukkit.getScheduler().cancelTasks(this);
		for (BukkitTask task : Utils.asList(Bukkit.getScheduler().getPendingTasks())) {
			if (equals(task.getOwner())) {
				task.cancel();
			}
		}
		for (BukkitWorker worker : Utils.asList(Bukkit.getScheduler().getActiveWorkers())) {
			if (equals(worker.getOwner())) {
				Bukkit.getScheduler().cancelTask(worker.getTaskId());
			}
		}

		// close GUIs
		GUI.unregisterAll(this);

		// integration
		for (PluginIntegration integration : pluginIntegration) {
			integration.disable();
			debug("Unregistered " + integration.getPluginName() + " integration");
		}
		pluginIntegration.clear();

		// data
		unregisterData();

		// unregister commands
		for (CommandRoot command : commands.values()) {
			getCommand(command.getAliases().get(0)).setExecutor(null);
		}
		commands.clear();

		// log
		success(getDescription().getName() + " v" + getDescription().getVersion() + " is now disabled.");
	}

}
