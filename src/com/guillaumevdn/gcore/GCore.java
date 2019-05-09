package com.guillaumevdn.gcore;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.guillaumevdn.gcore.commands.CommandDataExport;
import com.guillaumevdn.gcore.commands.CommandDataReset;
import com.guillaumevdn.gcore.commands.CommandItemMat;
import com.guillaumevdn.gcore.commands.CommandItemNbt;
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
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.UpdateCheck;
import com.guillaumevdn.gcore.lib.command.CommandArgument;
import com.guillaumevdn.gcore.lib.command.CommandRoot;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.data.DataManager.BackEnd;
import com.guillaumevdn.gcore.lib.data.mysql.Query;
import com.guillaumevdn.gcore.lib.event.PlayerBlockDropEvent;
import com.guillaumevdn.gcore.lib.event.PlayerCowMilkEvent;
import com.guillaumevdn.gcore.lib.event.PlayerCraftedItemEvent;
import com.guillaumevdn.gcore.lib.event.PlayerFireBlockEvent;
import com.guillaumevdn.gcore.lib.event.PlayerKillEvent;
import com.guillaumevdn.gcore.lib.event.PlayerSpawnedMobEvent;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.NpcManager;
import com.guillaumevdn.gcore.lib.util.BucketType;
import com.guillaumevdn.gcore.lib.util.ServerImplementation;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.SpawnEggUtils;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;
import com.guillaumevdn.gcore.lib.util.input.ItemInput;
import com.guillaumevdn.gcore.lib.util.input.LocationInput;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;
import com.guillaumevdn.gcore.libs.com.google.gson.Gson;

import sun.util.calendar.ZoneInfo;

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
	public static final Gson GSON = Utils.createGsonBuilder().setPrettyPrinting().create();
	public static final Gson UNPRETTY_GSON = Utils.createGsonBuilder().create();

	// storage
	private File dataRootFolder = null;
	private File userDataRootFolder = null;

	// settings
	private String activeLocaleLang = "en_US";
	private boolean updateCheck = true;
	private TimeZone calendarTimeZone = null;

	// misc
	private NpcManager npcManager = null;
	private VaultIntegration vaultIntegration = null;
	private Map<Player, ChatInput> chatInputs = new HashMap<Player, ChatInput>();
	private Map<Player, LocationInput> locationInputs = new HashMap<Player, LocationInput>();
	private Map<Player, ItemInput> itemInputs = new HashMap<Player, ItemInput>();

	// get
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

	public NpcManager getNpcManager() {
		return npcManager;
	}

	public VaultIntegration getVaultIntegration() {
		return vaultIntegration;
	}

	public void setVaultIntegration(VaultIntegration vaultIntegration) {
		this.vaultIntegration = vaultIntegration;
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
		// conversion V7
		// it failed earlier
		File failed = new File(getDataFolder() + "/conversion_failed.oops");
		if (failed.exists()) {
			cancelEnable("file " + failed.getPath() + " was found. This means that GCore tried to convert your files from a previous version but an error occured. Please check relevant logs, fix the problem (eventually contact the developper), then delete that file and reboot the server.");
			return false;
		}
		// attempt to convert
		try {
			new ConversionV7().start();
		} catch (Throwable exception) {
			// log
			exception.printStackTrace();
			cancelEnable("an error occured during the conversion of your files from a previous version. Please check relevant logs, fix the problem (eventually contact the developper), then delete that file and reboot the server.");
			// create lock file
			failed.getParentFile().mkdirs();
			try {
				failed.createNewFile();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			return false;
		}
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
		this.configuration = new YMLConfiguration(this, new File(getDataFolder() + "/config.yml"), "config.yml", false,
				true);
		this.allowCustomMaterials = getConfiguration().getBoolean("allow_custom_materials", false);
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
			if (npcManager == null)
				debug("Couldn't enable NPC manager with ProtocolLib");
		}

		// auto update
		updateCheck = getConfiguration().getBoolean("update_check", true);
		debug("Allowing update check : " + updateCheck);

		// return
		return true;
	}

	@Override
	protected boolean enable() {
		// server version
		debug("Detected server version : " + ServerVersion.CURRENT.getName()
		+ (ServerVersion.CURRENT.equals(ServerVersion.UNSUPPORTED)
				? " - this version isn't officially supported and plugins might not work as expected"
						: ""));
		debug("Detected server implementation : " + ServerImplementation.CURRENT.toString()
		+ (!ServerImplementation.CURRENT.equals(ServerImplementation.SPIGOT)
				? " - this implementation isn't officially supported and plugins might not work as expected"
						: ""));

		// init compat
		Compat.INSTANCE.init();

		// load the Query class, as it seems to throw a LinkageError if it's done 'in
		// action' (probably because MySQL operations are done asynchronously)
		try {
			getClassLoader().loadClass(Query.class.getName());
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}

		// vault integration
		registerPluginIntegration("Vault", VaultIntegration.class);

		// reload inner (mainly settings)
		innerReload();

		// register command
		CommandRoot root = new CommandRoot(this, Utils.asList("gcore"), null, GPerm.GCORE_ADMIN, false);
		registerCommand(root, GPerm.GCORE_ADMIN);
		// data commands
		CommandArgument data = new CommandArgument(this, Utils.asList("data"), "data-related commands",
				GPerm.GCORE_ADMIN, false);
		root.addChild(data);
		data.addChild(new CommandDataExport());
		data.addChild(new CommandDataReset());
		// item commands
		CommandArgument item = new CommandArgument(this, Utils.asList("item"), "item-related commands", GPerm.GCORE_ADMIN, false);
		root.addChild(item);
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
		return true;
	}

	// ------------------------------------------------------------
	// On disable
	// ------------------------------------------------------------

	@Override
	protected void disable() {
		// npc manager
		if (npcManager != null) {
			npcManager.disable();
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
		if (event.getPlugin().getName().equalsIgnoreCase("ProtocolLib")) {
			if (npcManager != null) {
				npcManager.disable();
				npcManager = null;
				debug("Disabled NPC manager with ProtocolLib");
			}
		}
	}

	@EventHandler
	public void event(PluginEnableEvent event) {
		if (event.getPlugin().getName().equalsIgnoreCase("ProtocolLib")) {
			if (npcManager == null) {
				(npcManager = new NpcManager()).enable();
				debug("Enabled NPC manager with ProtocolLib");
			}
		}
	}

	// ------------------------------------------------------------
	// Events
	// ------------------------------------------------------------

	private Map<Player, Block> interactedBlocks = new HashMap<Player, Block>();
	private Map<EntityType, Player> lastInteractedEggs = new HashMap<EntityType, Player>();
	private long lastBreakEvent = 0L;
	private BlockBreakEvent lastBreakBlock = null;
	private Mat lastBreakBlockType = null;
	private Map<Player, Cow> interactedCows = new HashMap<Player, Cow>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof Cow) {
			interactedCows.put(event.getPlayer(), (Cow) event.getRightClicked());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void event(PlayerBucketFillEvent event) {
		BucketType blockType = BucketType.get(event.getBlockClicked());
		if (blockType == null) {// milk bucket
			// get entity
			Cow cow = interactedCows.remove(event.getPlayer());
			if (cow != null) {
				PlayerCowMilkEvent ev = new PlayerCowMilkEvent(event.getPlayer(), cow);
				Bukkit.getPluginManager().callEvent(ev);
				event.setCancelled(ev.isCancelled());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerInteractEvent event) {
		// location input
		Player player = event.getPlayer();
		if (event.getAction().toString().contains("CLICK_BLOCK") && locationInputs.containsKey(player)) {
			locationInputs.remove(player).onChoose(player, event.getClickedBlock().getLocation());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void eventMonitor(PlayerInteractEvent event) {
		// clicked block
		if (event.getClickedBlock() != null) {
			Player player = event.getPlayer();
			interactedBlocks.put(player, event.getClickedBlock());
			// ... with creature egg
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null) {
				try {
					lastInteractedEggs.put(SpawnEggUtils.getSpawnedType(event.getItem()), player);
				} catch (Throwable ignored) {
				} // unsupported by server
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(CreatureSpawnEvent event) {
		if (event.getSpawnReason().toString().contains("EGG")) {
			Player player = lastInteractedEggs.remove(event.getEntity().getType());
			if (player != null) {
				Bukkit.getPluginManager().callEvent(new PlayerSpawnedMobEvent(player, event.getEntity()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		if (interactedBlocks.containsKey(player)) {
			PlayerFireBlockEvent newEvent = new PlayerFireBlockEvent(player, interactedBlocks.remove(player),
					event.getBlock(), event.getCause());
			Bukkit.getPluginManager().callEvent(newEvent);
			if (newEvent.isCancelled()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.getKiller() != null) {
			Bukkit.getPluginManager().callEvent(new PlayerKillEvent(player.getKiller(), player));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(BlockBreakEvent event) {
		lastBreakEvent = System.currentTimeMillis();
		lastBreakBlockType = Mat.from(event.getBlock());
		lastBreakBlock = event;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(ItemSpawnEvent event) {
		if (System.currentTimeMillis() - lastBreakEvent < 50L) {
			PlayerBlockDropEvent newEvent = new PlayerBlockDropEvent(lastBreakBlock.getPlayer(), event.getEntity(),
					lastBreakBlock, lastBreakBlockType);
			Bukkit.getPluginManager().callEvent(newEvent);
			event.setCancelled(newEvent.isCancelled());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerDropItemEvent event) {
		// item input
		if (itemInputs.containsKey(event.getPlayer())) {
			Player player = event.getPlayer();
			itemInputs.remove(event.getPlayer()).onChoose(player, event.getItemDrop().getItemStack());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(PlayerToggleSneakEvent event) {
		// location input
		if (locationInputs.containsKey(event.getPlayer())) {
			Player player = event.getPlayer();
			locationInputs.remove(event.getPlayer()).onChoose(player, player.getLocation());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void event(final AsyncPlayerChatEvent event) {
		// chat input
		if (chatInputs.containsKey(event.getPlayer())) {
			event.setCancelled(true);
			event.getRecipients().clear();
			event.setMessage("");
			event.setFormat("");
			// resync to continue
			new BukkitRunnable() {
				@Override
				public void run() {
					chatInputs.remove(event.getPlayer()).onChat(event.getPlayer(), Utils.format(event.getMessage()));
				}
			}.runTask(GCore.instance);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void event(PlayerJoinEvent event) {
		if (updateCheck && GPerm.GCORE_ADMIN.has(event.getPlayer())) {
			UpdateCheck.notify(Utils.asList(event.getPlayer()));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void event(final CraftItemEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final ItemStack preCursor = !Mat.from(player.getItemOnCursor()).isAir() ? player.getItemOnCursor().clone()
				: null;
		final Map<Integer, ItemStack> preItems = new HashMap<Integer, ItemStack>();
		for (int slot = 0; slot < player.getInventory().getContents().length; ++slot) {
			ItemStack item = player.getInventory().getContents()[slot];
			if (!Mat.from(item).isAir()) {
				preItems.put(slot, item.clone());
			}
		}
		// delay for craft
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!event.isCancelled()) {
					Map<Integer, ItemStack> crafted = new HashMap<Integer, ItemStack>();
					for (int slot = 0; slot < player.getInventory().getContents().length; ++slot) {
						ItemStack preItem = preItems.get(slot);
						ItemStack item = player.getInventory().getContents()[slot];
						if (!Mat.from(item).isAir()) {
							if (preItem != null) {// has pre item
								int added = preItem != null ? item.getAmount() - preItem.getAmount() : item.getAmount();
								if (added > 0) {
									preItem.setAmount(added);
									crafted.put(slot, preItem);
								}
							} else {// no pre item
								crafted.put(slot, item.clone());
							}
						}
					}
					ItemStack cursor = player.getItemOnCursor();
					if (!Mat.from(cursor).isAir()) {
						if (preCursor != null) {// has pre cursor
							int added = preCursor != null ? cursor.getAmount() - preCursor.getAmount()
									: cursor.getAmount();
							if (added > 0) {
								preCursor.setAmount(added);
								crafted.put(-1, preCursor);
							}
						} else {// no pre item
							crafted.put(-1, cursor.clone());
						}
					}
					if (!crafted.isEmpty()) {
						Bukkit.getPluginManager().callEvent(new PlayerCraftedItemEvent(event, crafted));
					}
				}
			}
		}.runTaskLater(this, 1L);
	}

}
