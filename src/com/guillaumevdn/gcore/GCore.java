package com.guillaumevdn.gcore;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.command.GcoreBlockMat;
import com.guillaumevdn.gcore.command.GcoreBlockMaterial;
import com.guillaumevdn.gcore.command.GcoreBoardConvert;
import com.guillaumevdn.gcore.command.GcoreBoardElementPrint;
import com.guillaumevdn.gcore.command.GcoreBoardStringElementPrint;
import com.guillaumevdn.gcore.command.GcoreBoardUUIDElementPrint;
import com.guillaumevdn.gcore.command.GcoreExport;
import com.guillaumevdn.gcore.command.GcoreImpl;
import com.guillaumevdn.gcore.command.GcoreItemRead;
import com.guillaumevdn.gcore.command.GcoreItemReadChat;
import com.guillaumevdn.gcore.command.GcoreItemReadClick;
import com.guillaumevdn.gcore.command.GcoreItemSetNBTString;
import com.guillaumevdn.gcore.command.GcoreNpcReset;
import com.guillaumevdn.gcore.command.GcoreNpcResetEveryone;
import com.guillaumevdn.gcore.command.GcorePermsReset;
import com.guillaumevdn.gcore.command.GcorePlugins;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.integration.citizens.IntegrationInstanceCitizens;
import com.guillaumevdn.gcore.integration.deluxechat.IntegrationDeluxeChat;
import com.guillaumevdn.gcore.integration.headdatabase.IntegrationInstanceHeadDatabase;
import com.guillaumevdn.gcore.integration.mythicmobs.v4.IntegrationInstanceMythicMobsV4;
import com.guillaumevdn.gcore.integration.mythicmobs.v5.IntegrationInstanceMythicMobsV5;
import com.guillaumevdn.gcore.integration.placeholderapi.IntegrationInstancePlaceholderAPI;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.chat.AwaitingChatListeners;
import com.guillaumevdn.gcore.lib.chat.VanillaChatListeners;
import com.guillaumevdn.gcore.lib.data.sql.MySQLHandler;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemTypes;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationListeners;
import com.guillaumevdn.gcore.lib.location.AwaitingItemListeners;
import com.guillaumevdn.gcore.lib.location.AwaitingLocationListeners;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterUserNPCs;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.TextFile;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameTypes;
import com.guillaumevdn.gcore.libs.com.google.gson.GsonBuilder;
import com.guillaumevdn.gcore.listeners.ConnectionEvents;
import com.guillaumevdn.gcore.listeners.InventoryEvents;
import com.guillaumevdn.gcore.migration.v8_0.config.MigrationV8Config;
import com.guillaumevdn.gcore.migration.v8_0.data.MigrationV8Data;
import com.guillaumevdn.gcore.migration.v8_24.MigrationV8_24;
import com.guillaumevdn.gcore.migration.v8_25.MigrationV8_25;
import com.guillaumevdn.gcore.migration.v8_30.MigrationV8_30;
import com.guillaumevdn.gcore.migration.v8_5.MigrationV8_5;
import com.guillaumevdn.gcore.migration.v8_9.MigrationV8_9;
import com.guillaumevdn.gcore.task.OfflineUserNPCsActionsProcessor;

/**
 * @author GuillaumeVDN
 */
public final class GCore extends GPlugin<ConfigGCore, PermissionGCore> {

	private static GCore instance;
	public static GCore inst() { return instance; }

	public GCore() {
		super(24180, "gcore", "gcore", ConfigGCore.class, PermissionGCore.class, "data_v8",
				MigrationV8Config.class, MigrationV8Data.class,
				MigrationV8_5.class,
				MigrationV8_9.class,
				MigrationV8_24.class,
				MigrationV8_25.class,
				MigrationV8_30.class
				// __MigrationV8_36.class
				);
		instance = this;
	}

	@Override
	protected void preEnable() throws Throwable {
		// maybe create migration data files
		MigrationV8Config migration = new MigrationV8Config();
		if (!migration.wasMade() && !migration.mustMigrate()) {
			migration.markMade();
			new MigrationV8Data().markMade();
		}
	}

	@Override
	public GsonBuilder createGsonBuilder() {
		return super.createGsonBuilder().registerTypeAdapter(UserNPCs.class, AdapterUserNPCs.INSTANCE.getGsonAdapter());
	}

	// ----- base
	TimeFrameTypes timeFrameTypes = null;
	PositionTypes positionTypes = null;
	GUIItemTypes guiItemTypes = null;

	private MySQLHandler mysqlHandler = new MySQLHandler();
	private WorkerGCore worker;

	public TimeFrameTypes getTimeFrameTypes() {
		return timeFrameTypes;
	}

	public PositionTypes getPositionTypes() {
		return positionTypes;
	}

	public GUIItemTypes getGUIItemTypes() {
		return guiItemTypes;
	}

	public MySQLHandler getMySQLHandler() {
		return mysqlHandler;
	}

	public WorkerGCore getWorler() {
		return worker;
	}

	// ----- plugin
	@Override
	protected void registerTypes() {
		Serializer.init();
		// don't init time types here, they need CommonMats
	}

	@Override
	protected void registerTexts() {
		registerTextFile(new TextFile<>(this, "generic.yml", TextGeneric.class));
		registerTextFile(new TextFile<>(this, "generic_editor.yml", TextEditorGeneric.class));
		registerTextFile(new TextFile(this, "gcore.yml", TextGCore.class));
	}

	@Override
	protected void registerAndEnableIntegrations() {
		registerAndEnableIntegration(new Integration<>(this, "HeadDatabase", IntegrationInstanceHeadDatabase.class));
	}

	@Override
	protected void registerAndEnableIntegrationsPostConfig() {
		registerAndEnableIntegration(new Integration<>(this, "Citizens", IntegrationInstanceCitizens.class));
		registerAndEnableIntegration(new Integration<>(this, "PlaceholderAPI", IntegrationInstancePlaceholderAPI.class));

		Plugin mm = PluginUtils.getPlugin("MythicMobs");
		if (mm != null) {
			int current = StringUtils.getUniqueVersionNumber(mm.getDescription().getVersion().split("-")[0]);
			boolean v5 = current >= StringUtils.getUniqueVersionNumber("5.0.0");
			if (v5) {
				registerAndEnableIntegration(new Integration<>(this, "MythicMobs", IntegrationInstanceMythicMobsV5.class));
			} else {
				registerAndEnableIntegration(new Integration<>(this, "MythicMobs", IntegrationInstanceMythicMobsV4.class));
			}
		}
	}

	@Override
	protected void registerData() {
		registerDataBoard(new BoardStatistics());
		registerDataBoard(new BoardUsersNPCs());
	}

	@Override
	protected void enable() throws Throwable {
		// avoid some class errors
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.player.PlayerUtils");
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.data.sql.Query");
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.function.ThrowableConsumer");

		try {  // those occur with GCoreLegacy for some reason
			getClassLoader().loadClass("com.comphenix.protocol.wrappers.WrappedSignedProperty");
		} catch (ClassNotFoundException ignored) {}
		try {
			com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols.inst().getDefaultHumanEntityMetadata();
		} catch (Throwable ignored) {}

		// try to connect to mysql
		try {
			mysqlHandler.updateCanConnect();
			if (mysqlHandler.canConnect()) {
				getMainLogger().info("Connected to MySQL");
			}
		} catch (Throwable exception) {
			getMainLogger().error("couldn't initialize mysql connection, no data will be saved on database during this session", exception);
		}

		// init worker
		getMainLogger().info("Initializing worker and caches");
		worker = new WorkerGCore();
		if (!ConfigGCore.dontCacheOfflinePlayersOnLoad) {
			for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				worker.registerOfflinePlayer(player.getName(), player.getUniqueId());
			}
		}

		// integrations
		getMainLogger().info("Registering integrations");
		registerAndEnableIntegration(new Integration<>(this, "DeluxeChat", IntegrationDeluxeChat.class));

		// listeners
		getMainLogger().info("Initializing tasks and listeners");
		registerListener("connection", new ConnectionEvents());
		registerListener("inventory", new InventoryEvents());
		registerListener("vanilla_chat", new VanillaChatListeners());
		registerListener("awaiting_chat", new AwaitingChatListeners());
		registerListener("awaiting_location", new AwaitingLocationListeners());
		registerListener("awaiting_item", new AwaitingItemListeners());
		registerListener("integration", new IntegrationListeners());

		// tasks
		registerTask("offline_user_npcs_actions_processor", true, 5, new OfflineUserNPCsActionsProcessor());

		// gcore command
		getMainCommand().setSubcommand(new GcorePlugins());
		getMainCommand().setSubcommand(new GcoreExport());
		getMainCommand().setSubcommand(new GcoreNpcReset());
		getMainCommand().setSubcommand(new GcoreNpcResetEveryone());
		getMainCommand().setSubcommand(new GcorePermsReset());
		getMainCommand().setSubcommand(new GcoreItemRead());
		getMainCommand().setSubcommand(new GcoreItemReadChat());
		getMainCommand().setSubcommand(new GcoreItemReadClick());
		getMainCommand().setSubcommand(new GcoreItemSetNBTString());
		getMainCommand().setSubcommand(new GcoreBlockMat());
		getMainCommand().setSubcommand(new GcoreBlockMaterial());
		getMainCommand().setSubcommand(new GcoreImpl());
		getMainCommand().setSubcommand(new GcoreBoardElementPrint());
		getMainCommand().setSubcommand(new GcoreBoardStringElementPrint());
		getMainCommand().setSubcommand(new GcoreBoardUUIDElementPrint());
		getMainCommand().setSubcommand(new GcoreBoardConvert());
	}

	@Override
	protected void disable() throws Throwable {
		if (worker != null && worker.getNpcManager() != null) {
			worker.getNpcManager().disable();
		}
	}

}
