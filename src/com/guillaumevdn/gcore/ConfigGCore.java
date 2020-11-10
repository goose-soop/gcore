package com.guillaumevdn.gcore;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.GPluginConfig;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mats;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTBase;
import com.guillaumevdn.gcore.lib.compatibility.particle.Particles;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sounds;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variants;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.data.MySQL;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.function.ThrowableSupplier;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.location.position.PositionTypes;
import com.guillaumevdn.gcore.lib.logging.LogLevel;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScript;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScriptDecoder;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameTypes;
import com.guillaumevdn.gcore.lib.tuple.GUIItemTriple;

/**
 * @author GuillaumeVDN
 */
public class ConfigGCore extends GPluginConfig {

	public static YMLConfiguration baseConfig;

	// commands aliases
	public static List<String> commandsAliasesPlugins;
	public static List<String> commandsAliasesReload;
	public static List<String> commandsAliasesItemRead;
	public static List<String> commandsAliasesExport;

	// variants
	public static Mats mats = null;
	public static Sounds sounds;
	public static Particles particles;

	// types
	public static LowerCaseHashMap<ParticleScript> particleScripts;

	// misc
	public static String langId;
	public static String unknownPlaceholderResult;
	public static int numberFormattingDecimals;
	public static String numberFormattingSeparateThousands;
	public static boolean numberFormattingBigNumbers;
	public static ZoneId customTimeZone;
	public static boolean allowProtocolGUIs;
	public static boolean dontLogMissingEditorTexts;
	public static boolean logspamItemNbt;

	public static void logspamItemNbt(ThrowableSupplier<String> line) {
		logspamItemNbt(null, line);
	}

	public static void logspamItemNbt(NBTBase nbt, ThrowableSupplier<String> line) {
		logspamItemNbt(nbt, line, true);
	}

	public static void logspamItemNbt(NBTBase nbt, ThrowableSupplier<String> line, boolean prefix) {
		if (logspamItemNbt) {
			try {
				Bukkit.getConsoleSender().sendMessage((prefix ? LogLevel.DEBUG.getConsoleColor() + "[GCore] " : "") + (nbt == null ? "" : StringUtils.repeatString(">", nbt.getDepth() + 1) + " ") + line.get());
			} catch (Throwable exception) {
				GCore.inst().getMainLogger().error("Couldn't logspam item/nbt on nbt " + nbt, exception);
			}
		}
	}

	public static String formatDate(long epoch) {
		return DateTimeFormatter.ofPattern(TextGeneric.dateTimeFormat.parseLine()).format(LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), customTimeZone != null ? customTimeZone : ZoneId.systemDefault()));
	}

	// gui
	public static int guiItemRefreshSecondsPlaceholders = 30;

	public static ItemStack backItem;
	public static ItemStack previousPageItem;
	public static ItemStack nextPageItem;

	public static GUIType guiConfirmType;
	public static GUIItemTriple guiConfirmItemYes;
	public static GUIItemTriple guiConfirmItemNo;
	public static List<GUIItemTriple> guiConfirmContent;

	// load
	@Override
	protected YMLConfiguration doLoad() throws Throwable {
		baseConfig = GCore.inst().loadConfigurationFile("config.yml");
		// commands aliases
		commandsAliasesPlugins = CollectionUtils.asLowercaseList(baseConfig.readMandatoryStringList("commands_aliases.plugins"));
		commandsAliasesReload = CollectionUtils.asLowercaseList(baseConfig.readMandatoryStringList("commands_aliases.reload"));
		commandsAliasesItemRead = CollectionUtils.asLowercaseList(baseConfig.readStringList("commands_aliases.itemread", CollectionUtils.asList("itemread", "ir")));
		commandsAliasesExport = CollectionUtils.asLowercaseList(baseConfig.readMandatoryStringList("commands_aliases.export"));
		// materials
		try {
			GCore.inst().getMainLogger().info("Loading materials...");
			boolean regenerate = baseConfig.readMandatoryBoolean("materials.regenerate_file");
			boolean lenient = baseConfig.readMandatoryBoolean("materials.lenient");
			mats = loadVariants(new Mats(regenerate, lenient));
		} catch (Throwable exception) {
			YMLError causeYML = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (causeYML != null) throw new ConfigError("Couldn't load materials : " + causeYML.getMessage());
			if (causeConfig != null) throw causeConfig;
			throw new Error("Couldn't load materials", exception);
		}
		try {
			CommonMats.init();
		} catch (Throwable exception) {
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (causeConfig != null) throw new ConfigError(causeConfig.getMessage() + " (when loading common materials)");
			else throw new Error("Couldn't load common materials", exception);
		}
		// sounds
		try {
			GCore.inst().getMainLogger().info("Loading sounds...");
			boolean regenerate = baseConfig.readMandatoryBoolean("sounds.regenerate_file");
			sounds = loadVariants(new Sounds(regenerate));
		} catch (Throwable exception) {
			YMLError causeYML = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			if (causeYML != null) throw new ConfigError("Couldn't load sounds : " + causeYML.getMessage());
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (causeConfig != null) throw causeConfig;
			throw new Error("Couldn't load sounds", exception);
		}
		// particles
		try {
			GCore.inst().getMainLogger().info("Loading particles...");
			boolean regenerate = baseConfig.readMandatoryBoolean("particles.regenerate_file");
			particles = loadVariants(new Particles(regenerate));
		} catch (Throwable exception) {
			YMLError causeYML = ObjectUtils.findCauseOrNull(exception, YMLError.class);
			if (causeYML != null) throw new ConfigError("Couldn't load particles : " + causeYML.getMessage());
			ConfigError causeConfig = ObjectUtils.findCauseOrNull(exception, ConfigError.class);
			if (causeConfig != null) throw causeConfig;
			throw new Error("Couldn't load particles", exception);
		}
		// types
		GCore.inst().timeFrameTypes = new TimeFrameTypes();
		GCore.inst().positionTypes = new PositionTypes();
		particleScripts = new LowerCaseHashMap<>();
		loadParticleScripts(GCore.inst().getDataFile("particle_scripts/"));
		Currency.values().forEach(Currency::enable);
		// misc
		langId = baseConfig.readMandatoryString("lang");
		unknownPlaceholderResult = baseConfig.readMandatoryString("unknown_placeholder_result");
		numberFormattingDecimals = baseConfig.readMandatoryInteger("number_formatting_decimals");
		numberFormattingSeparateThousands = baseConfig.readString("number_formatting_separate_thousands", "");
		if (numberFormattingSeparateThousands.isEmpty()) numberFormattingSeparateThousands = null;
		numberFormattingBigNumbers = baseConfig.readBoolean("number_formatting_big_numbers", false);
		baseConfig.readKeysForSection("console_log_level_colors").forEach(key -> {
			LogLevel level = ObjectUtils.safeValueOf(key, LogLevel.class);
			if (level != null) {
				ChatColor color = baseConfig.readEnum("console_log_level_colors." + key, null, ChatColor.class);
				if (color != null) {
					level.setConsoleColor(color);
				}
			}
		});
		String zoneId = baseConfig.readString("custom_time_zone", null);
		if (zoneId != null) {
			customTimeZone = ZoneId.of(zoneId);
		}
		allowProtocolGUIs = baseConfig.readBoolean("allow_protocol_guis", true);
		dontLogMissingEditorTexts = baseConfig.readBoolean("dont_log_missing_editor_texts", true);
		logspamItemNbt = baseConfig.readBoolean("logspam_item_nbt", false);
		// data
		if (baseConfig.contains("mysql")) {
			String host = baseConfig.readMandatoryString("mysql.host");
			String name = baseConfig.readMandatoryString("mysql.name");
			String usr = baseConfig.readMandatoryString("mysql.user");
			String pwd = baseConfig.readMandatoryString("mysql.pass");
			String customArgs = baseConfig.readString("mysql.args", "");
			String url = "jdbc:mysql://" + host + "/" + name + "?allowMultiQueries=true" + customArgs;
			GCore.inst().getMySQLConnector().setMysql(new MySQL(url, usr, pwd));
		}
		// gui
		guiItemRefreshSecondsPlaceholders = baseConfig.readMandatoryInteger("gui_items_refresh_seconds_placeholder");
		backItem = baseConfig.readMandatoryItemStack("gui_items.back");
		previousPageItem = baseConfig.readMandatoryItemStack("gui_items.previous_page");
		nextPageItem = baseConfig.readMandatoryItemStack("gui_items.next_page");
		guiConfirmType = baseConfig.readMandatoryEnum("confirm_gui.type", GUIType.class);
		guiConfirmItemYes = baseConfig.readMandatoryGUIItemTriple("confirm_gui.item_yes");
		guiConfirmItemNo = baseConfig.readMandatoryGUIItemTriple("confirm_gui.item_no");
		guiConfirmContent = baseConfig.readGUIItemTripleList("confirm_gui.contents", new ArrayList<>());
		for (String rawType : baseConfig.readKeysForSection("gui_type_slots")) {
			GUIType type = ObjectUtils.safeValueOf(rawType, GUIType.class);
			if (type != null) {
				int back = baseConfig.readInteger("gui_type_slots." + rawType + ".back", -1);
				int previousPage = baseConfig.readInteger("gui_type_slots." + rawType + ".previous_page", -1);
				int nextPage = baseConfig.readInteger("gui_type_slots." + rawType + ".next_page", -1);
				if (back >= -1 && back < type.getSize()) type.setBackItemSlot(back);
				if (previousPage >= -1 && previousPage < type.getSize()) type.setPreviousPageItemSlot(previousPage);
				if (nextPage >= -1 && nextPage < type.getSize()) type.setNextPageItemSlot(nextPage);
			}
		}
		// add missing default options to config
		if (!baseConfig.contains("allow_protocol_guis")) {
			baseConfig.write("allow_protocol_guis", true);
			baseConfig.save();
		}
		// done, load loggers from base config
		return baseConfig;
	}

	private void loadParticleScripts(File file) throws Throwable {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				loadParticleScripts(f);
			}
		} else {
			String scriptId = FileUtils.getSimpleName(file).toLowerCase();
			ParticleScriptDecoder decoder = new ParticleScriptDecoder(GCore.inst(), file, scriptId);
			decoder.decode();
			particleScripts.put(scriptId, decoder.getScript());
		}
	}

	public static <V extends Variants> V loadVariants(V variants) {
		variants.reload(GCore.inst().getDataFile(variants.getTypeName() + "s.yml"), "resources/" + variants.getTypeName() + "s.yml");
		return variants;
	}

}
