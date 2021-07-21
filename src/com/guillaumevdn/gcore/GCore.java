package com.guillaumevdn.gcore;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.guillaumevdn.gcore.command.GcoreBlockMat;
import com.guillaumevdn.gcore.command.GcoreBlockMaterial;
import com.guillaumevdn.gcore.command.GcoreExport;
import com.guillaumevdn.gcore.command.GcoreImpl;
import com.guillaumevdn.gcore.command.GcoreItemRead;
import com.guillaumevdn.gcore.command.GcoreNpcReset;
import com.guillaumevdn.gcore.command.GcorePlugins;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.integration.citizens.IntegrationInstanceCitizens;
import com.guillaumevdn.gcore.integration.deluxechat.IntegrationDeluxeChat;
import com.guillaumevdn.gcore.integration.holographicdisplays.IntegrationInstanceHolographicDisplays;
import com.guillaumevdn.gcore.integration.mythicmobs.IntegrationInstanceMythicMobs;
import com.guillaumevdn.gcore.integration.placeholderapi.IntegrationInstancePlaceholderAPI;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.chat.AwaitingChatListeners;
import com.guillaumevdn.gcore.lib.chat.VanillaChatListeners;
import com.guillaumevdn.gcore.lib.data.MySQLConnector;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemTypes;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationListeners;
import com.guillaumevdn.gcore.lib.location.AwaitingItemListeners;
import com.guillaumevdn.gcore.lib.location.AwaitingLocationListeners;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterUserNPCs;
import com.guillaumevdn.gcore.lib.string.TextFile;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameTypes;
import com.guillaumevdn.gcore.libs.com.google.gson.GsonBuilder;
import com.guillaumevdn.gcore.listeners.ConnectionEvents;
import com.guillaumevdn.gcore.migration.v8_0.config.MigrationV8Config;
import com.guillaumevdn.gcore.migration.v8_0.data.MigrationV8Data;
import com.guillaumevdn.gcore.migration.v8_24.MigrationV8_24;
import com.guillaumevdn.gcore.migration.v8_5.MigrationV8_5;
import com.guillaumevdn.gcore.migration.v8_9.MigrationV8_9;

/**
 * @author GuillaumeVDN
 */
public final class GCore extends GPlugin<ConfigGCore, PermissionGCore> {

	private static GCore instance;
	public static GCore inst() { return instance; }

	public GCore() {
		super(24180, "gcore", "gcore", ConfigGCore.class, PermissionGCore.class,
				MigrationV8Config.class, MigrationV8Data.class,
				MigrationV8_5.class,
				MigrationV8_9.class,
				MigrationV8_24.class
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

	private MySQLConnector mysqlConnector = new MySQLConnector();
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

	public MySQLConnector getMySQLConnector() {
		return mysqlConnector;
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
	public File getDefaultTextsFolder() {
		return getDataFile("/data_v8/default_texts/");
	}

	@Override
	protected void registerAndEnableIntegrations() {
		registerAndEnableIntegration(new Integration<>(this, "Citizens", IntegrationInstanceCitizens.class));
		registerAndEnableIntegration(new Integration<>(this, "HolographicDisplays", IntegrationInstanceHolographicDisplays.class));
		registerAndEnableIntegration(new Integration<>(this, "MythicMobs", IntegrationInstanceMythicMobs.class));
		registerAndEnableIntegration(new Integration<>(this, "PlaceholderAPI", IntegrationInstancePlaceholderAPI.class));
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
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.data.Query");
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.function.ThrowableConsumer");

		try {  // those occur with GCoreLegacy for some reason
			getClassLoader().loadClass("com.comphenix.protocol.wrappers.WrappedSignedProperty");
		} catch (ClassNotFoundException ignored) {}
		try {
			com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols.inst().getDefaultHumanEntityMetadata();
		} catch (Throwable ignored) {}

		// try to connect to mysql
		try {
			mysqlConnector.updateCanConnect();
			if (mysqlConnector.canConnect()) {
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
		registerListener("vanilla_chat", new VanillaChatListeners());
		registerListener("awaiting_chat", new AwaitingChatListeners());
		registerListener("awaiting_location", new AwaitingLocationListeners());
		registerListener("awaiting_item", new AwaitingItemListeners());
		registerListener("integration", new IntegrationListeners());

		// gcore command
		getMainCommand().setSubcommand(new GcorePlugins());
		getMainCommand().setSubcommand(new GcoreExport());
		getMainCommand().setSubcommand(new GcoreNpcReset());
		getMainCommand().setSubcommand(new GcoreItemRead());
		getMainCommand().setSubcommand(new GcoreBlockMat());
		getMainCommand().setSubcommand(new GcoreBlockMaterial());
		getMainCommand().setSubcommand(new GcoreImpl());
	}

	@Override
	protected void disable() throws Throwable {
		if (worker != null && worker.getNpcManager() != null) {
			worker.getNpcManager().disable();
		}
	}

}
