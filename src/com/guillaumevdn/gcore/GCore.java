package com.guillaumevdn.gcore;

import java.io.File;

import com.guillaumevdn.gcore.command.GcoreExport;
import com.guillaumevdn.gcore.command.GcoreItemRead;
import com.guillaumevdn.gcore.command.GcorePlugins;
import com.guillaumevdn.gcore.command.GcoreReload;
import com.guillaumevdn.gcore.data.BoardStatistics;
import com.guillaumevdn.gcore.data.usernpcs.BoardUsersNPCs;
import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.integration.citizens.IntegrationInstanceCitizens;
import com.guillaumevdn.gcore.integration.deluxechat.IntegrationDeluxeChat;
import com.guillaumevdn.gcore.integration.mythicmobs.IntegrationInstanceMythicMobs;
import com.guillaumevdn.gcore.integration.placeholderapi.IntegrationInstancePlaceholderAPI;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.chat.AwaitingChatListeners;
import com.guillaumevdn.gcore.lib.chat.VanillaChatListeners;
import com.guillaumevdn.gcore.lib.command.Command;
import com.guillaumevdn.gcore.lib.compatibility.bossbar.Bossbar;
import com.guillaumevdn.gcore.lib.data.MySQLConnector;
import com.guillaumevdn.gcore.lib.event.CustomEventsListeners;
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
import com.guillaumevdn.gcore.listeners.ConnectionEvent;
import com.guillaumevdn.gcore.migration.v8_0.config.MigrationV8Config;
import com.guillaumevdn.gcore.migration.v8_0.data.MigrationV8Data;

/**
 * @author GuillaumeVDN
 */
public final class GCore extends GPlugin<ConfigGCore, PermissionGCore> {

	private static GCore instance;
	public static GCore inst() { return instance; }

	public GCore() {
		super(24180, ConfigGCore.class, PermissionGCore.class, MigrationV8Config.class, MigrationV8Data.class);
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

	// base
	TimeFrameTypes timeFrameTypes = null;
	PositionTypes positionTypes = null;

	private MySQLConnector mysqlConnector = new MySQLConnector();
	private WorkerGCore worker;

	public TimeFrameTypes getTimeFrameTypes() {
		return timeFrameTypes;
	}

	public PositionTypes getPositionTypes() {
		return positionTypes;
	}

	public MySQLConnector getMySQLConnector() {
		return mysqlConnector;
	}

	public WorkerGCore getWorler() {
		return worker;
	}

	// plugin
	@Override
	protected void registerTypes() {
		Serializer.init();
		// don't init time frame / position types here, they need CommonMats
	}

	@Override
	protected void registerTexts() {
		registerTextFile(new TextFile<>(this, "generic.yml", TextGeneric.class));
		registerTextFile(new TextFile<>(this, "generic_editor.yml", TextEditorGeneric.class));
		registerTextFile(new TextFile(this, "gcore.yml", TextGCore.class));
	}

	@Override
	protected File getDefaultTextsFolder() {
		return getDataFile("/data_v8/default_texts/");
	}

	@Override
	protected void registerAndEnableIntegrations() {
		registerAndEnableIntegration(new Integration<>(this, "Citizens", IntegrationInstanceCitizens.class));
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
		// avoid some linkage errors
		getClassLoader().loadClass("com.guillaumevdn.gcore.lib.player.PlayerUtils");

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
		worker = new WorkerGCore();

		// listeners
		registerListener(new ConnectionEvent());
		registerListener(new VanillaChatListeners());
		registerListener(new AwaitingChatListeners());
		registerListener(new AwaitingLocationListeners());
		registerListener(new AwaitingItemListeners());
		registerListener(new CustomEventsListeners());
		registerListener(new IntegrationListeners());

		// integrations
		registerAndEnableIntegration(new Integration<>(this, "DeluxeChat", IntegrationDeluxeChat.class));

		// gcore command
		Command commandGcore = registerCommand(new Command(this, "gcore", "gcore", null));
		commandGcore.setSubcommand(new GcoreReload());
		commandGcore.setSubcommand(new GcorePlugins());
		commandGcore.setSubcommand(new GcoreExport());
		commandGcore.setSubcommand(new GcoreItemRead());
	}

	@Override
	protected void disable() throws Throwable {
		if (worker != null && worker.getNpcManager() != null) {
			worker.getNpcManager().disable();
		}
		Bossbar.stopAllActive();
	}

}
