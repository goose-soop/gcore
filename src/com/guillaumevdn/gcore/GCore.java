package com.guillaumevdn.gcore;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.commands.CommandDataExport;
import com.guillaumevdn.gcore.commands.CommandDataReset;
import com.guillaumevdn.gcore.commands.CommandItemMat;
import com.guillaumevdn.gcore.commands.CommandItemNbt;
import com.guillaumevdn.gcore.commands.CommandItemRead;
import com.guillaumevdn.gcore.commands.CommandItemSetdura;
import com.guillaumevdn.gcore.commands.CommandItemSetname;
import com.guillaumevdn.gcore.commands.CommandItemSetunbreakable;
import com.guillaumevdn.gcore.commands.CommandNpcHide;
import com.guillaumevdn.gcore.commands.CommandNpcSetEquipment;
import com.guillaumevdn.gcore.commands.CommandNpcSetName;
import com.guillaumevdn.gcore.commands.CommandNpcSetStatus;
import com.guillaumevdn.gcore.commands.CommandNpcShow;
import com.guillaumevdn.gcore.commands.CommandNpcTeleport;
import com.guillaumevdn.gcore.commands.CommandPlugins;
import com.guillaumevdn.gcore.commands.CommandSetuserprofile;
import com.guillaumevdn.gcore.data.GDataManager;
import com.guillaumevdn.gcore.integration.HeadDatabaseIntegration;
import com.guillaumevdn.gcore.integration.economy.EconomyHandler;
import com.guillaumevdn.gcore.integration.permission.PermissionHandler;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandRoot;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.npc.NpcManager;
import com.guillaumevdn.gcore.lib.util.ServerImplementation;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;
import com.guillaumevdn.gcore.lib.util.input.ItemInput;
import com.guillaumevdn.gcore.lib.util.input.LocationInput;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;

import sun.util.calendar.ZoneInfo;

// FIXME : add fucking gravity to NPCs
public class GCore extends GPlugin {

	// ------------------------------------------------------------
	// Instance and constructor
	// ------------------------------------------------------------

	private static GCore instance;

	public GCore() {
		instance = this;
	}

	public static GCore inst() {
		return instance;
	}

	// ------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------

	// gson
	public static Gson GSON = null;
	public static Gson UNPRETTY_GSON = null;

	// storage
	private File dataRootFolder = null;
	private File userDataRootFolder = null;

	// settings
	private String activeLocaleLang = "en_US";
	private boolean updateCheck = true;
	private TimeZone calendarTimeZone = null;
	private boolean cacheParsedItems = true;

	// misc
	private EconomyHandler economyHandler = new EconomyHandler();
	private PermissionHandler permissionHandler = new PermissionHandler();
	private NpcManager npcManager = null;
	private HeadDatabaseIntegration headDatabaseIntegration = null;
	private Map<Player, ChatInput> chatInputs = new HashMap<Player, ChatInput>();
	private Map<Player, LocationInput> locationInputs = new HashMap<Player, LocationInput>();
	private Map<Player, ItemInput> itemInputs = new HashMap<Player, ItemInput>();

	// get
	public EconomyHandler getEconomyHandler() {
		return economyHandler;
	}

	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	public File getDataRootFolder() {
		return dataRootFolder;
	}

	public File getUserDataRootFolder() {
		return userDataRootFolder;
	}

	public String getActiveLocaleLang() {
		return activeLocaleLang;
	}

	public boolean updateCheck() {
		return updateCheck;
	}
	
	public boolean cacheParsedItems() {
		return cacheParsedItems;
	}

	public NpcManager getNpcManager() {
		return npcManager;
	}

	public HeadDatabaseIntegration getHeadDatabaseIntegration() {
		return headDatabaseIntegration;
	}

	public void setHeadDatabaseIntegration(HeadDatabaseIntegration headDatabaseIntegration) {
		this.headDatabaseIntegration = headDatabaseIntegration;
	}

	public Map<Player, ChatInput> getChatInputs() {
		return chatInputs;
	}

	public Map<Player, LocationInput> getLocationInputs() {
		return locationInputs;
	}

	public Map<Player, ItemInput> getItemInputs() {
		return itemInputs;
	}

	/**
	 * @return the configured calendar time zone, or null if none configured
	 */
	public TimeZone getCalendarTimeZone() {
		return calendarTimeZone;
	}

	/**
	 * @return the current calendar instance, using {@link #getCalendarTimeZone()} or the server host's time zone if none
	 */
	public Calendar getCalendarInstance() {
		return calendarTimeZone != null ? Calendar.getInstance(calendarTimeZone) : Calendar.getInstance();
	}

	// ------------------------------------------------------------
	// Data and configuration
	// ------------------------------------------------------------

	private GDataManager dataManager = null;
	private YMLConfiguration configuration = null;

	private boolean allowCustomMaterials = false;

	@Override
	public YMLConfiguration getConfiguration() {
		return configuration;
	}

	public GDataManager getData() {
		return dataManager;
	}

	@Override
	protected void unregisterData() {
		dataManager.disable();
	}

	@Override
	public void resetData() {
		dataManager.reset();
	}

	public boolean allowCustomMaterials() {
		return allowCustomMaterials;
	}

	// ------------------------------------------------------------
	// ACTIVATION
	// ------------------------------------------------------------

	@Override
	protected boolean preEnable() {
		// spigot resource id
		spigotResourceId = 24180;
		// success
		return true;
	}

	@Override
	protected boolean innerReload() {
		// init data folder
		(this.dataRootFolder = new File(getDataFolder() + "/data/")).mkdirs();
		(this.userDataRootFolder = new File(getDataFolder() + "/userdata/")).mkdirs();

		// configuration
		this.configuration = new YMLConfiguration(this, new File(getDataFolder() + "/config.yml"), "config.yml", false, true);
		this.allowCustomMaterials = getConfiguration().getBoolean("allow_custom_materials", false);
		this.cacheParsedItems = getConfiguration().getBoolean("cache_parsed_items", true);
		if (getConfiguration().contains("time_zone")) {
			String raw = getConfiguration().getString("time_zone", null);
			try {
				calendarTimeZone = TimeZone.getTimeZone(raw);
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
			if (calendarTimeZone == null || calendarTimeZone.equals(new ZoneInfo("GMT", 0))) {
				calendarTimeZone = null;
				error("Couldn't parse time zone '" + raw + "', using server host's time zone");
			}
		} else {
			calendarTimeZone = null;
		}
		success("Loaded config.yml");

		// locale
		this.activeLocaleLang = getConfiguration().getString("locale_lang", "en_US");
		success("Using locale " + activeLocaleLang + " (with default backup en_US)");

		// load locale file
		reloadLocale(GLocale.file);

		// data manager
		if (dataManager == null) {
			BackEnd backend = getConfiguration().getEnumValue("data.backend", BackEnd.class, BackEnd.JSON);
			if (backend == null) {
				backend = BackEnd.JSON;
			}
			this.dataManager = new GDataManager(backend);
			dataManager.enable();
		}

		// npc manager
		if (npcManager == null) {
			boolean hasProtocols = false;
			try {
				hasProtocols = NpcProtocols.INSTANCE != null;
			} catch (Throwable ignored) {
			}
			try {
				if (hasProtocols && Utils.getPlugin("ProtocolLib") != null) {
					(npcManager = new NpcManager()).enable();
					success("Enabled NPC manager with ProtocolLib");
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
			}
			if (npcManager == null) {
				debug("Couldn't enable NPC manager with ProtocolLib");
			}
		}

		// reload NPCs data
		if (npcManager != null) {
			npcManager.reload();
		}

		// auto update
		updateCheck = getConfiguration().getBoolean("update_check", true);
		debug("Allowing update check : " + updateCheck);

		// return
		return true;
	}

	@Override
	protected boolean enable() {
		// load some buggy classes ? ;-;
		for (String className : Utils.asList("com.guillaumevdn.gcore.lib.data.mysql.Query", "com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI", "com.guillaumevdn.gcore.lib.util.Handler")) {
			try {
				getClassLoader().loadClass(className);
			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		}

		// reload inner (mainly settings)
		innerReload();

		// server version
		debug("Detected server version : " + ServerVersion.CURRENT.getName() + (ServerVersion.CURRENT.equals(ServerVersion.UNSUPPORTED) ? " - this version isn't officially supported and plugins might not work as expected" : ""));
		debug("Detected server implementation : " + ServerImplementation.CURRENT.toString() + (!ServerImplementation.CURRENT.equals(ServerImplementation.SPIGOT) ? " - this implementation isn't officially supported and plugins might not work as expected" : ""));

		// init compat
		Compat.INSTANCE.init();

		// gson
		GSON = Utils.createGsonBuilder().setPrettyPrinting().create();
		UNPRETTY_GSON = Utils.createGsonBuilder().create();

		// integration
		initEconomy();
		initPermission();
		registerPluginIntegration("HeadDatabase", HeadDatabaseIntegration.class);

		// register command
		CommandRoot root = new CommandRoot(this, Utils.asList("gcore"), null, GPerm.GCORE_ADMIN, false);
		registerCommand(root, GPerm.GCORE_ADMIN);
		// data commands
		CommandArgument data = new CommandArgument(this, Utils.asList("data"), "data-related commands", GPerm.GCORE_ADMIN, false);
		root.addChild(data);
		data.addChild(new CommandDataExport());
		data.addChild(new CommandDataReset());
		// item commands
		CommandArgument item = new CommandArgument(this, Utils.asList("item"), "item-related commands", GPerm.GCORE_ADMIN, false);
		root.addChild(item);
		item.addChild(new CommandItemRead());
		item.addChild(new CommandItemSetdura());
		item.addChild(new CommandItemSetname());	
		item.addChild(new CommandItemSetunbreakable());
		item.addChild(new CommandItemMat());
		item.addChild(new CommandItemNbt());
		// item commands
		CommandArgument npc = new CommandArgument(this, Utils.asList("npc"), "npc-related commands", GPerm.GCORE_NPC_MANIPULATE, false);
		root.addChild(npc);
		npc.addChild(new CommandNpcHide());
		npc.addChild(new CommandNpcSetEquipment());
		npc.addChild(new CommandNpcSetName());
		npc.addChild(new CommandNpcSetStatus());
		npc.addChild(new CommandNpcShow());
		npc.addChild(new CommandNpcTeleport());
		// other commands
		root.addChild(new CommandPlugins());
		root.addChild(new CommandSetuserprofile());
		// root.addChild(new CommandPathfindingTest());

		// listeners
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);

		return true;
	}

	private void initEconomy() {
		if (economyHandler.init()) {
			debug("Initialized economy handler with " + economyHandler.getUtils().getClass().getSimpleName().replace("Utils", ""));
		} else {
			warning("No economy handler was found, some features might not work as intended");
		}
	}

	private void initPermission() {
		if (permissionHandler.init()) {
			debug("Initialized permission handler with " + permissionHandler.getUtils().getClass().getSimpleName().replace("Utils", ""));
		} else {
			warning("No permission handler was found, some features might not work as intended");
		}
	}

	// ------------------------------------------------------------
	// On disable
	// ------------------------------------------------------------

	@Override
	protected void disable() {
		// npc manager
		if (npcManager != null) {
			npcManager.disable(false);
			npcManager = null;
			debug("Disabled NPC manager with ProtocolLib");
		}
		// disable all other plugins
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin.isEnabled() && plugin.getDescription().getDepend().contains("GCore")) {
				try {
					Bukkit.getPluginManager().disablePlugin(plugin);
				} catch (Throwable exception) {
					exception.printStackTrace();
					error("Couldn't disable plugin " + plugin.getName());
				}
			}
		}
	}

	// ------------------------------------------------------------
	// ProtocolLib
	// ------------------------------------------------------------

	@EventHandler
	public void event(PluginDisableEvent event) {
		// ProtocolLib
		if (event.getPlugin().getName().equals("ProtocolLib")) {
			if (npcManager != null) {
				npcManager.disable(isEnabled());
				npcManager = null;
				debug("Disabled NPC manager with ProtocolLib");
			}
		}
		// economy and permissions
		else {
			if (economyHandler.isPluginHandled(event.getPlugin().getName())) {
				initEconomy();
			}
			if (permissionHandler.isPluginHandled(event.getPlugin().getName())) {
				initPermission();
			}
		}
	}

	@EventHandler
	public void event(PluginEnableEvent event) {
		// ProtocolLib
		if (event.getPlugin().getName().equals("ProtocolLib")) {
			if (npcManager == null) {
				(npcManager = new NpcManager()).enable();
				debug("Enabled NPC manager with ProtocolLib");
			}
		}
		// economy and permissions
		else {
			if (economyHandler.isPluginHandled(event.getPlugin().getName())) {
				initEconomy();
			}
			if (permissionHandler.isPluginHandled(event.getPlugin().getName())) {
				initPermission();
			}
		}
	}

}
