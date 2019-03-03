package be.pyrrh4.pyrcore.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.CropState;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.lib.Logger;
import be.pyrrh4.pyrcore.lib.Logger.Level;
import be.pyrrh4.pyrcore.lib.Perm;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Messenger;
import be.pyrrh4.pyrcore.lib.messenger.Replacer;
import be.pyrrh4.pyrcore.lib.messenger.Tab;
import be.pyrrh4.pyrcore.lib.messenger.Text;
import be.pyrrh4.pyrcore.lib.messenger.Title;
import be.pyrrh4.pyrcore.lib.util.BungeeMessagingAPI.BungeeOutMessage;
import be.pyrrh4.pyrcore.lib.versioncompat.Compat;
import be.pyrrh4.pyrcore.lib.versioncompat.particle.ParticleManager;
import be.pyrrh4.pyrcore.lib.versioncompat.sound.Sound;
import be.pyrrh4.pyrcore.libs.com.google.gson.Gson;
import be.pyrrh4.pyrcore.libs.com.google.gson.GsonBuilder;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterAchievement;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterClass;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterClassFactory;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterEnchantment;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterEntityType;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterFile;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterInventory;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterItemStack;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterLocation;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterMat;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterMaterial;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterPCUser;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterPlugin;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterPotionEffectType;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterSound;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterSoundBukkit;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterTab;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterText;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterTitle;
import be.pyrrh4.pyrcore.libs.com.google.gson.adapter.AdapterWorld;

// TODO : sort this and remove useless/duplicate methods

public class Utils {

	// ------------------------------------------------------------
	// Static fields
	// ------------------------------------------------------------

	public static final Random RANDOM = new Random();
	public static final long MILLIS_2HOURS = Utils.getHoursInMillis(2);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm");
	public static final boolean IS_1_13 = ServerVersion.CURRENT.isAtLeast(ServerVersion.MC_1_13);

	// ------------------------------------------------------------
	// Server version
	// ------------------------------------------------------------

	public static ServerVersion getServerVersion() {
		String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
		List<ServerVersion> versions = Utils.asList(ServerVersion.values());
		Collections.reverse(versions);
		for (ServerVersion version : versions) {
			if (bukkitVersion.contains(version.getName())) {
				return version;
			}
		}
		return ServerVersion.UNSUPPORTED;
	}

	public static ServerImplementation getServerImplementation() {
		try {
			Class.forName("com.destroystokyo.paper.PaperConfig");
			return ServerImplementation.PAPERSPIGOT;
		} catch (Exception ignored) {}
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			return ServerImplementation.SPIGOT;
		} catch (Exception ignored) {}
		return ServerImplementation.CRAFTBUKKIT;
	}

	// ------------------------------------------------------------
	// Compatibility
	// ------------------------------------------------------------

	public static Compat createCompat() {
		try {
			Compat compat = ServerVersion.CURRENT.getCompatClass().newInstance();
			PyrCore.inst().debug("Creating compatibility for version " + ServerVersion.CURRENT.getName());
			return compat;
		} catch (Throwable exception) {
			exception.printStackTrace();
			PyrCore.inst().error("Could not initialize compatibility for version " + ServerVersion.CURRENT.getName());
			return null;
		}
	}

	public static ParticleManager createParticleManagerCompat() {
		try {
			ParticleManager compat = ServerVersion.CURRENT.getParticleCompatClass().newInstance();
			PyrCore.inst().debug("Creating particle manager compatibility for version " + ServerVersion.CURRENT.getName());
			return compat;
		} catch (InstantiationException | IllegalAccessException exception) {
			exception.printStackTrace();
			PyrCore.inst().error("Could not initialize particle manager compatibility for version " + ServerVersion.CURRENT.getName());
			return null;
		}
	}

	public static int getUniqueVersionNumber(String pluginVersion)
	{
		if (pluginVersion.contains("-")) {
			pluginVersion = pluginVersion.split("-")[0];
		}

		String version = "1";//add 1 to consider the eventual 0 before
		String[] temp = new String[3];// up to 3 entries maximum, example : 1.0.0 will be converted to 1 100 000 000

		int i = 0;
		for (String str : pluginVersion.split("\\.")) {
			temp[i++] = parseToHundredFormat(str);
		}

		for (i = 0; i < temp.length; i++) {
			if (temp[i] == null) temp[i] = "000";
			version += temp[i];
		}

		return Integer.parseInt(version);
	}

	private static String parseToHundredFormat(String str)
	{
		while (str.length() < 3) {
			str = "0" + str;
		}

		return str;
	}

	public static Plugin getPlugin(String name) {
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin.getName().equalsIgnoreCase(name)) {
				return plugin;
			}
		}
		return null;
	}

	public static boolean isPluginEnabled(String name) {
		Plugin plugin = getPlugin(name);
		return plugin == null ? false : plugin.isEnabled();
	}

	// ------------------------------------------------------------
	// Storage
	// ------------------------------------------------------------

	public static GsonBuilder createGsonBuilder()
	{
		return new GsonBuilder()
				.registerTypeAdapterFactory(new AdapterClassFactory())
				.registerTypeAdapter(Class.class, new AdapterClass())
				.registerTypeAdapter(File.class, new AdapterFile())
				.registerTypeAdapter(Achievement.class, new AdapterAchievement())
				.registerTypeAdapter(Enchantment.class, new AdapterEnchantment())
				.registerTypeAdapter(EntityType.class, new AdapterEntityType())
				.registerTypeAdapter(Location.class, new AdapterLocation())
				.registerTypeAdapter(ItemStack.class, new AdapterItemStack())
				.registerTypeAdapter(Material.class, new AdapterMaterial())
				.registerTypeAdapter(Mat.class, new AdapterMat())
				.registerTypeAdapter(PotionEffectType.class, new AdapterPotionEffectType())
				.registerTypeAdapter(Sound.class, new AdapterSound())
				.registerTypeAdapter(org.bukkit.Sound.class, new AdapterSoundBukkit())
				.registerTypeAdapter(Text.class, new AdapterText())
				.registerTypeAdapter(Title.class, new AdapterTitle())
				.registerTypeAdapter(Tab.class, new AdapterTab())
				.registerTypeAdapter(World.class, new AdapterWorld())
				.registerTypeAdapter(Inventory.class, new AdapterInventory())
				.registerTypeAdapter(Plugin.class, new AdapterPlugin())
				.registerTypeAdapter(PCUser.class, new AdapterPCUser())
				.enableComplexMapKeySerialization()
				.disableInnerClassSerialization()
				.serializeSpecialFloatingPointValues();
	}

	public static <T> boolean saveToGson(T object, File file) {
		return saveToGson(object, file, PyrCore.GSON);
	}

	public static <T> boolean saveToGson(T object, File file, Gson gson) {
		File temp = new File(file.getPath() + ".temp");
		try {
			if (file.getParentFile() != null && !file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (temp.getParentFile() != null && !temp.getParentFile().exists()) {
				temp.getParentFile().mkdirs();
			}

			if (!temp.exists()) {
				temp.createNewFile();
			}

			FileWriter writer = new FileWriter(temp);
			gson.toJson(object, writer);
			writer.close();

			if (file.exists()) {
				file.delete();
			}

			temp.renameTo(file);
			return true;
		}
		catch (Throwable exception) {
			exception.printStackTrace();
			temp.delete();
			Logger.log(Level.SEVERE, "PyrCore", "Could not save " + file);
			return false;
		}
	}

	public static <T> T loadFromGson(Class<? extends T> typeClass, File file, boolean logError) {
		return loadFromGson(typeClass, file, logError, PyrCore.GSON);
	}

	public static <T> T loadFromGson(Class<? extends T> typeClass, File file, boolean logError, Gson gson) {
		try {
			if (file != null && file.exists()) {
				FileReader reader = new FileReader(file);
				T t = gson.fromJson(reader, typeClass);
				reader.close();
				return t;
			}
		}
		catch (Throwable exception) {
			if (logError) {
				exception.printStackTrace();
				Logger.log(Level.SEVERE, "PyrCore", "Could not load " + file);
			}
		}

		return null;
	}

	public static void setDefaultResource(PyrPlugin plugin, File file, String defaultResource, boolean createIfNoDefault) {
		if (!file.exists()) {
			InputStream in = defaultResource == null ? null : plugin.getResource(defaultResource);
			if (in != null) {
				try {
					OutputStream out = new FileOutputStream(file);
					byte[] buf = new byte['?'];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
					in.close();
				} catch (Throwable exception) {
					exception.printStackTrace();
					Logger.log(Level.INFO, plugin, "Could not set default resources " + file.getName());
				}
			}
		}
		if (!file.exists() && createIfNoDefault) {
			try {
				file.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
				Logger.log(Level.INFO, plugin, "Could not create " + file.getName());
			}
		}
	}

	// ------------------------------------------------------------
	// Reflection
	// ------------------------------------------------------------

	// TODO : remove this
	private static Map<String, String> eventActorSpecialMethodName = asMap(
			"EntityToggleGlideEvent", "getEntity",
			"EntityBlockFormEvent", "getEntity",
			"EntityRegainHealthEvent", "getEntity",
			"CraftItemEvent", "getWhoClicked",
			"EnchantItemEvent", "getEnchanter",
			"HorseJumpEvent", "getEntity.getPassenger",
			"EntityDismountEvent", "getEntity",
			"EntityDeathEvent", "getEntity.getKiller",
			"EntityMountEvent", "getEntity",
			"EntityTameEvent", "getOwner",
			"MythicMobDeathEvent", "getKiller",
			"NPCClickEvent", "getClicker",
			"NPCRightClickEvent", "getClicker",
			"NPCLeftClickEvent", "getClicker",
			"NPCDeathEvent", "getNPC.getEntity.getKiller",
			"EntityPortalEnterEvent", "getEntity",
			"EntityPortalExitEvent", "getEntity",
			"PotionSplashEvent", "getPotion.getShooter",
			"ProjectileHitEvent", "getEntity.getShooter",
			"PlayerExperienceGainEvent", "getPlayerData.getPlayer",
			"VehicleEnterEvent", "getEntered",
			"VehicleExitEvent", "getExited",
			"ExpBottleEvent", "getEntity.getShooter",
			"BlockIgniteEvent", "getIgnitingEntity",
			"PlayerDeathEvent", "getEntity",
			"PlayerLootEvent", "getLooter"
			);

	public static Player getEventActor(Event event) {
		try {
			String raw = eventActorSpecialMethodName.containsKey(event.getClass().getSimpleName()) ? eventActorSpecialMethodName.get(event.getClass().getSimpleName()) : "getPlayer";
			List<String> methodNames = Utils.split("\\.", raw, false);
			Object invokeResult = null;
			for (String methodName : methodNames) {
				Object invoker = invokeResult == null ? event : invokeResult;
				invokeResult = invoker.getClass().getMethod(methodName).invoke(invoker);
				if (invokeResult == null) {
					return null;
				}
			}
			if (invokeResult != null && invokeResult instanceof Player) {
				return (Player) invokeResult;
			}
			return null;
		} catch (Throwable exception) {}
		return null;
	}

	public static void setField(Object instance, String fieldName, Object value)
	{
		try {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		}
		catch (NullPointerException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}

	public static <T extends Enum<T>> T valueOfOrNull(Class<T> typeClass, String string) {
		if (typeClass == null || string == null) {
			return null;
		}
		try {
			return Enum.valueOf(typeClass, string);
		} catch (Throwable ignored) {
			try {
				return Enum.valueOf(typeClass, string.toUpperCase());
			} catch (Throwable ignored2) {
				return null;
			}
		}
	}

	public static Enchantment enchantmentOrNull(String string) {
		try {
			return Enchantment.getByName(string);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static PotionEffectType potionEffectTypeOrNull(String string) {
		try {
			return PotionEffectType.getByName(string);
		} catch (Throwable ignored) {
			return null;
		}
	}

	// ------------------------------------------------------------
	// File
	// ------------------------------------------------------------

	public static File unzipGZ(File gzFile, PyrPlugin pl)
	{
		if (gzFile == null) {
			return null;
		}

		try
		{
			File output = File.createTempFile(pl.getDescription().getName().toLowerCase() + "_" + "log_", ".log");
			Logger.log(Level.INFO, pl, "Unzipped file to " + output.getAbsolutePath());
			byte[] buffer = new byte[1024];
			GZIPInputStream in = new GZIPInputStream(new FileInputStream(gzFile));
			FileOutputStream out = new FileOutputStream(output);

			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			in.close();
			out.close();

			return output;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void zipFile(File file, File target) {
		final Path sourceDir = Paths.get(file.getPath());
		String zipFileName = target.getPath();
		try {
			if (!file.exists()) {
				target.createNewFile();
			}
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					try {
						Path targetFile = sourceDir.relativize(file);
						outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
						byte[] bytes = Files.readAllBytes(file);
						outputStream.write(bytes, 0, bytes.length);
						outputStream.closeEntry();
					} catch (IOException exception) {
						exception.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
			outputStream.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static String getFileNameWithoutExtension(File file) {
		return file.getName().contains(".") ? file.getName().split("\\.")[0] : file.getName();
	}

	public static String getClassActualName(Class<?> clazz) {
		String name = clazz.getName();
		return name.contains(".") ? name.substring(name.lastIndexOf(".") + 1, name.length()) : name;
	}

	public static boolean ensureAuthorization(JavaPlugin plugin) {
		boolean authorized = false;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://www.pyrrh4.be/plugins/safety.php?plugin=" + plugin.getName()).openConnection();
			conn.setDoOutput(true);
			String response = new BufferedReader(new InputStreamReader(conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream())).readLine();
			if (String.valueOf(response).equals("ALLOWED")) {
				authorized = true;
			}
		} catch (IOException | IllegalArgumentException | SecurityException exception) {}
		return authorized;
	}

	// ------------------------------------------------------------
	// Title, tab and action bar
	// ------------------------------------------------------------

	public static void clearTitle(Player player) {
		Messenger.title(player, "", "", 0, 1, 0);
	}

	public static void clearActionBar(Player player) {
		Messenger.actionBar(player, "");
	}

	public static void clearTab(Player player) {
		Messenger.tab(player, "", "");
	}

	// ------------------------------------------------------------
	// Entities
	// ------------------------------------------------------------

	public static Entity getEntityByUUID(UUID uuid) {
		if (uuid == null) return null;
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity.getUniqueId().equals(uuid)) {
					return entity;
				}
			}
		}
		return null;
	}

	public static List<LivingEntity> getLivingEntitiesInBox(Location center, double xOffset, double yOffset, double zOffset) {
		List<LivingEntity> result = new ArrayList<LivingEntity>();
		for (LivingEntity entity : center.getWorld().getLivingEntities()) {
			Location loc = entity.getLocation();
			if (loc.getX() > center.getX() - xOffset && loc.getX() < center.getX() + xOffset
					&& loc.getY() > center.getY() - yOffset && loc.getY() < center.getY() + yOffset
					&& loc.getZ() > center.getZ() - zOffset && loc.getZ() < center.getZ() + zOffset) {
				result.add(entity);
			}
		}
		return result;
	}

	public static <T> List<T> getElementsOfType(Collection<?> collection, Class<T> typeClass) {
		List<T> result = new ArrayList<T>();
		for (Object entity : collection) {
			if (instanceOf(entity, typeClass)) {
				result.add((T) entity);
			}
		}
		return result;
	}

	public static <T extends Entity> List<T> getEntitiesInBox(Location center, double xOffset, double yOffset, double zOffset, Class<T> entityClass) {
		List<T> result = new ArrayList<T>();
		for (T entity : center.getWorld().getEntitiesByClass(entityClass)) {
			Location loc = entity.getLocation();
			if (loc.getX() > center.getX() - xOffset && loc.getX() < center.getX() + xOffset
					&& loc.getY() > center.getY() - yOffset && loc.getY() < center.getY() + yOffset
					&& loc.getZ() > center.getZ() - zOffset && loc.getZ() < center.getZ() + zOffset) {
				result.add(entity);
			}
		}
		return result;
	}

	public static List<Entity> getEntitiesInBox(Location center, double xOffset, double yOffset, double zOffset) {
		List<Entity> result = new ArrayList<Entity>();
		for (Entity entity : center.getWorld().getEntities()) {
			Location loc = entity.getLocation();
			if (loc.getX() > center.getX() - xOffset && loc.getX() < center.getX() + xOffset
					&& loc.getY() > center.getY() - yOffset && loc.getY() < center.getY() + yOffset
					&& loc.getZ() > center.getZ() - zOffset && loc.getZ() < center.getZ() + zOffset) {
				result.add(entity);
			}
		}
		return result;
	}

	// ------------------------------------------------------------
	// Players
	// ------------------------------------------------------------

	public static List<Player> getOnlinePlayersWithPermission(Perm permission) {
		if (permission == null) return getOnlinePlayers();
		List<Player> players = new ArrayList<Player>();
		for (Player pl : getOnlinePlayers()) {
			if (permission.has(pl)) {
				players.add(pl);
			}
		}
		return players;
	}

	public static List<Player> getOnlinePlayers() {
		return getOnlinePlayers(false);
	}

	public static List<Player> getOnlinePlayers(boolean opOnly) {
		if (opOnly) {
			List<Player> players = new ArrayList<Player>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.isOp()) {
					players.add(player);
				}
			}
			return players;
		} else {
			return new ArrayList<Player>(Bukkit.getOnlinePlayers());
		}
	}

	public static List<Player> getOnlinePlayers(Collection<UUID> uuids) {
		List<Player> players = new ArrayList<Player>();
		if (uuids == null) return players;
		for (UUID uuid : uuids) {
			Player player = getPlayer(uuid);
			if (player != null) {
				players.add(player);
			}
		}
		return players;
	}

	public static List<Player> getOnlinePlayersOf(Collection<OfflinePlayer> players) {
		List<Player> online = new ArrayList<Player>();
		if (players == null) return online;
		for (OfflinePlayer player : players) {
			if (player.isOnline()) {
				online.add(player.getPlayer());
			}
		}
		return online;
	}

	public static List<UUID> getOnlinePlayersUUIDs() {
		return getPlayersUUIDs(getOnlinePlayers());
	}

	public static List<UUID> getPlayersUUIDs(Collection<Player> players) {
		List<UUID> uuids = new ArrayList<UUID>();
		if (players == null) return uuids;
		for (Player pl : players) uuids.add(pl.getUniqueId());
		return uuids;
	}

	public static List<OfflinePlayer> getOfflinePlayers(Collection<UUID> uuids) {
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		if (uuids == null) return players;
		for (UUID uuid : uuids) {
			OfflinePlayer player = getOfflinePlayer(uuid);
			if (player != null && !player.isOnline()) {
				players.add(player);
			}
		}
		return players;
	}

	public static Player getPlayer(String name)
	{
		if (name == null) return null;
		name = name.replace(" ", "").replace("\t", "");
		OfflinePlayer player = getOfflinePlayer(name);
		if (player != null && !(player instanceof Player)) return player.getPlayer();
		return (Player) player;
	}

	public static Player getPlayer(UUID uuid)
	{
		if (uuid == null) return null;
		OfflinePlayer player = getOfflinePlayer(uuid);
		if (player != null && !(player instanceof Player)) return player.getPlayer();
		return (Player) player;
	}

	public static OfflinePlayer getOfflinePlayer(String name) {
		if (name == null) return null;
		name = name.replace(" ", "").replace("\t", "");
		OfflinePlayer player = null;
		if ((player = Bukkit.getPlayer(name)) != null) {
			return player;
		} else if ((player = Bukkit.getOfflinePlayer(name)) != null) {
			return player;
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayer(UUID uuid)
	{
		if (uuid == null) return null;
		OfflinePlayer player = null;
		if ((player = Bukkit.getPlayer(uuid)) != null) {
			return player;
		} else if ((player = Bukkit.getOfflinePlayer(uuid)) != null) {
			return player;
		}
		return null;
	}

	// ------------------------------------------------------------
	// Player utils
	// ------------------------------------------------------------

	public static void clear(Player player)
	{
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}

	public static void resetEffects(Player player)
	{
		for (PotionEffectType pe : PotionEffectType.values())
		{
			if (pe != null) {
				player.removePotionEffect(pe);
			}
		}
	}

	public static void resetLife(Player player)
	{
		player.resetMaxHealth();
		player.setHealth(((Damageable) player).getMaxHealth());
		player.setFoodLevel(20);
	}

	public static void resetGameMode(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
	}

	public static void resetWalkSpeed(Player player) {
		player.setWalkSpeed(0.2F);
	}

	public static void resetFlySpeed(Player player) {
		player.setFlySpeed(0.1F);
	}

	public static void allowFly(Player player, boolean allow)
	{
		if (allow) {
			player.setAllowFlight(true);
		} else {
			player.setAllowFlight(player.getGameMode().equals(GameMode.CREATIVE) ? true : false);
			player.setFlying(false);
		}
	}

	public static void hideFromAll(Player player)
	{
		for (Player pl : getOnlinePlayers()) {
			pl.hidePlayer(player);
		}
	}

	public static void hideFromPlayers(Player player, List<Player> players)
	{
		for (Player pl : players) {
			pl.hidePlayer(player);
		}
	}

	public static void showToAll(Player player)
	{
		for (Player pl : getOnlinePlayers()) {
			pl.showPlayer(player);
		}
	}

	public static void showToPlayers(Player player, List<Player> players)
	{
		for (Player pl : players) {
			pl.showPlayer(player);
		}
	}

	public static void bungeeTeleport(final BungeeMessagingAPI apiInstance, final Player player, final List<String> servers, int redirectDelaySeconds, final boolean kickIfCantTeleport)
	{
		new BukkitRunnable()
		{
			private int index = 0;

			@Override
			public void run()
			{
				try
				{
					if (player == null || !player.isOnline())
					{
						Logger.log(Level.DEBUG, PyrCore.inst(), "->" + (player == null ? "target" : player.getName()) + " isn't online");
						cancel();
						return;
					}

					bungeeTeleport(apiInstance, player, servers.get(index));
					index++;

					if (index + 1 >= servers.size())
					{
						Logger.log(Level.DEBUG, PyrCore.inst(), "->" + player.getName() + " : index " + index + ", end of servers");
						if (kickIfCantTeleport) {
							player.kickPlayer("Could not teleport you to a bungee server.");
						}

						cancel();
						return;
					}
				}
				catch(Exception ignored) {}
			}
		}.runTaskTimer(apiInstance.getPlugin(), 20L, new Integer(redirectDelaySeconds * 20).longValue());
	}

	public static void bungeeTeleport(BungeeMessagingAPI apiInstance, Player player, String server)
	{
		BungeeOutMessage message = apiInstance.new BungeeOutMessage("Connect", server);
		message.send(player);
		Logger.log(Level.DEBUG, PyrCore.inst(), "->" + player.getName() + " : sending on server " + server);
	}

	// ------------------------------------------------------------
	// Firework and color
	// ------------------------------------------------------------

	public static final Map<Integer, Color> COLORS = Utils.asMap(1, Color.AQUA, 2, Color.BLACK, 3, Color.BLUE, 4, Color.FUCHSIA, 5, Color.GRAY, 6, Color.GREEN, 7, Color.LIME, 8, Color.MAROON, 9, Color.NAVY, 10, Color.OLIVE, 11, Color.ORANGE, 12, Color.PURPLE, 13, Color.RED, 14, Color.SILVER, 15, Color.TEAL, 16, Color.WHITE, 17, Color.YELLOW);
	public static final Map<String, Byte> COLOR_DATA_ASSOCIATIONS = Utils.asMap("§0", (byte) 15, "§1", (byte) 11, "§2", (byte) 13, "§3", (byte) 9, "§4", (byte) 14, "§5", (byte) 2, "§6", (byte) 1, "§7", (byte) 8, "§8", (byte) 7, "§9", (byte) 10, "§a", (byte) 5, "§b", (byte) 3, "§c", (byte) 14, "§d", (byte) 6, "§e", (byte) 4, "§f", (byte) 0);

	public static Color getColor(int i) {
		return COLORS.containsKey(i) ? COLORS.get(i) : Color.AQUA;
	}

	public static Color getRandomBukkitColor() {
		return random(Utils.asList(COLORS.values()));
	}

	public static DyeColor getRandomDyeColor() {
		return random(Utils.asList(DyeColor.values()));
	}

	public static byte getColorData(String color) {
		return COLOR_DATA_ASSOCIATIONS.containsKey(color) ? COLOR_DATA_ASSOCIATIONS.get(color) : (byte) 0;
	}

	public static String getRandomColor() {
		return random(asList(COLOR_DATA_ASSOCIATIONS.keySet()));
	}

	public static void playFirework(Location loc)
	{
		// Spawn the Firework, get the FireworkMeta.

		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		// Get the type

		int rt = RANDOM.nextInt(4) + 1;

		Type type = Type.BALL;
		if (rt == 1) type = Type.BALL;
		if (rt == 2) type = Type.BALL_LARGE;
		if (rt == 3) type = Type.BURST;
		if (rt == 4) type = Type.CREEPER;
		if (rt == 5) type = Type.STAR;

		// Get our random colours  

		int r1i = RANDOM.nextInt(17) + 1;
		int r2i = RANDOM.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);

		// Create our effect with this

		FireworkEffect effect = FireworkEffect.builder().flicker(RANDOM.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(RANDOM.nextBoolean()).build();

		// Then apply the effect to the meta

		fwm.addEffect(effect);

		// Generate some random power and set it

		fwm.setPower(1);

		// Then apply this to our rocket

		fw.setFireworkMeta(fwm);
	}

	// ------------------------------------------------------------
	// Location
	// ------------------------------------------------------------

	public static boolean coordsEquals(Location a, Location b) {
		return a == null || b == null ? a == b : equals(a.getWorld(), b.getWorld()) && a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
	}

	public static Location simplify(Location loc) {
		return new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public static Location cloneLocation(Location loc) {
		return loc == null ? null : new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	/*public static Pair<Location, Location> getMinMax(Location p1, Location p2) {
		// validity and world check
		if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
			return null;
		}
		// coords
		double x1 = p1.getX(), y1 = p1.getY(), z1 = p1.getZ();
		double x2 = p2.getX(), y2 = p2.getY(), z2 = p2.getZ();
		double minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
		double minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
		double minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);

	}*/

	/*public static String serializeLocation(Location location)
	{
		if (location == null || location.getWorld() == null) {
			return null;
		}

		String ser = location.getX() + "s" + location.getY() + "s" + location.getZ() + "s" + location.getYaw() + "s" + location.getPitch();
		ser = ser.replace("-", "n");
		ser = ser.replace(".", "d");
		ser = location.getWorld().getName() + "_" + ser;

		return ser;
	}*/

	public static String serializeWXYZLocation(Location location) {
		if (location == null || location.getWorld() == null) {
			return "null";
		}
		return location.getWorld().getName() + "," + Utils.round(location.getX()) + "," + Utils.round(location.getY()) + "," + Utils.round(location.getZ()) + "," + Utils.round(location.getYaw()) + "," + Utils.round(location.getPitch());
	}

	/*public static Location unserializeLocation(String raw)
	{
		if (raw == null || raw.isEmpty()) {
			return null;
		}

		int wIndex = raw.lastIndexOf("_");
		String w = raw.substring(0, wIndex);
		raw = raw.substring(wIndex + 1);
		World world = Bukkit.getWorld(w);

		if (world == null) {
			return null;
		}

		double x = Double.parseDouble(raw.split("s")[0].replace("d", ".").replace("n", "-"));
		double y = Double.parseDouble(raw.split("s")[1].replace("d", ".").replace("n", "-"));
		double z = Double.parseDouble(raw.split("s")[2].replace("d", ".").replace("n", "-"));
		float yaw = Float.parseFloat(raw.split("s")[3].replace("d", ".").replace("n", "-"));
		float pitch = Float.parseFloat(raw.split("s")[4].replace("d", ".").replace("n", "-"));

		return new Location(world, x, y, z, yaw, pitch);
	}*/

	public static Location unserializeWXYZLocation(String wxyzLocation)
	{
		if (wxyzLocation == null || wxyzLocation.isEmpty() || wxyzLocation.equals("null")) {
			return null;
		}

		String[] split = wxyzLocation.split(",");
		World world = Bukkit.getWorld(split[0]);

		if (world == null) {
			return null;
		}

		double x = Double.parseDouble(split[1]);
		double y = Double.parseDouble(split[2]);
		double z = Double.parseDouble(split[3]);
		float yaw = 90F;
		float pitch = 0F;

		if (split.length > 4) {
			yaw = Float.parseFloat(split[4]);
			pitch = Float.parseFloat(split[5]);
		}

		return new Location(world, x, y, z, yaw, pitch);
	}

	public static boolean isLocInArea(Location loc, Location p1, Location p2) {
		return isLocInArea(loc, p1, p2, true);
	}

	public static boolean isLocInArea(Location loc, Location p1, Location p2, boolean checkY) {
		// validity and world check
		if (loc == null || p1 == null || p2 == null || !loc.getWorld().equals(p1.getWorld()) || !p1.getWorld().equals(p2.getWorld())) {
			return false;
		}
		// coords
		double x1 = p1.getX(), y1 = p1.getY(), z1 = p1.getZ();
		double x2 = p2.getX(), y2 = p2.getY(), z2 = p2.getZ();
		double minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
		double minY = Math.min(y1, y2), maxY = Math.max(y1, y2);
		double minZ = Math.min(z1, z2), maxZ = Math.max(z1, z2);
		if (loc.getBlockX() < minX || loc.getBlockX() > maxX || loc.getBlockZ() < minZ || loc.getBlockZ() > maxZ || (checkY && (loc.getBlockY() < minY || loc.getBlockY() > maxY))) {
			return false;
		}
		return true;
	}

	public static boolean isChunkInArea(Chunk chunk, Location p1, Location p2) {
		// chunk validity
		if (chunk == null || chunk.getWorld() == null) {
			return false;
		}
		// return
		return isLocInArea(chunk.getBlock(8, 64, 8).getLocation(), p1, p2);
	}

	public static double distance(Location a, Location b) {
		return a == null || b == null || a.getWorld() == null || b.getWorld() == null || !a.getWorld().equals(b.getWorld()) ? -1.0D : a.distance(b);
	}

	public static String getArrowString(Location from, Location to) {
		if (from != null && to != null && from.getWorld().equals(to.getWorld())) {
			from = from.clone();
			to = to.clone();
			if (from.getWorld().equals(to.getWorld())) {
				from.setY(0);
				to.setY(0);
				Vector d = from.getDirection();
				Vector v = to.subtract(from).toVector().normalize();
				double a = Math.toDegrees(Math.atan2(d.getX(), d.getZ()));
				a -= Math.toDegrees(Math.atan2(v.getX(), v.getZ()));
				a = (int) (a + 22.5) % 360;

				if (a < 0) a += 360;
				return "§l" + "⬆⬈➡⬊⬇⬋⬅⬉".charAt((int) a / 45);
			}
		}
		return "?";
	}

	public static boolean isCrops(Block block) {
		return block != null && block.getState().getData() instanceof Crops;
	}

	public static boolean isFullyGrown(Block block) {
		if (block == null) return false;
		MaterialData mat = block.getState().getData();
		return mat instanceof Crops ? ((Crops) mat).getState().equals(CropState.RIPE) : false;
	}

	public static boolean setGrowthStage(Block block, int stage) {
		for (int i = 0; i < CropState.values().length; ++i) {
			if (i == stage) {
				return setGrowthStage(block, CropState.values()[i]);
			}
		}
		return false;
	}

	public static boolean setGrowthStage(Block block, CropState stage) {
		if (block == null) return false;
		MaterialData mat = block.getState().getData();
		if (mat instanceof Crops) {
			Crops crops = (Crops) mat;
			crops.setState(stage);
			block.getState().update(true);
		}
		return false;
	}

	public static boolean isIgnited(Block block) {
		if (block == null) return false;
		for (BlockFace face : BlockFace.values()) {
			if (Mat.from(block.getRelative(face).getType()).equals(Mat.FIRE)) {
				return true;
			}
		}
		return false;
	}

	/*public static boolean typeEquals(Material main, Material second) {
		if (!main.equals(second)) {
			String first = new StringBuilder().append(main).toString();
			String sec = new StringBuilder().append(second).toString();
			if (!first.equals(sec)) {
				return false;
			}
		}
		return true;
	}*/

	public static Set<Block> getBlocksAround(Location center, double radius) {
		int offset = (int) Math.floor(radius);
		return getBlocksAround(center, radius, offset, offset, offset);
	}

	public static Set<Block> getBlocksAround(Location center, double radius, int offx, int offy, int offz) {
		Set<Block> result = new HashSet<Block>();
		for (int x = -offx; x <= offx; x++) {
			for (int z = -offy; z <= offy; z++) {
				for (int y = -offz; y <= offz; y++) {
					Location loc = new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z);
					if (distance(loc, center) <= radius) {
						result.add(loc.getBlock());
					}
				}
			}
		}
		return result;
	}

	// ------------------------------------------------------------
	// Items
	// ------------------------------------------------------------

	public static String serializeItemKey(ItemStack item, boolean withAmount) {
		if (item == null) {
			return "null";
		}
		ItemMeta meta = item.getItemMeta();
		Mat mat = Mat.from(item);
		String key = mat.getModernName() + "-" + mat.getLegacyData() + "-" + mat.getDurability() + (withAmount ? "-" + item.getAmount() : "");
		for (Entry<Enchantment,Integer> ench : item.getEnchantments().entrySet()) {
			key += ench.getKey().getName() + "#" + ench.getValue();
		}
		if (meta != null && meta instanceof SkullMeta && ((SkullMeta) meta).getOwner() != null) {
			key += ((SkullMeta) meta).getOwner();
		}
		if (meta != null && meta instanceof PotionMeta) {
			for (PotionEffect eff : ((PotionMeta) meta).getCustomEffects()) {
				key += eff.getType().getName() + "#" + eff.getAmplifier() + "#" + eff.getDuration();
			}
		}
		if (meta != null && meta.getDisplayName() != null && !meta.getDisplayName().isEmpty()) {
			key += meta.getDisplayName();
		}
		if (meta != null && meta.getLore() != null && !meta.getLore().isEmpty()) {
			key += Utils.asNiceString(meta.getLore(), false);
		}
		return key;
	}

	public static final String serializeItem(ItemStack item) {
		if (item == null) {
			return "null";
		}

		ItemMeta meta = item.getItemMeta();
		String serializedItemStack = "t@@@" + Mat.from(item).getModernName();

		if (item.getDurability() != 0)
		{
			String isDurability = String.valueOf(item.getDurability());
			serializedItemStack += ":::dr@@@" + isDurability;
		}

		if (!IS_1_13 && item.getData().getData() != 0)
		{
			String isData = String.valueOf(item.getData().getData());
			serializedItemStack += ":::d@@@" + isData;
		}

		if (item.getAmount() != 1)
		{
			String isAmount = String.valueOf(item.getAmount());
			serializedItemStack += ":::a@@@" + isAmount;
		}

		for (Entry<Enchantment,Integer> ench : item.getEnchantments().entrySet()) {
			serializedItemStack += ":::e@@@" + ench.getKey().getName() + "@@@" + ench.getValue();
		}

		if (meta != null && meta instanceof SkullMeta && ((SkullMeta) meta).getOwner() != null)
		{
			String owner = ((SkullMeta) meta).getOwner();
			serializedItemStack += ":::o@@@" + owner;
		}

		if (meta != null && meta instanceof PotionMeta)
		{
			for (PotionEffect eff : ((PotionMeta) meta).getCustomEffects()) {
				serializedItemStack += ":::eff@@@" + eff.getType().getName() + "@@@" + eff.getDuration() + "@@@" + eff.getAmplifier();
			}
		}

		if (meta != null && meta.getDisplayName() != null && !meta.getDisplayName().isEmpty())
		{
			String name = meta.getDisplayName();
			serializedItemStack += ":::n@@@" + name;
		}

		if (meta != null && meta.getLore() != null && !meta.getLore().isEmpty())
		{
			List<String> lore = new ArrayList<String>(meta.getLore());
			serializedItemStack += ":::l";

			for (String str : lore) {
				serializedItemStack += "@@@" + str;
			}
		}

		if (meta != null && meta.getDisplayName() != null && !meta.getDisplayName().isEmpty())
		{
			Object nbt = Compat.INSTANCE.getNbt(item);
			if (nbt != null) {
				try {
					serializedItemStack += ":::nbt@@@" + Compat.INSTANCE.serializeNbt(nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return serializedItemStack;
	}

	public static final ItemStack unserializeItem(String serialized)
	{
		if (serialized == null || serialized.equals("null")) {
			return null;
		}

		ItemStack item = null;
		String[] serializedItemStack = serialized.split(":::");
		boolean createdItemStack = false;

		for (String itemInfo : serializedItemStack)
		{
			String[] itemAttribute = itemInfo.split("@@@");

			if (itemAttribute[0].equals("t")) {
				Mat mat = Mat.from(itemAttribute[1], 0);
				if (mat != null) {
					item = mat.getNewCurrentStack();
					createdItemStack = true;
				}
			}
			else if (itemAttribute[0].equals("dr") && createdItemStack) {
				item.setDurability(Short.valueOf(itemAttribute[1]));
			}
			else if (itemAttribute[0].equals("d") && createdItemStack && !IS_1_13) {
				try {
					item.setData(new MaterialData(item.getType(), Byte.valueOf(itemAttribute[1])));
				} catch (Throwable exception) {
				}
			}
			else if (itemAttribute[0].equals("a") && createdItemStack) {
				item.setAmount(Integer.valueOf(itemAttribute[1]));
			}
			else if (itemAttribute[0].equals("e") && createdItemStack) {
				item.addUnsafeEnchantment(Enchantment.getByName(itemAttribute[1]), Integer.valueOf(itemAttribute[2]));
			}
			else if (itemAttribute[0].equals("o") && createdItemStack)
			{
				ItemMeta meta = item.getItemMeta();
				((SkullMeta) meta).setOwner(itemAttribute[1]);
				item.setItemMeta(meta);
			}
			else if (itemAttribute[0].equals("eff") && createdItemStack)
			{
				ItemMeta meta = item.getItemMeta();
				((PotionMeta) meta).addCustomEffect(new PotionEffect(PotionEffectType.getByName(itemAttribute[1]), Integer.parseInt(itemAttribute[2]), Integer.parseInt(itemAttribute[3])), true);
				item.setItemMeta(meta);
			}
			else if (itemAttribute[0].equals("n") && createdItemStack)
			{
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(itemAttribute[1]);
				item.setItemMeta(meta);
			}
			else if (itemAttribute[0].equals("l") && createdItemStack)
			{
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();

				for (int i = 1; i < itemAttribute.length; i++) {
					lore.add(itemAttribute[i]);
				}

				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			else if (itemAttribute[0].equals("nbt") && createdItemStack) {
				try {
					Object nbt = Compat.INSTANCE.unserializeNbt(itemAttribute[1]);
					item = Compat.INSTANCE.setNbt(item, nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return item;
	}

	public static boolean equalsOrMoreAmount(ItemStack a, ItemStack b) {
		return a == null || b == null ? a == b : a.isSimilar(b) && a.getAmount() >= b.getAmount();
	}

	public static boolean hasEffects(ThrownPotion potion, List<PotionEffectType> effects) {
		for (PotionEffect effect : potion.getEffects()) {
			if (effects.contains(effect.getType())) {
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------
	// Numbers
	// ------------------------------------------------------------

	private static DecimalFormat FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(java.util.Locale.US));

	static {
		FORMAT.setRoundingMode(RoundingMode.FLOOR);
	}

	public static String round(double value) {
		return value % 1 == 0 ? String.valueOf((int) value) : FORMAT.format(value);
	}

	public static String round(BigDecimal value) {
		String string = value.toPlainString();
		int index = string.indexOf('.');
		if (index >= 0 && index + 2 < string.length()) {
			return string.substring(0, index + 3);
		}
		return string;
	}

	public static double randomDouble(double min, double max) {
		double r = RANDOM.nextDouble();
		if (r <= 0.5) {
			return ((1 - RANDOM.nextDouble()) * (max - min) + min);
		}
		return (RANDOM.nextDouble() * (max - min) + min);
	}

	public static double round(double value, int places)
	{
		if (places <= 0) {
			return value;
		}

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static int getRoundedUp(double nb)
	{
		try
		{
			int nonDecimals = Integer.parseInt(String.valueOf(nb).split(".")[0]);
			int decimals = Integer.parseInt(String.valueOf(nb).split(".")[1]);

			return decimals > 0 ? nonDecimals + 1 : nonDecimals;
		}
		catch (Throwable exception) {
			return new Double(nb).intValue();
		}
	}

	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Throwable ignored) {
			return false;
		}
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Throwable ignored) {
			return false;
		}
	}

	public static Integer integerOrNull(String raw) {
		try {
			return Integer.valueOf(raw);
		} catch (Throwable ignored) {
			return null;
		}
	}

	public static Double doubleOrNull(String raw) {
		try {
			return Double.valueOf(raw);
		} catch (Throwable ignored) {
			return null;
		}
	}

	// ------------------------------------------------------------
	// String
	// ------------------------------------------------------------

	/*private static final Map<String, String> COLOR_DARKNESS = Utils.asMap("§a", "§2", "§b", "§3", "§c", "§4", "§d", "§5", "§e", "§6", "§7", "§8", "§9", "§1");

	public static String darkenColor(String color)
	{
		if (COLOR_DARKNESS.containsKey(color)) {
			return COLOR_DARKNESS.get(color);
		}

		return color;
	}*/

	public static String fillPlaceholderAPI(Player player, String text) {
		if (text == null) return null;
		if (Utils.isPluginEnabled("PlaceholderAPI")) {
			return PlaceholderAPIHandler.fill(player, text);
		} else {
			return text;
		}
	}

	public static List<String> fillPlaceholderAPI(Player player, List<String> text) {
		if (text == null || text.isEmpty()) return text;
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
			return PlaceholderAPIHandler.fill(player, text);
		} else {
			return text;
		}
	}

	public static <T> boolean keysEquals(Map<String, T> map1, Map<String, T> map2) {
		List<String> keys1 = new ArrayList<String>(map1.keySet()), keys2 = new ArrayList<String>(map2.keySet());
		return keys1.equals(keys2);
	}

	public static List<String> separateSentences(List<String> text, int maxLength, String addToSeparated) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < text.size(); ++i) {
			// separate line
			String line = text.get(i);
			List<String> lineSeparated = separateSentence(line, maxLength, addToSeparated);
			// combine with last result line
			if (result.isEmpty()) {
				result.addAll(lineSeparated);
			} else {
				boolean first = false;
				for (String separated : lineSeparated) {
					if (first) {
						first = false;
						result.set(result.size() - 1, result.get(result.size() - 1).concat(separated));
					} else {
						result.add(separated);
					}
				}
			}
		}
		return result;
	}

	public static List<String> separateSentence(String text, int maxLength, String addToSeparated) {
		if (text.length() <= maxLength) {
			return asList(text);
		}
		List<String> result = new ArrayList<String>();
		String left = text;
		while (left.length() > maxLength) {
			int index = left.lastIndexOf(" ", maxLength);
			if (index == -1) index = maxLength;
			result.add((result.isEmpty() ? "" : addToSeparated + getLastColor(result.get(0))) + left.substring(0, index));
			left = left.substring(index + 1);
		}
		result.add((result.isEmpty() ? "" : addToSeparated + getLastColor(result.get(0))) + left);
		return result;
	}

	public static String getLastColor(String text) {
		text = format(text);
		int index = text.length();
		String currentColor = "";
		while (index >= 0) {
			index = text.lastIndexOf("§");
			if (index + 1 < text.length()) {
				String color = "§" + text.charAt(index + 1);
				currentColor = color + currentColor;
				// is a color and not a format, break
				if (COLOR_DATA_ASSOCIATIONS.containsKey(color)) {
					break;
				}
			}
			if (index >= 0) {
				text = text.substring(0, index);
			}
		}
		return currentColor;
	}

	public static List<String> split(String separator, String string, boolean allowEmpty) {
		return split(separator, string, allowEmpty, true);
	}

	public static List<String> split(String separator, String string, boolean allowEmpty, boolean addStringIfNone) {
		List<String> split = new ArrayList<String>();
		for (String str : string.split(separator, allowEmpty ? -1 : 0)) {
			str = str == null ? "" : str;
			if (allowEmpty ? true : !str.isEmpty()) {
				split.add(str == null ? "" : str);
			}
		}
		if (split.isEmpty() && addStringIfNone) {
			split.add(string);
		}
		return split;
	}

	public static List<String> split(String string, char separator, char omitBegin, char omitEnd, boolean allowEmpty) {
		if (!string.contains("" + separator)) {
			return Utils.asList(string);
		}

		List<String> split = new ArrayList<String>();
		boolean isOmit = false;
		String current = "";
		char[] array = string.toCharArray();

		for (int i = 0; i < array.length; i++) {
			char c = array[i];
			// split
			if (c == separator && !isOmit) {
				if (current.isEmpty() && !allowEmpty) {}
				else {
					split.add(current);
				}

				current = "";
				continue;
			}
			// omit start
			else if (c == omitBegin) {
				isOmit = true;
			}
			// omit end
			else if (c == omitEnd) {
				isOmit = false;
			}

			current += c;

			if (i + 1 == array.length && !split.contains(current)) {
				split.add(current);
			}
		}

		if (split.isEmpty()) {
			split.add(string);
		}

		return split;
	}

	public static String getPluralFor(String word, int count) {
		return word + getPlural(count);
	}

	public static String getPluralFor(String word, int count, String plural) {
		return word + getPlural(plural, count);
	}

	public static String getPlural(int amount) {
		return getPlural("s", amount);
	}

	public static String getPlural(String pluralName, int amount) {
		return amount > 1 ? pluralName : "";
	}

	public static String format(String toFormat)
	{
		if (toFormat == null) return null;
		return ChatColor.translateAlternateColorCodes('&', toFormat);
	}

	public static List<String> format(Collection<String> toFormat)
	{
		if (toFormat == null) return null;
		List<String> result = new ArrayList<String>();

		for (String str : toFormat) {
			result.add(format(str));
		}

		return result;
	}

	public static String unformat(String toUnformat)
	{
		if (toUnformat == null) return null;
		return ChatColor.stripColor(toUnformat);
	}

	public static List<String> unformat(Collection<String> toUnformat)
	{
		if (toUnformat == null) return null;
		List<String> result = new ArrayList<String>();

		for (String str : toUnformat) {
			result.add(unformat(str));
		}

		return result;
	}

	public static List<String> addBeforeAll(Collection<String> list, String toAdd)
	{
		if (list == null) return null;
		List<String> result = new ArrayList<String>();
		for (String str : list) result.add(toAdd + str);
		return result;
	}

	public static List<String> alterElements(Collection<String> list, String prefix, String suffix) {
		if (list == null) return null;
		List<String> result = new ArrayList<String>();
		for (String str : list) result.add(prefix + str + suffix);
		return result;
	}

	public static List<String> addElementsAfter(Collection<String> list, String... toAdd) {
		List<String> l = asList(list);
		for (String str : toAdd) {
			l.add(str);
		}
		return l;
	}

	public static boolean containsIgnoreCase(Collection<String> list, String toCheck) {
		for (String str : list) {
			if (str.equalsIgnoreCase(toCheck)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> replaceAll(List<String> list, Object... objects) {
		if (objects.length != 0 && objects.length % 2 != 0) {
			throw new IllegalArgumentException("size isn't a multiple of 2");
		}
		return new Replacer(objects).apply(list);
	}

	public static String replace(String string, String replacer, String... toReplace) {
		for (String toRec : toReplace) {
			string = string.replace(toRec, replacer);
		}
		return string;
	}

	public static List<String> replaceAllLinesWith(String value, Collection<String> list, Collection<String> replaceWith) {
		List<String> result = new ArrayList<String>();
		for (String str : list) {
			if (str != null) {
				if (str.contains(value)) {
					for (String replace : replaceWith) {
						if (replace != null) {
							result.add(replace);
						}
					}
				} else {
					result.add(str);
				}
			}
		}
		return result;
	}

	public static String reverseWords(String original) {
		char[] chars = original.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ' ') {
				return reverseWords(original.substring(i + 1)) + original.substring(0, i) + " ";
			}
		}
		return original + " ";
	}

	public static String capitalizeFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return Character.toUpperCase(original.charAt(0)) + original.substring(1);
	}

	public static String uncapitalizeFirstLetter(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return Character.toLowerCase(original.charAt(0)) + original.substring(1);
	}

	// so basically an enum value
	public static String capitalizeUnderscores(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		StringBuilder result = new StringBuilder(original.length() - countChars('_', original));
		result.append(Character.toUpperCase(original.charAt(0)));
		for (int i = 1; i < original.length(); ++i) {
			char c = original.charAt(i);
			if (c == '_') {
				result.append(' ');
				if (++i < original.length()) {
					result.append(Character.toUpperCase(original.charAt(i)));
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	public static String separateOnCaps(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		StringBuilder result = new StringBuilder(original.length());
		for (int i = 0; i < original.length(); ++i) {
			char c = original.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) result.append(' ');
				result.append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	// https://stackoverflow.com/questions/12831719/fastest-way-to-check-a-string-is-alphanumeric-in-java
	public static boolean isAlphanumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a) {
				return false;
			}
		}
		return true;
	}

	public static List<String> toList(String[][] array) {
		List<String> result = emptyList();
		if (array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				String line = "";
				for (int j = 0; j < array[i].length; j++) {
					line += array[i][j];
				}
				result.add(line);
			}
		}
		return result;
	}

	public static Pair<String, String[]> separateRoot(String string, boolean allowEmpty)
	{
		List<String> split = split(" ", string, allowEmpty);

		if (split.size() == 0) {
			return new Pair<String, String[]>("none", new String[0]);
		}

		String root = split.get(0);
		String[] others = new String[split.size() - 1];

		for (int i = 1; i < split.size(); i++) {
			String other = split.get(i);
			others[i - 1] = other;
		}

		return new Pair<String, String[]>(root, others);
	}

	public static Pair<String, String> separateRootAtChar(String string, char separator) {
		int index = string.indexOf(separator);
		return index != -1 ? new Pair<String, String>(string.substring(0, index), string.substring(index + 1, string.length())) : new Pair<String, String>(string, null);
	}

	private static String lastInventoryName = null;
	//private static int inventoryNameSizeTolerance = 31;
	//private static int inventoryNameSizeTolerance = 27;

	/*public static String getNewInventoryName(String previousName, String newName) {
		String thisName = "";
		String totalName = previousName == null || previousName.isEmpty() ? newName : previousName + "§r/" + newName;
		if (totalName.length() > inventoryNameSizeTolerance) {
			int startIndex = totalName.length() - inventoryNameSizeTolerance;
			if (startIndex > 0 && totalName.charAt(startIndex - 1) == '§') startIndex--;
			String substring = totalName.substring(startIndex, totalName.length());
			thisName = (substring.equals(totalName)) ? substring : "..." + substring;
		} else {
			thisName = totalName;
		}
		return lastInventoryName = thisName;
	}*/

	public static String getNewInventoryName(String previousName, String newName) {
		return lastInventoryName = (previousName != null && !previousName.isEmpty() ? previousName + "§r/" + newName : newName);
	}

	public static String getLastInventoryName() {
		return lastInventoryName;
	}

	public static int getInventorySize(int contentSize) {
		int size = 9;
		while (size < contentSize && size <= 54) {
			size += 9;
		}
		return size;
	}

	/**
	 * @return un nombre qui dépend de la similarité des deux chaînes de caractère. Même chaînes = 0. Chaîne de similarité potentielle = environ 5.
	 */

	public static int getLevenshteinSimilarity(String s1, String s2)
	{
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		int[] costs = new int[s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++)
		{
			int lastValue = i;

			for (int j = 0; j <= s2.length(); j++)
			{
				if (i == 0) {
					costs[j] = j;
				}
				else
				{
					if (j > 0)
					{
						int newValue = costs[j - 1];

						if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						}

						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}

			if (i > 0) {
				costs[s2.length()] = lastValue;
			}
		}

		return costs[s2.length()];
	}

	public static boolean contains(String s, String... contains)
	{
		for (String cont : contains) {
			if (s.contains(cont)) {
				return true;
			}
		}

		return false;
	}

	public static boolean containsOne(Collection<?> coll, Collection<?> toContains) {
		for (Object collObj : coll) {
			for (Object obj : toContains) {
				if (equals(collObj, obj)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean equals(String s, String... equals) {
		for (String eq : equals) {
			if (s.equals(eq)) {
				return true;
			}
		}
		return false;
	}

	public static boolean equalsIgnoreCase(String s, String... equals) {
		for (String eq : equals) {
			if (s.equalsIgnoreCase(eq)) {
				return true;
			}
		}
		return false;
	}

	public static int countChars(char ch, String str)
	{
		int result = 0;

		for (char c : str.toCharArray()) {
			if (c == ch) {
				result++;
			}
		}

		return result;
	}

	public static String copyString(String str, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			builder.append(str);
		}
		return builder.toString();
	}

	public static boolean isNull(Object obj) {// TODO : what in the world is that thing
		try {
			obj.getClass();
			return obj == null ? true : false;
		} catch (Throwable ignored) {
			return true;
		}
	}

	// ------------------------------------------------------------
	// Object
	// ------------------------------------------------------------

	public static <T> boolean equals(T a, T b) {
		return a == null || b == null ? a == b : a.equals(b);
	}

	public static <T> boolean instanceOf(T obj, Class<?> typeClass) {
		return typeClass == null ? obj == null : (obj == null ? false : typeClass.isAssignableFrom(obj.getClass()));
	}

	public static <T> boolean instanceOf(Class<?> objClass, Class<?> typeClass) {
		return typeClass == null ? objClass == null : (objClass == null ? false : typeClass.isAssignableFrom(objClass));
	}

	public static boolean isNumeric(String str) {
		if (str.contains("-") && !str.startsWith("-")) {
			return false;
		}
		boolean minus = false, dots = false;
		for (char c : str.toCharArray()) {
			if (c == '-') {
				if (minus) {
					return false;
				} else {
					minus = true;
					continue;
				}
			}
			if (c == ',' || c == '.') {
				if (dots) {
					return false;
				} else {
					dots = true;
					continue;
				}
			}
			if (c >= '0' && c <= '9') {
				continue;
			}
			return false;
		}
		return true;
	}

	// ------------------------------------------------------------
	// Collection
	// ------------------------------------------------------------

	public static List<String> emptyList() {
		return new ArrayList<String>();
	}

	public static <T> List<T> emptyList(Class<T> typeClass) {
		return new ArrayList<T>();
	}

	public static <T> List<T> asList(T... ts)
	{
		List<T> list = new ArrayList<T>();
		if (ts != null) {
			for (T t : ts) list.add(t);
		}
		return list;
	}

	public static <T> List<T> asList(Collection<T> t)
	{
		List<T> list = new ArrayList<T>();
		if (t != null) {
			list.addAll(t);
		}
		return list;
	}

	@SafeVarargs
	public static <T> Set<T> asSet(T... ts)
	{
		HashSet<T> list = new HashSet<T>();
		if (ts != null) {
			for (T t : ts) list.add(t);
		}
		return list;
	}

	public static <T> Set<T> asSet(Collection<T> t)
	{
		HashSet<T> list = new HashSet<T>();
		if (t != null) {
			list.addAll(t);
		}
		return list;
	}

	@SafeVarargs
	public static <T> List<T> asListMultiple(Class<T> clazz, Object... objects)
	{
		List<T> list = new ArrayList<T>();

		for (Object obj : objects) {
			if (obj != null) {
				if (obj instanceof Collection) {
					list.addAll((Collection<T>) obj);
				} else if (obj instanceof Object[]) {
					for (T t : (T[]) obj) list.add(t);
				} else {
					list.add((T) obj);
				}
			}
		}

		return list;
	}

	@SafeVarargs
	public static <T> List<T> asListMultiple(Object... objects)
	{
		List<T> list = new ArrayList<T>();

		for (Object obj : objects) {
			if (obj != null) {
				if (obj instanceof Collection) {
					list.addAll((Collection<T>) obj);
				} else if (obj instanceof Object[]) {
					for (T t : (T[]) obj) list.add(t);
				} else {
					list.add((T) obj);
				}
			}
		}

		return list;
	}

	public static <T> List<T> asReverseList(Collection<T> list)
	{
		List<T> copy = asList(list);
		if (!copy.isEmpty()) {
			Collections.reverse(copy);
		}
		return copy;
	}

	public static <T> List<T> asShuffleList(Collection<T> list)
	{
		List<T> copy = asList(list);
		if (!copy.isEmpty()) {
			Collections.shuffle(copy);
		}
		return copy;
	}

	public static <T> List<T> asShuffleList(Collection<T> list, Random random)
	{
		List<T> copy = asList(list);
		if (!copy.isEmpty()) {
			Collections.shuffle(copy, random);
		}
		return copy;
	}

	public static <T extends Comparable<? super T>> List<T> asObjectSortedList(Collection<T> list) {
		return asSortedList(list, objectSorter);
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> list) {
		List<T> copy = asList(list);
		Collections.sort(copy);
		return copy;
	}

	public static <T> List<T> asSortedList(Collection<T> list, Comparator<? super T> comparator) {
		List<T> copy = asList(list);
		sortList(copy, comparator);
		return copy;
	}

	public static <T> void sortList(List<T> list, Comparator<? super T> comparator) {
		if (!list.isEmpty()) {
			Collections.sort(list, comparator);
		}
	}

	public static final Comparator<Object> objectSorter = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) return o2 == null ? 0 : -1;
			if (o2 == null) return 1;// o1 can't be null here
			return String.CASE_INSENSITIVE_ORDER.compare(fourDigitsNumber(o1.toString()), fourDigitsNumber(o2.toString()));
		}
	};

	public static final String fourDigitsNumber(String str) {
		if (Utils.isInteger(str)) {
			while (str.length() < 4) {
				str = "0" + str;
			}
		}
		return str;
	}

	public static final Comparator<PotionEffectType> effectSorter = new Comparator<PotionEffectType>() {
		@Override
		public int compare(PotionEffectType o1, PotionEffectType o2) {
			if (o1 == null) return o2 == null ? 0 : -1;
			if (o2 == null) return 1;// o1 can't be null here
			return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
		}
	};

	@SafeVarargs
	public static <TK, TV> Map<TK, TV> asMap(Object... objects)
	{
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		Map<TK, TV> map = new HashMap<TK, TV>();

		for (int i = 0; i < objects.length; i += 2) {
			map.put((TK) objects[i], (TV) objects[i + 1]);
		}

		return map;
	}

	public static <TK, TV> Map<TK, TV> asMapCopy(Map<TK, TV> map)
	{
		Map<TK, TV> copy = new HashMap<TK, TV>();

		for (TK key : map.keySet()) {
			copy.put(key, map.get(key));
		}

		return copy;
	}

	public <K, V> K getKeyByValue(Map<K, V> map, V v)
	{
		for (K k : map.keySet())
		{
			if (map.get(k).equals(v))
				return k;
		}

		return null;
	}

	public static <K, V> K getKeyAtPosition(Map<K, V> map, int index)
	{
		if (index >= map.size()) {
			throw new IndexOutOfBoundsException("Index : " + index +", size : " + map.size());
		}

		Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
		int i = 0;

		while (iterator.hasNext()) {
			if (i == index) {
				return iterator.next().getKey();
			} else {
				iterator.next();
				i++;
			}
		}

		return null;
	}

	public static <K, V> Map<K, V> asClone(Map<K, V> map)
	{
		Map<K, V> clone = new HashMap<K, V>();

		for (K k : map.keySet()) {
			clone.put(k, map.get(k));
		}

		return clone;
	}

	public static <T> T randomColl(Collection<T> list) {
		return list.size() == 0 ? null : Utils.asList(list).get(RANDOM.nextInt(list.size()));
	}

	public static <T> T random(List<T> list) {
		return list.size() == 0 ? null : list.get(RANDOM.nextInt(list.size()));
	}

	public static <T> T random(List<T> list, List<T> exclude) {
		List<T> copy = new ArrayList<T>();
		for (T t : list) {
			if (!exclude.contains(t)) {
				copy.add(t);
			}
		}
		return random(copy);
	}

	public static int random(int min, int max) {
		return min + random(max - min + 1);
	}

	public static int random(int range) {
		return range <= 0 ? 0 : RANDOM.nextInt(range);
	}

	public static String randomFakeIP() {
		return random(0, 255) + "." + random(0, 255) + "." + random(0, 255) + "." + random(0, 255);
	}

	public static <T> String asNiceString(Collection<T> list, boolean spaces) {
		if (list == null || list.isEmpty()) {
			return "/";
		}
		String str = "";
		for (T t : list) str += String.valueOf(t) + "," + (spaces ? " " : "");
		str = str.substring(0, str.length() - ("," + (spaces ? " " : "")).length());
		return str;
	}

	public static List<String> fromNiceString(String string, boolean spaces) {
		if (string == null || string.isEmpty()) {
			return emptyList();
		}
		return Utils.split(spaces ? ", " : ",", string, true);
	}

	public static <T> String asNiceString(T[] array, boolean spaces) {
		if (array.length == 0) {
			return "/";
		}
		String str = "";
		for (T t : array) str += String.valueOf(t) + ", ";
		str = str.substring(0, str.length() - ", ".length());
		return spaces ? str : str.replace(" ", "");
	}

	public static <T> String asString(T[] array)
	{
		if (array.length == 0) {
			return "[]";
		}

		String str = "[ ";
		for (T t : array) str += "'" + String.valueOf(t) + "', ";
		str = str.substring(0, str.length() - ", ".length()) + " ]";
		return str;
	}

	public static <T> String asString(Collection<T> list)
	{
		if (list.isEmpty()) {
			return "[]";
		}

		String str = "[ ";
		for (T t : list) str += "'" + String.valueOf(t) + "', ";
		str = str.substring(0, str.length() - ", ".length()) + " ]";
		return str;
	}

	public static <K, V> String asString(Map<K, V> map)
	{
		if (map.isEmpty()) {
			return "{}";
		}

		String str = "{ ";
		for (K k : map.keySet()) str += "['" + String.valueOf(k) + "', '" + String.valueOf(map.get(k)) + "'], ";
		str = str.substring(0, str.length() - ", ".length()) + " }";
		return str;
	}

	public static List<String> resizeStringAsList(String string, int maxLineSize) {
		List<String> list = new ArrayList<String>();
		for (String str : string.split("(?<=\\G.{" + maxLineSize + "})")) {
			list.add(str);
		}
		return list;
	}

	public static <T> int countElement(Collection<? extends T> list, T obj) {
		int count = 0;
		for (T elem : list) {
			if (equals(obj, elem)) {
				count++;
			}
		}
		return count;
	}

	public static <T> T getSetElement(Set<? extends T> list, int index) {
		if (index < 0 || index >= list.size()) {
			throw new IndexOutOfBoundsException("size " + list.size() + ", index " + index);
		}
		Iterator<? extends T> iterator = list.iterator();
		int i = -1;
		while (iterator.hasNext()) {
			i++;
			if (i == index) {
				return iterator.next();
			} else {
				iterator.next();
			}
		}
		return null;
	}

	public static boolean isContentEmpty(Collection<String> list) {
		for (String str : list) {
			if (!Utils.unformat(str).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public static <T> int indexOf(List<? extends T> object, List<? extends T> list) {
		Integer last = null;
		int equals = -1;
		for (int i = 0; i < list.size(); ++i) {
			int peek = equals + 1;
			if (peek < object.size() && equals(object.get(peek), list.get(i))) {
				equals = peek;
				if (last == null) last = i;
				else if (equals == object.size()) return last;
			} else {
				equals = 0;
				last = null;
			}
		}
		return equals;
	}

	// ------------------------------------------------------------
	// Time
	// ------------------------------------------------------------

	public static long getMillisSince(long oldTime)
	{
		if (oldTime == 0L) {
			return -1L;
		} else {
			return System.currentTimeMillis() - oldTime;
		}
	}

	public static long getSecondsInMillis(int seconds) {
		return (long) (seconds) * 1000L;
	}

	public static long getMinutesInMillis(int minutes) {
		return (long) (minutes) * 60L * 1000L;
	}

	public static long getHoursInMillis(int hours) {
		return (long) (hours) * 60L * 60L * 1000L;
	}

	public static int getMillisInSeconds(long millis) {
		return (int) (millis / 1000L);
	}

	public static int getMillisInMinutes(long millis) {
		return (int) (getMillisInSeconds(millis) / 60);
	}

	public static int getMillisInHours(long millis) {
		return (int) (getMillisInSeconds(millis) / 60);
	}

	public static long getSecondsInTicks(int seconds) {
		return (long) (seconds) * 20L;
	}

	public static long getMinutesInTicks(int minutes) {
		return getSecondsInTicks(minutes * 60);
	}

	public static long getHoursInTicks(int hours) {
		return getSecondsInTicks(hours * 60 * 60);
	}

	public static String formatDurationSeconds(int seconds)
	{
		if (seconds < 60) {
			return PCLocale.MISC_GENERIC_TIMEFORMATSECONDS.getLines("{seconds}", twoDigitString(seconds % 60)).get(0);
		} else if (seconds < 3600) {
			return PCLocale.MISC_GENERIC_TIMEFORMATMINUTES.getLines("{minutes}", ((seconds % 3600) / 60), "{seconds}", twoDigitString(seconds % 60)).get(0);
		} else if (seconds < 86400) {
			return PCLocale.MISC_GENERIC_TIMEFORMATHOURS.getLines("{hours}", (seconds / 3600), "{minutes}", ((seconds % 3600) / 60), "{seconds}", twoDigitString(seconds = seconds % 60)).get(0);
		} else {
			return PCLocale.MISC_GENERIC_TIMEFORMATDAYS.getLines("{days}", (seconds / 86400), "{hours}", ((seconds / 86400 * 24) - (seconds / 3600)), "{minutes}", ((seconds % 3600) / 60), "{seconds}", twoDigitString(seconds = seconds % 60)).get(0);
		}
	}

	public static String formatDurationMillis(long millis) {
		return formatDurationSeconds(Utils.getMillisInSeconds(millis));
	}

	public static String formatDate(long millis) {
		return Utils.DATE_FORMAT.format(new Date(millis));
	}

	private static String twoDigitString(int number)
	{
		if (number == 0) {
			return "00";
		} else if (number / 10 == 0) {
			return "0" + number;
		} else {
			return String.valueOf(number);
		}
	}

	// https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form, that guy is a real programmer, Pog
	public static double calculateExpression(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ') nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			//        | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if      (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if      (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else if (func.equals("log")) x = Math.log(x);
					else if (func.equals("ceil")) x = Math.ceil(x);
					else if (func.equals("floor")) x = Math.floor(x);
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char)ch);
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}

}
