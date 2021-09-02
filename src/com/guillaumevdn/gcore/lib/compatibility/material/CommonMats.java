package com.guillaumevdn.gcore.lib.compatibility.material;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;

/**
 * @author GuillaumeVDN
 */
public final class CommonMats {

	public static final Mat AIR = Mat.firstFromIdOrDataName("AIR").get();
	public static final Mat ANVIL = Mat.firstFromIdOrDataName("ANVIL").get();
	public static final Mat APPLE = Mat.firstFromIdOrDataName("APPLE").get();
	public static final Mat ARROW = Mat.firstFromIdOrDataName("ARROW").get();
	public static final Mat BEDROCK = Mat.firstFromIdOrDataName("BEDROCK").get();
	public static final Mat BUCKET = Mat.firstFromIdOrDataName("BUCKET").get();
	public static final Mat BOOKSHELF = Mat.firstFromIdOrDataName("BOOKSHELF").get();
	public static final Mat BLACK_WOOL = Mat.firstFromIdOrDataName("BLACK_WOOL").get();
	public static final Mat BLAZE_ROD = Mat.firstFromIdOrDataName("BLAZE_ROD").get();
	public static final Mat BLUE_BANNER = !Version.ATLEAST_1_8 ? null : Mat.firstFromIdOrDataName("BLUE_BANNER").get();
	public static final Mat BLUE_WOOL = Mat.firstFromIdOrDataName("BLUE_WOOL").get();
	public static final Mat BOOK = Mat.firstFromIdOrDataName("BOOK").get();
	public static final Mat CHEST = Mat.firstFromIdOrDataName("CHEST").get();
	public static final Mat CHEST_MINECART = Mat.firstFromIdOrDataName("CHEST_MINECART").get();
	public static final Mat CLOCK = Mat.firstFromIdOrDataName("CLOCK").get();
	public static final Mat COBBLESTONE = Mat.firstFromIdOrDataName("COBBLESTONE").get();
	public static final Mat COMMAND_BLOCK = Mat.firstFromIdOrDataName("COMMAND_BLOCK").get();
	public static final Mat COMPARATOR = Mat.firstFromIdOrDataName("COMPARATOR").get();
	public static final Mat COMPASS = Mat.firstFromIdOrDataName("COMPASS").get();
	public static final Mat DETECTOR_RAIL = Mat.firstFromIdOrDataName("DETECTOR_RAIL").get();
	public static final Mat DIAMOND_BOOTS = Mat.firstFromIdOrDataName("DIAMOND_BOOTS").get();
	public static final Mat DIAMOND_SWORD = Mat.firstFromIdOrDataName("DIAMOND_SWORD").get();
	public static final Mat DIAMOND_PICKAXE = Mat.firstFromIdOrDataName("DIAMOND_PICKAXE").get();
	public static final Mat DIRT = Mat.firstFromIdOrDataName("DIRT").get();
	public static final Mat ELYTRA = !Version.ATLEAST_1_9 ? null : Mat.firstFromIdOrDataName("ELYTRA").get();
	public static final Mat EMERALD = Mat.firstFromIdOrDataName("EMERALD").get();
	public static final Mat ENCHANTED_BOOK = Mat.firstFromIdOrDataName("ENCHANTED_BOOK").get();
	public static final Mat ENCHANTING_TABLE = Mat.firstFromIdOrDataName("ENCHANTING_TABLE").get();
	public static final Mat ENDER_CHEST = Mat.firstFromIdOrDataName("ENDER_CHEST").get();
	public static final Mat EXPERIENCE_BOTTLE = Mat.firstFromIdOrDataName("EXPERIENCE_BOTTLE").get();
	public static final Mat ENDER_EYE = Mat.firstFromIdOrDataName("ENDER_EYE").get();
	public static final Mat FURNACE_MINECART = Mat.firstFromIdOrDataName("FURNACE_MINECART").get();
	public static final Mat FISHING_ROD = Mat.firstFromIdOrDataName("FISHING_ROD").get();
	public static final Mat GLASS_BOTTLE = Mat.firstFromIdOrDataName("GLASS_BOTTLE").get();
	public static final Mat GOLDEN_APPLE = Mat.firstFromIdOrDataName("GOLDEN_APPLE").get();
	public static final Mat GOLDEN_CHESTPLATE = Mat.firstFromIdOrDataName("GOLDEN_CHESTPLATE").get();
	public static final Mat GOLD_BLOCK = Mat.firstFromIdOrDataName("GOLD_BLOCK").get();
	public static final Mat GOLD_INGOT = Mat.firstFromIdOrDataName("GOLD_INGOT").get();
	public static final Mat GOLD_NUGGET = Mat.firstFromIdOrDataName("GOLD_NUGGET").get();
	public static final Mat GRASS_BLOCK = Mat.firstFromIdOrDataName("GRASS_BLOCK").get();
	public static final Mat GRAY_DYE = Mat.firstFromIdOrDataName("GRAY_DYE").get();
	public static final Mat GRAY_WOOL = Mat.firstFromIdOrDataName("GRAY_WOOL").get();
	public static final Mat LEVER = Mat.firstFromIdOrDataName("LEVER").get();
	public static final Mat LIME_DYE = Mat.firstFromIdOrDataName("LIME_DYE").get();
	public static final Mat GREEN_WOOL = Mat.firstFromIdOrDataName("GREEN_WOOL").get();
	public static final Mat GUNPOWDER = Mat.firstFromIdOrDataName("GUNPOWDER").get();
	public static final Mat HOPPER = Mat.firstFromIdOrDataName("HOPPER").get();
	public static final Mat ICE = Mat.firstFromIdOrDataName("ICE").get();
	public static final Mat IRON_DOOR = Mat.firstFromIdOrDataName("IRON_DOOR").get();
	public static final Mat IRON_INGOT = Mat.firstFromIdOrDataName("IRON_INGOT").get();
	public static final Mat LIGHT_BLUE_DYE = Mat.firstFromIdOrDataName("LIGHT_BLUE_DYE").get();
	public static final Mat LIGHT_BLUE_WOOL = Mat.firstFromIdOrDataName("LIGHT_BLUE_WOOL").get();
	public static final Mat MAGENTA_WOOL = Mat.firstFromIdOrDataName("MAGENTA_WOOL").get();
	public static final Mat MINECART = Mat.firstFromIdOrDataName("MINECART").get();
	public static final Mat NETHERRACK = Mat.firstFromIdOrDataName("NETHERRACK").get();
	public static final Mat NETHER_STAR = Mat.firstFromIdOrDataName("NETHER_STAR").get();
	public static final Mat NOTE_BLOCK = Mat.firstFromIdOrDataName("NOTE_BLOCK").get();
	public static final Mat OAK_BOAT = Mat.firstFromIdOrDataName("OAK_BOAT").get();
	public static final Mat OAK_BUTTON = Mat.firstFromIdOrDataName("OAK_BUTTON").get();
	public static final Mat OAK_FENCE = Mat.firstFromIdOrDataName("OAK_FENCE").get();
	public static final Mat OAK_LOG = Mat.firstFromIdOrDataName("OAK_LOG").get();
	public static final Mat OBSIDIAN = Mat.firstFromIdOrDataName("OBSIDIAN").get();
	public static final Mat ORANGE_WOOL = Mat.firstFromIdOrDataName("ORANGE_WOOL").get();
	public static final Mat PAPER = Mat.firstFromIdOrDataName("PAPER").get();
	public static final Mat PLAYER_HEAD = Mat.firstFromIdOrDataName("PLAYER_HEAD").get();
	public static final Mat PINK_WOOL = Mat.firstFromIdOrDataName("PINK_WOOL").get();
	public static final Mat POTION = Mat.firstFromIdOrDataName("POTION").get();
	public static final Mat RAIL = Mat.firstFromIdOrDataName("RAIL").get();
	public static final Mat REDSTONE = Mat.firstFromIdOrDataName("REDSTONE").get();
	public static final Mat RED_BED = Mat.firstFromIdOrDataName("RED_BED").get();
	public static final Mat RED_DYE = Mat.firstFromIdOrDataName("RED_DYE").get();
	public static final Mat RED_WOOL = Mat.firstFromIdOrDataName("RED_WOOL").get();
	public static final Mat REPEATER = Mat.firstFromIdOrDataName("REPEATER").get();
	public static final Mat SADDLE = Mat.firstFromIdOrDataName("SADDLE").get();
	public static final Mat GRAY_STAINED_GLASS_PANE = Mat.firstFromIdOrDataName("GRAY_STAINED_GLASS_PANE").get();
	public static final Mat STICK = Mat.firstFromIdOrDataName("STICK").get();
	public static final Mat TNT = Mat.firstFromIdOrDataName("TNT").get();
	public static final Mat TORCH = Mat.firstFromIdOrDataName("TORCH").get();
	public static final Mat WATER_BUCKET = Mat.firstFromIdOrDataName("WATER_BUCKET").get();
	public static final Mat WHITE_WOOL = Mat.firstFromIdOrDataName("WHITE_WOOL").get();
	public static final Mat WOODEN_SWORD = Mat.firstFromIdOrDataName("WOODEN_SWORD").get();
	public static final Mat WRITABLE_BOOK = Mat.firstFromIdOrDataName("WRITABLE_BOOK").get();
	public static final Mat WRITTEN_BOOK = Mat.firstFromIdOrDataName("WRITTEN_BOOK").get();
	public static final Mat YELLOW_WOOL = Mat.firstFromIdOrDataName("YELLOW_WOOL").get();
	public static final Mat ZOMBIE_HEAD = Mat.firstFromIdOrDataName("ZOMBIE_HEAD").get();

	public static Mat NOCHECK;

	public static void init() {
		try {
			NOCHECK = ConfigGCore.mats.createElement("NOCHECK", new MatData(Version.CURRENT, ComparisonType.EQUALS, "NOCHECK", null, -1, null, null));
		} catch (Throwable exception) {
			NOCHECK = null;
			GCore.inst().getMainLogger().error("Couldn't initialize NOCHECK material, please report this :", exception);
		}
	}

}
