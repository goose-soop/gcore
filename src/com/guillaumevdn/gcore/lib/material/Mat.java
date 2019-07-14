package com.guillaumevdn.gcore.lib.material;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

/**
 * Adapted from https://www.spigotmc.org/threads/329630/
 */
public class Mat implements Cloneable, Comparable<Mat> {

	// static fields and constructor
	private static final Map<String, Mat> REGULAR_VALUES = new HashMap<String, Mat>();
	private static final Map<String, MatDurabilities> MODERN = new HashMap<String, MatDurabilities>();
	private static final Map<String, MatDatas> LEGACY = new HashMap<String, MatDatas>();
	public static final MatVersion VERSION = ServerVersion.IS_1_13 ? MatVersion.MODERN : MatVersion.LEGACY;

	// values
	public static final Mat ACACIA_BOAT = registerValue(new Mat(VERSION, "ACACIA_BOAT", "BOAT_ACACIA"));
	public static final Mat ACACIA_BUTTON = registerValue(new Mat(VERSION, "ACACIA_BUTTON", "WOOD_BUTTON"));
	public static final Mat ACACIA_DOOR = registerValue(new Mat(VERSION, "ACACIA_DOOR", "ACACIA_DOOR"));
	protected static final Mat ACACIA_DOOR_ITEM = registerValue(new Mat(VERSION, "ACACIA_DOOR", "ACACIA_DOOR_ITEM"));
	public static final Mat ACACIA_FENCE = registerValue(new Mat(VERSION, "ACACIA_FENCE", "ACACIA_FENCE"));
	public static final Mat ACACIA_FENCE_GATE = registerValue(new Mat(VERSION, "ACACIA_FENCE_GATE", "ACACIA_FENCE_GATE"));
	public static final Mat ACACIA_LEAVES = registerValue(new Mat(VERSION, "ACACIA_LEAVES", "LEAVES_2"));
	public static final Mat ACACIA_LOG = registerValue(new Mat(VERSION, "ACACIA_LOG", "LOG_2"));
	public static final Mat ACACIA_PLANKS = registerValue(new Mat(VERSION, "ACACIA_PLANKS", "WOOD", 4));
	public static final Mat ACACIA_PRESSURE_PLATE = registerValue(new Mat(VERSION, "ACACIA_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat ACACIA_SAPLING = registerValue(new Mat(VERSION, "ACACIA_SAPLING", "SAPLING", 4));
	public static final Mat ACACIA_SLAB = registerValue(new Mat(VERSION, "ACACIA_SLAB", "WOOD_STEP", 4));
	public static final Mat ACACIA_STAIRS = registerValue(new Mat(VERSION, "ACACIA_STAIRS", "ACACIA_STAIRS", 4));
	public static final Mat ACACIA_TRAPDOOR = registerValue(new Mat(VERSION, "ACACIA_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat ACACIA_WOOD = registerValue(new Mat(VERSION, "ACACIA_WOOD", "LOG_2"));
	public static final Mat ACTIVATOR_RAIL = registerValue(new Mat(VERSION, "ACTIVATOR_RAIL", "ACTIVATOR_RAIL"));
	public static final Mat AIR = registerValue(new Mat(VERSION, "AIR", "AIR"));
	public static final Mat ALLIUM = registerValue(new Mat(VERSION, "ALLIUM", "-"));
	public static final Mat ANDESITE = registerValue(new Mat(VERSION, "ANDESITE", "-", 5));
	public static final Mat ANVIL = registerValue(new Mat(VERSION, "ANVIL", "ANVIL"));
	public static final Mat APPLE = registerValue(new Mat(VERSION, "APPLE", "APPLE"));
	public static final Mat ARMOR_STAND = registerValue(new Mat(VERSION, "ARMOR_STAND", "ARMOR_STAND"));
	public static final Mat ARROW = registerValue(new Mat(VERSION, "ARROW", "ARROW"));
	public static final Mat ATTACHED_MELON_STEM = registerValue(new Mat(VERSION, "ATTACHED_MELON_STEM", "MELON_STEM", 7));
	public static final Mat ATTACHED_PUMPKIN_STEM = registerValue(new Mat(VERSION, "ATTACHED_PUMPKIN_STEM", "PUMPKIN_STEM", 7));
	public static final Mat AZURE_BLUET = registerValue(new Mat(VERSION, "AZURE_BLUET", "RED_ROSE", 3));
	public static final Mat BAKED_POTATO = registerValue(new Mat(VERSION, "BAKED_POTATO", "BAKED_POTATO"));
	public static final Mat BARRIER = registerValue(new Mat(VERSION, "BARRIER", "BARRIER"));
	public static final Mat BAT_SPAWN_EGG = registerValue(new Mat(VERSION, "BAT_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat BEACON = registerValue(new Mat(VERSION, "BEACON", "BEACON"));
	public static final Mat BEDROCK = registerValue(new Mat(VERSION, "BEDROCK", "BEDROCK"));
	public static final Mat BEEF = registerValue(new Mat(VERSION, "BEEF", "RAW_BEEF"));
	public static final Mat BEETROOT = registerValue(new Mat(VERSION, "BEETROOT", "BEETROOT"));
	public static final Mat BEETROOTS = registerValue(new Mat(VERSION, "BEETROOTS", "BEETROOT"));
	public static final Mat BEETROOT_SEEDS = registerValue(new Mat(VERSION, "BEETROOT_SEEDS", "BEETROOT_SEEDS"));
	public static final Mat BEETROOT_SOUP = registerValue(new Mat(VERSION, "BEETROOT_SOUP", "BEETROOT_SOUP"));
	public static final Mat BIRCH_BOAT = registerValue(new Mat(VERSION, "BIRCH_BOAT", "BOAT_BIRCH"));
	public static final Mat BIRCH_BUTTON = registerValue(new Mat(VERSION, "BIRCH_BUTTON", "WOOD_BUTTON"));
	public static final Mat BIRCH_DOOR = registerValue(new Mat(VERSION, "BIRCH_DOOR", "BIRCH_DOOR"));
	protected static final Mat BIRCH_DOOR_ITEM = registerValue(new Mat(VERSION, "BIRCH_DOOR", "BIRCH_DOOR_ITEM"));
	public static final Mat BIRCH_FENCE = registerValue(new Mat(VERSION, "BIRCH_FENCE", "BIRCH_FENCE"));
	public static final Mat BIRCH_FENCE_GATE = registerValue(new Mat(VERSION, "BIRCH_FENCE_GATE", "BIRCH_FENCE_GATE"));
	public static final Mat BIRCH_LEAVES = registerValue(new Mat(VERSION, "BIRCH_LEAVES", "LEAVES", 2));
	public static final Mat BIRCH_LOG = registerValue(new Mat(VERSION, "BIRCH_LOG", "LOG", 2));
	public static final Mat BIRCH_PLANKS = registerValue(new Mat(VERSION, "BIRCH_PLANKS", "WOOD", 2));
	public static final Mat BIRCH_PRESSURE_PLATE = registerValue(new Mat(VERSION, "BIRCH_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat BIRCH_SAPLING = registerValue(new Mat(VERSION, "BIRCH_SAPLING", "SAPLING", 2));
	public static final Mat BIRCH_SLAB = registerValue(new Mat(VERSION, "BIRCH_SLAB", "WOOD_STEP", 2));
	public static final Mat BIRCH_STAIRS = registerValue(new Mat(VERSION, "BIRCH_STAIRS", "BIRCH_WOOD_STAIRS"));
	public static final Mat BIRCH_TRAPDOOR = registerValue(new Mat(VERSION, "BIRCH_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat BIRCH_WOOD = registerValue(new Mat(VERSION, "BIRCH_WOOD", "LOG", 2));
	public static final Mat BLACK_BANNER = registerValue(new Mat(VERSION, "BLACK_BANNER", "BANNER"));
	public static final Mat BLACK_BED = registerValue(new Mat(VERSION, "BLACK_BED", "BED", 15));
	public static final Mat BLACK_CARPET = registerValue(new Mat(VERSION, "BLACK_CARPET", "CARPET", 15));
	protected static final Mat CONCRETE = registerValue(new Mat(VERSION, "CONCRETE", "CONCRETE", 0));
	public static final Mat BLACK_CONCRETE = registerValue(new Mat(VERSION, "BLACK_CONCRETE", "STAINED_CLAY", 15));
	public static final Mat BLACK_CONCRETE_POWDER = registerValue(new Mat(VERSION, "BLACK_CONCRETE_POWDER", "CONCRETE_POWDER", 15));
	public static final Mat BLACK_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "BLACK_GLAZED_TERRACOTTA", "BLACK_GLAZED_TERRACOTTA"));
	public static final Mat BLACK_SHULKER_BOX = registerValue(new Mat(VERSION, "BLACK_SHULKER_BOX", "BLACK_SHULKER_BOX"));
	public static final Mat BLACK_STAINED_GLASS = registerValue(new Mat(VERSION, "BLACK_STAINED_GLASS", "STAINED_GLASS", 15));
	public static final Mat BLACK_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 15));
	public static final Mat BLACK_TERRACOTTA = registerValue(new Mat(VERSION, "BLACK_TERRACOTTA", "STAINED_CLAY", 15));
	public static final Mat BLACK_WALL_BANNER = registerValue(new Mat(VERSION, "BLACK_WALL_BANNER", "WALL_BANNER"));
	public static final Mat BLACK_WOOL = registerValue(new Mat(VERSION, "BLACK_WOOL", "WOOL", 15));
	public static final Mat BLAZE_POWDER = registerValue(new Mat(VERSION, "BLAZE_POWDER", "BLAZE_POWDER"));
	public static final Mat BLAZE_ROD = registerValue(new Mat(VERSION, "BLAZE_ROD", "BLAZE_ROD"));
	public static final Mat BLAZE_SPAWN_EGG = registerValue(new Mat(VERSION, "BLAZE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat BLUE_BANNER = registerValue(new Mat(VERSION, "BLUE_BANNER", "BANNER", 11));
	public static final Mat BLUE_BED = registerValue(new Mat(VERSION, "BLUE_BED", "BED", 4));
	public static final Mat BLUE_CARPET = registerValue(new Mat(VERSION, "BLUE_CARPET", "CARPET", 11));
	public static final Mat BLUE_CONCRETE = registerValue(new Mat(VERSION, "BLUE_CONCRETE", "STAINED_CLAY", 11));
	public static final Mat BLUE_CONCRETE_POWDER = registerValue(new Mat(VERSION, "BLUE_CONCRETE_POWDER", "CONCRETE_POWDER", 11));
	public static final Mat BLUE_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "BLUE_GLAZED_TERRACOTTA", "BLUE_GLAZED_TERRACOTTA"));
	public static final Mat BLUE_ICE = registerValue(new Mat(VERSION, "BLUE_ICE", "PACKED_ICE"));
	public static final Mat BLUE_ORCHID = registerValue(new Mat(VERSION, "BLUE_ORCHID", "RED_ROSE", 1));
	public static final Mat BLUE_SHULKER_BOX = registerValue(new Mat(VERSION, "BLUE_SHULKER_BOX", "BLUE_SHULKER_BOX"));
	public static final Mat BLUE_STAINED_GLASS = registerValue(new Mat(VERSION, "BLUE_STAINED_GLASS", "STAINED_GLASS", 11));
	public static final Mat BLUE_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 11));
	public static final Mat BLUE_TERRACOTTA = registerValue(new Mat(VERSION, "BLUE_TERRACOTTA", "STAINED_CLAY", 11));
	public static final Mat BLUE_WALL_BANNER = registerValue(new Mat(VERSION, "BLUE_WALL_BANNER", "WALL_BANNER", 11));
	public static final Mat BLUE_WOOL = registerValue(new Mat(VERSION, "BLUE_WOOL", "WOOL", 11));
	public static final Mat BONE = registerValue(new Mat(VERSION, "BONE", "BONE"));
	public static final Mat BONE_BLOCK = registerValue(new Mat(VERSION, "BONE_BLOCK", "BONE_BLOCK"));
	public static final Mat BONE_MEAL = registerValue(new Mat(VERSION, "BONE_MEAL", "INK_SACK", 15));
	public static final Mat BOOK = registerValue(new Mat(VERSION, "BOOK", "BOOK"));
	public static final Mat BOOKSHELF = registerValue(new Mat(VERSION, "BOOKSHELF", "BOOKSHELF"));
	public static final Mat BOW = registerValue(new Mat(VERSION, "BOW", "BOW"));
	public static final Mat BOWL = registerValue(new Mat(VERSION, "BOWL", "BOWL"));
	public static final Mat BRAIN_CORAL = registerValue(new Mat(VERSION, "BRAIN_CORAL", "-"));
	public static final Mat BRAIN_CORAL_BLOCK = registerValue(new Mat(VERSION, "BRAIN_CORAL_BLOCK", "-"));
	public static final Mat BRAIN_CORAL_FAN = registerValue(new Mat(VERSION, "BRAIN_CORAL_FAN", "-"));
	public static final Mat BREAD = registerValue(new Mat(VERSION, "BREAD", "BREAD"));
	public static final Mat BREWING_STAND = registerValue(new Mat(VERSION, "BREWING_STAND", "BREWING_STAND"));
	public static final Mat BREWING_STAND_ITEM = registerValue(new Mat(VERSION, "BREWING_STAND", "BREWING_STAND_ITEM"));
	public static final Mat BRICK = registerValue(new Mat(VERSION, "BRICK", "CLAY_BRICK"));
	public static final Mat BRICKS = registerValue(new Mat(VERSION, "BRICKS", "BRICK"));
	public static final Mat BRICK_SLAB = registerValue(new Mat(VERSION, "BRICK_SLAB", "STEP", 4));
	public static final Mat BRICK_STAIRS = registerValue(new Mat(VERSION, "BRICK_STAIRS", "BRICK_STAIRS"));
	public static final Mat BROWN_BANNER = registerValue(new Mat(VERSION, "BROWN_BANNER", "BANNER", 3));
	public static final Mat BROWN_BED = registerValue(new Mat(VERSION, "BROWN_BED", "BED", 12));
	public static final Mat BROWN_CARPET = registerValue(new Mat(VERSION, "BROWN_CARPET", "CARPET", 12));
	public static final Mat BROWN_CONCRETE = registerValue(new Mat(VERSION, "BROWN_CONCRETE", "STAINED_CLAY", 12));
	public static final Mat BROWN_CONCRETE_POWDER = registerValue(new Mat(VERSION, "BROWN_CONCRETE_POWDER", "CONCRETE_POWDER", 12));
	public static final Mat BROWN_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "BROWN_GLAZED_TERRACOTTA", "BROWN_GLAZED_TERRACOTTA"));
	public static final Mat BROWN_MUSHROOM = registerValue(new Mat(VERSION, "BROWN_MUSHROOM", "BROWN_MUSHROOM"));
	public static final Mat BROWN_MUSHROOM_BLOCK = registerValue(new Mat(VERSION, "BROWN_MUSHROOM_BLOCK", "BROWN_MUSHROOM"));
	public static final Mat BROWN_SHULKER_BOX = registerValue(new Mat(VERSION, "BROWN_SHULKER_BOX", "BROWN_SHULKER_BOX"));
	public static final Mat BROWN_STAINED_GLASS = registerValue(new Mat(VERSION, "BROWN_STAINED_GLASS", "STAINED_GLASS", 12));
	public static final Mat BROWN_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "BROWN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 12));
	public static final Mat BROWN_TERRACOTTA = registerValue(new Mat(VERSION, "BROWN_TERRACOTTA", "STAINED_CLAY", 12));
	public static final Mat BROWN_WALL_BANNER = registerValue(new Mat(VERSION, "BROWN_WALL_BANNER", "WALL_BANNER", 3));
	public static final Mat BROWN_WOOL = registerValue(new Mat(VERSION, "BROWN_WOOL", "WOOL", 12));
	public static final Mat BUBBLE_COLUMN = registerValue(new Mat(VERSION, "BUBBLE_COLUMN", "-"));
	public static final Mat BUBBLE_CORAL = registerValue(new Mat(VERSION, "BUBBLE_CORAL", "-"));
	public static final Mat BUBBLE_CORAL_BLOCK = registerValue(new Mat(VERSION, "BUBBLE_CORAL_BLOCK", "-"));
	public static final Mat BUBBLE_CORAL_FAN = registerValue(new Mat(VERSION, "BUBBLE_CORAL_FAN", "-"));
	public static final Mat BUCKET = registerValue(new Mat(VERSION, "BUCKET", "BUCKET"));
	public static final Mat CACTUS = registerValue(new Mat(VERSION, "CACTUS", "CACTUS"));
	public static final Mat CACTUS_GREEN = registerValue(new Mat(VERSION, "CACTUS_GREEN", "INK_SACK", 2));
	public static final Mat GREEN_DYE = registerValue(new Mat(VERSION, "GREEN_DYE", "INK_SACK", 2));
	public static final Mat CAKE = registerValue(new Mat(VERSION, "CAKE", "CAKE")); 
	public static final Mat CARROT = registerValue(new Mat(VERSION, "CARROT", "CARROT_ITEM"));
	public static final Mat CARROTS = registerValue(new Mat(VERSION, "CARROTS", "CARROT"));
	public static final Mat CARROT_ON_A_STICK = registerValue(new Mat(VERSION, "CARROT_ON_A_STICK", "CARROT_STICK"));
	public static final Mat CARVED_PUMPKIN = registerValue(new Mat(VERSION, "CARVED_PUMPKIN", "PUMPKIN"));
	public static final Mat CAULDRON = registerValue(new Mat(VERSION, "CAULDRON", "CAULDRON"));
	public static final Mat CAULDRON_ITEM = registerValue(new Mat(VERSION, "CAULDRON", "CAULDRON_ITEM"));
	public static final Mat CAVE_AIR = registerValue(new Mat(VERSION, "CAVE_AIR", "AIR"));
	public static final Mat CAVE_SPIDER_SPAWN_EGG = registerValue(new Mat(VERSION, "CAVE_SPIDER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat CHAINMAIL_BOOTS = registerValue(new Mat(VERSION, "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"));
	public static final Mat CHAINMAIL_CHESTPLATE = registerValue(new Mat(VERSION, "CHAINMAIL_CHESTPLATE", "CHAINMAIL_CHESTPLATE"));
	public static final Mat CHAINMAIL_HELMET = registerValue(new Mat(VERSION, "CHAINMAIL_HELMET", "CHAINMAIL_HELMET"));
	public static final Mat CHAINMAIL_LEGGINGS = registerValue(new Mat(VERSION, "CHAINMAIL_LEGGINGS", "CHAINMAIL_LEGGINGS"));
	public static final Mat CHAIN_COMMAND_BLOCK = registerValue(new Mat(VERSION, "CHAIN_COMMAND_BLOCK", "COMMAND_CHAIN"));
	public static final Mat CHARCOAL = registerValue(new Mat(VERSION, "CHARCOAL", "COAL", 1));
	public static final Mat CHEST = registerValue(new Mat(VERSION, "CHEST", "CHEST"));
	public static final Mat CHEST_MINECART = registerValue(new Mat(VERSION, "CHEST_MINECART", "STORAGE_MINECART"));
	public static final Mat CHICKEN = registerValue(new Mat(VERSION, "CHICKEN", "RAW_CHICKEN"));
	public static final Mat CHICKEN_SPAWN_EGG = registerValue(new Mat(VERSION, "CHICKEN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat CHIPPED_ANVIL = registerValue(new Mat(VERSION, "CHIPPED_ANVIL", "ANVIL", 1));
	public static final Mat CHISELED_QUARTZ_BLOCK = registerValue(new Mat(VERSION, "CHISELED_QUARTZ_BLOCK", "QUARTZ_BLOCK", 1));
	public static final Mat CHISELED_RED_SANDSTONE = registerValue(new Mat(VERSION, "CHISELED_RED_SANDSTONE", "RED_SANDSTONE", 1));
	public static final Mat CHISELED_SANDSTONE = registerValue(new Mat(VERSION, "CHISELED_SANDSTONE", "SANDSTONE", 1));
	public static final Mat CHISELED_STONE_BRICKS = registerValue(new Mat(VERSION, "CHISELED_STONE_BRICKS", "SMOOTH_BRICK", 3));
	public static final Mat CHORUS_FLOWER = registerValue(new Mat(VERSION, "CHORUS_FLOWER", "CHORUS_FLOWER"));
	public static final Mat CHORUS_FRUIT = registerValue(new Mat(VERSION, "CHORUS_FRUIT", "CHORUS_FRUIT"));
	public static final Mat CHORUS_PLANT = registerValue(new Mat(VERSION, "CHORUS_PLANT", "CHORUS_PLANT"));
	public static final Mat CLAY = registerValue(new Mat(VERSION, "CLAY", "CLAY"));
	public static final Mat CLAY_BALL = registerValue(new Mat(VERSION, "CLAY_BALL", "CLAY_BALL"));
	public static final Mat CLOCK = registerValue(new Mat(VERSION, "CLOCK", "WATCH"));
	public static final Mat COAL = registerValue(new Mat(VERSION, "COAL", "COAL"));
	public static final Mat COAL_BLOCK = registerValue(new Mat(VERSION, "COAL_BLOCK", "COAL_BLOCK"));
	public static final Mat COAL_ORE = registerValue(new Mat(VERSION, "COAL_ORE", "COAL_ORE"));
	public static final Mat COARSE_DIRT = registerValue(new Mat(VERSION, "COARSE_DIRT", "DIRT", 1));
	public static final Mat COBBLESTONE = registerValue(new Mat(VERSION, "COBBLESTONE", "COBBLESTONE"));
	public static final Mat COBBLESTONE_SLAB = registerValue(new Mat(VERSION, "COBBLESTONE_SLAB", "STEP", 3));
	public static final Mat COBBLESTONE_STAIRS = registerValue(new Mat(VERSION, "COBBLESTONE_STAIRS", "COBBLESTONE_STAIRS"));
	public static final Mat COBBLESTONE_WALL = registerValue(new Mat(VERSION, "COBBLESTONE_WALL", "COBBLE_WALL"));
	public static final Mat COBWEB = registerValue(new Mat(VERSION, "COBWEB", "WEB"));
	public static final Mat COCOA = registerValue(new Mat(VERSION, "COCOA", "COCOA"));
	public static final Mat COCOA_BEANS = registerValue(new Mat(VERSION, "COCOA_BEANS", "INK_SACK", 3));
	public static final Mat COD = registerValue(new Mat(VERSION, "COD", "RAW_FISH"));
	public static final Mat COD_BUCKET = registerValue(new Mat(VERSION, "COD_BUCKET", "BUCKET"));
	public static final Mat COD_SPAWN_EGG = registerValue(new Mat(VERSION, "COD_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat COMMAND_BLOCK = registerValue(new Mat(VERSION, "COMMAND_BLOCK", "COMMAND"));
	public static final Mat COMMAND_BLOCK_MINECART = registerValue(new Mat(VERSION, "COMMAND_BLOCK_MINECART", "COMMAND_MINECART"));
	public static final Mat COMPARATOR = registerValue(new Mat(VERSION, "COMPARATOR", "REDSTONE_COMPARATOR"));
	public static final Mat COMPASS = registerValue(new Mat(VERSION, "COMPASS", "COMPASS"));
	public static final Mat CONDUIT = registerValue(new Mat(VERSION, "CONDUIT", "-"));
	public static final Mat COOKED_BEEF = registerValue(new Mat(VERSION, "COOKED_BEEF", "COOKED_BEEF"));
	public static final Mat COOKED_CHICKEN = registerValue(new Mat(VERSION, "COOKED_CHICKEN", "COOKED_CHICKEN"));
	public static final Mat COOKED_COD = registerValue(new Mat(VERSION, "COOKED_COD", "COOKED_FISH"));
	public static final Mat COOKED_MUTTON = registerValue(new Mat(VERSION, "COOKED_MUTTON", "COOKED_MUTTON"));
	public static final Mat COOKED_PORKCHOP = registerValue(new Mat(VERSION, "COOKED_PORKCHOP", "GRILLED_PORK"));
	public static final Mat COOKED_RABBIT = registerValue(new Mat(VERSION, "COOKED_RABBIT", "COOKED_RABBIT"));
	public static final Mat COOKED_SALMON = registerValue(new Mat(VERSION, "COOKED_SALMON", "COOKED_FISH", 1));
	public static final Mat COOKIE = registerValue(new Mat(VERSION, "COOKIE", "COOKIE"));
	public static final Mat COW_SPAWN_EGG = registerValue(new Mat(VERSION, "COW_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat CRACKED_STONE_BRICKS = registerValue(new Mat(VERSION, "CRACKED_STONE_BRICKS", "SMOOTH_BRICK", 2));
	public static final Mat CRAFTING_TABLE = registerValue(new Mat(VERSION, "CRAFTING_TABLE", "WORKBENCH"));
	public static final Mat CREEPER_HEAD = registerValue(new Mat(VERSION, "CREEPER_HEAD", "SKULL_ITEM", 4));
	public static final Mat CREEPER_SPAWN_EGG = registerValue(new Mat(VERSION, "CREEPER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat CREEPER_WALL_HEAD = registerValue(new Mat(VERSION, "CREEPER_WALL_HEAD", "SKULL_ITEM", 4));
	public static final Mat CUT_RED_SANDSTONE = registerValue(new Mat(VERSION, "CUT_RED_SANDSTONE", "-"));
	public static final Mat CUT_SANDSTONE = registerValue(new Mat(VERSION, "CUT_SANDSTONE", "-"));
	public static final Mat CYAN_BANNER = registerValue(new Mat(VERSION, "CYAN_BANNER", "BANNER", 6));
	public static final Mat CYAN_BED = registerValue(new Mat(VERSION, "CYAN_BED", "BED", 9));
	public static final Mat CYAN_CARPET = registerValue(new Mat(VERSION, "CYAN_CARPET", "CARPET", 9));
	public static final Mat CYAN_CONCRETE = registerValue(new Mat(VERSION, "CYAN_CONCRETE", "STAINED_CLAY", 9));
	public static final Mat CYAN_CONCRETE_POWDER = registerValue(new Mat(VERSION, "CYAN_CONCRETE_POWDER", "CONCRETE_POWDER", 9));
	public static final Mat CYAN_DYE = registerValue(new Mat(VERSION, "CYAN_DYE", "INK_SACK", 6));
	public static final Mat CYAN_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "CYAN_GLAZED_TERRACOTTA", "CYAN_GLAZED_TERRACOTTA"));
	public static final Mat CYAN_SHULKER_BOX = registerValue(new Mat(VERSION, "CYAN_SHULKER_BOX", "CYAN_SHULKER_BOX"));
	public static final Mat CYAN_STAINED_GLASS = registerValue(new Mat(VERSION, "CYAN_STAINED_GLASS", "STAINED_GLASS", 9));
	public static final Mat CYAN_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "CYAN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 9));
	public static final Mat CYAN_TERRACOTTA = registerValue(new Mat(VERSION, "CYAN_TERRACOTTA", "STAINED_CLAY", 9));
	public static final Mat CYAN_WALL_BANNER = registerValue(new Mat(VERSION, "CYAN_WALL_BANNER", "WALL_BANNER"));
	public static final Mat CYAN_WOOL = registerValue(new Mat(VERSION, "CYAN_WOOL", "WOOL", 9));
	public static final Mat DAMAGED_ANVIL = registerValue(new Mat(VERSION, "DAMAGED_ANVIL", "ANVIL", 2));
	public static final Mat DANDELION = registerValue(new Mat(VERSION, "DANDELION", "YELLOW_FLOWER"));
	public static final Mat DANDELION_YELLOW = registerValue(new Mat(VERSION, "DANDELION_YELLOW", "INK_SACK", 11));
	public static final Mat DARK_OAK_BOAT = registerValue(new Mat(VERSION, "DARK_OAK_BOAT", "BOAT_DARK_OAK"));
	public static final Mat DARK_OAK_BUTTON = registerValue(new Mat(VERSION, "DARK_OAK_BUTTON", "WOOD_BUTTON"));
	public static final Mat DARK_OAK_DOOR = registerValue(new Mat(VERSION, "DARK_OAK_DOOR", "DARK_OAK_DOOR"));
	protected static final Mat DARK_OAK_DOOR_ITEM = registerValue(new Mat(VERSION, "DARK_OAK_DOOR", "DARK_OAK_DOOR_ITEM"));
	public static final Mat DARK_OAK_FENCE = registerValue(new Mat(VERSION, "DARK_OAK_FENCE", "DARK_OAK_FENCE"));
	public static final Mat DARK_OAK_FENCE_GATE = registerValue(new Mat(VERSION, "DARK_OAK_FENCE_GATE", "DARK_OAK_FENCE_GATE"));
	public static final Mat DARK_OAK_LEAVES = registerValue(new Mat(VERSION, "DARK_OAK_LEAVES", "LEAVES_2", 1));
	public static final Mat DARK_OAK_LOG = registerValue(new Mat(VERSION, "DARK_OAK_LOG", "LOG_2", 1));
	public static final Mat DARK_OAK_PLANKS = registerValue(new Mat(VERSION, "DARK_OAK_PLANKS", "WOOD", 5));
	public static final Mat DARK_OAK_PRESSURE_PLATE = registerValue(new Mat(VERSION, "DARK_OAK_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat DARK_OAK_SAPLING = registerValue(new Mat(VERSION, "DARK_OAK_SAPLING", "SAPLING", 5));
	public static final Mat DARK_OAK_SLAB = registerValue(new Mat(VERSION, "DARK_OAK_SLAB", "WOOD_STEP"));
	public static final Mat DARK_OAK_STAIRS = registerValue(new Mat(VERSION, "DARK_OAK_STAIRS", "DARK_OAK_STAIRS"));
	public static final Mat DARK_OAK_TRAPDOOR = registerValue(new Mat(VERSION, "DARK_OAK_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat DARK_OAK_WOOD = registerValue(new Mat(VERSION, "DARK_OAK_WOOD", "LOG_2", 1));
	public static final Mat DARK_PRISMARINE = registerValue(new Mat(VERSION, "DARK_PRISMARINE", "PRISMARINE", 2));
	public static final Mat DARK_PRISMARINE_SLAB = registerValue(new Mat(VERSION, "DARK_PRISMARINE_SLAB", "-"));
	public static final Mat DARK_PRISMARINE_STAIRS = registerValue(new Mat(VERSION, "DARK_PRISMARINE_STAIRS", "-"));
	public static final Mat DAYLIGHT_DETECTOR = registerValue(new Mat(VERSION, "DAYLIGHT_DETECTOR", "DAYLIGHT_DETECTOR"));
	public static final Mat DEAD_BRAIN_CORAL = registerValue(new Mat(VERSION, "DEAD_BRAIN_CORAL", "-"));
	public static final Mat DEAD_BRAIN_CORAL_BLOCK = registerValue(new Mat(VERSION, "DEAD_BRAIN_CORAL_BLOCK", "-"));
	public static final Mat DEAD_BRAIN_CORAL_FAN = registerValue(new Mat(VERSION, "DEAD_BRAIN_CORAL_FAN", "-"));
	public static final Mat DEAD_BRAIN_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "DEAD_BRAIN_CORAL_WALL_FAN", "-"));
	public static final Mat DEAD_BUBBLE_CORAL = registerValue(new Mat(VERSION, "DEAD_BUBBLE_CORAL", "-"));
	public static final Mat DEAD_BUBBLE_CORAL_BLOCK = registerValue(new Mat(VERSION, "DEAD_BUBBLE_CORAL_BLOCK", "-"));
	public static final Mat DEAD_BUBBLE_CORAL_FAN = registerValue(new Mat(VERSION, "DEAD_BUBBLE_CORAL_FAN", "-"));
	public static final Mat DEAD_BUBBLE_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "DEAD_BUBBLE_CORAL_WALL_FAN", "-"));
	public static final Mat DEAD_BUSH = registerValue(new Mat(VERSION, "DEAD_BUSH", "DEAD_BUSH"));
	public static final Mat DEAD_FIRE_CORAL = registerValue(new Mat(VERSION, "DEAD_FIRE_CORAL", "-"));
	public static final Mat DEAD_FIRE_CORAL_BLOCK = registerValue(new Mat(VERSION, "DEAD_FIRE_CORAL_BLOCK", "-"));
	public static final Mat DEAD_FIRE_CORAL_FAN = registerValue(new Mat(VERSION, "DEAD_FIRE_CORAL_FAN", "-"));
	public static final Mat DEAD_FIRE_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "DEAD_FIRE_CORAL_WALL_FAN", "-"));
	public static final Mat DEAD_HORN_CORAL = registerValue(new Mat(VERSION, "DEAD_HORN_CORAL", "-"));
	public static final Mat DEAD_HORN_CORAL_BLOCK = registerValue(new Mat(VERSION, "DEAD_HORN_CORAL_BLOCK", "-"));
	public static final Mat DEAD_HORN_CORAL_FAN = registerValue(new Mat(VERSION, "DEAD_HORN_CORAL_FAN", "-"));
	public static final Mat DEAD_HORN_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "DEAD_HORN_CORAL_WALL_FAN", "-"));
	public static final Mat DEAD_TUBE_CORAL = registerValue(new Mat(VERSION, "DEAD_TUBE_CORAL", "-"));
	public static final Mat DEAD_TUBE_CORAL_BLOCK = registerValue(new Mat(VERSION, "DEAD_TUBE_CORAL_BLOCK", "-"));
	public static final Mat DEAD_TUBE_CORAL_FAN = registerValue(new Mat(VERSION, "DEAD_TUBE_CORAL_FAN", "-"));
	public static final Mat DEAD_TUBE_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "DEAD_TUBE_CORAL_WALL_FAN", "-"));
	public static final Mat DEBUG_STICK = registerValue(new Mat(VERSION, "DEBUG_STICK", "STICK"));
	public static final Mat DETECTOR_RAIL = registerValue(new Mat(VERSION, "DETECTOR_RAIL", "DETECTOR_RAIL"));
	public static final Mat DIAMOND = registerValue(new Mat(VERSION, "DIAMOND", "DIAMOND"));
	public static final Mat DIAMOND_AXE = registerValue(new Mat(VERSION, "DIAMOND_AXE", "DIAMOND_AXE"));
	public static final Mat DIAMOND_BLOCK = registerValue(new Mat(VERSION, "DIAMOND_BLOCK", "DIAMOND_BLOCK"));
	public static final Mat DIAMOND_BOOTS = registerValue(new Mat(VERSION, "DIAMOND_BOOTS", "DIAMOND_BOOTS"));
	public static final Mat DIAMOND_CHESTPLATE = registerValue(new Mat(VERSION, "DIAMOND_CHESTPLATE", "DIAMOND_CHESTPLATE"));
	public static final Mat DIAMOND_HELMET = registerValue(new Mat(VERSION, "DIAMOND_HELMET", "DIAMOND_HELMET"));
	public static final Mat DIAMOND_HOE = registerValue(new Mat(VERSION, "DIAMOND_HOE", "DIAMOND_HOE"));
	public static final Mat DIAMOND_HORSE_ARMOR = registerValue(new Mat(VERSION, "DIAMOND_HORSE_ARMOR", "DIAMOND_BARDING"));
	public static final Mat DIAMOND_LEGGINGS = registerValue(new Mat(VERSION, "DIAMOND_LEGGINGS", "DIAMOND_LEGGINGS"));
	public static final Mat DIAMOND_ORE = registerValue(new Mat(VERSION, "DIAMOND_ORE", "DIAMOND_ORE"));
	public static final Mat DIAMOND_PICKAXE = registerValue(new Mat(VERSION, "DIAMOND_PICKAXE", "DIAMOND_PICKAXE"));
	public static final Mat DIAMOND_SHOVEL = registerValue(new Mat(VERSION, "DIAMOND_SHOVEL", "DIAMOND_SPADE"));
	public static final Mat DIAMOND_SWORD = registerValue(new Mat(VERSION, "DIAMOND_SWORD", "DIAMOND_SWORD"));
	public static final Mat DIORITE = registerValue(new Mat(VERSION, "DIORITE", "STONE", 3));
	public static final Mat DIRT = registerValue(new Mat(VERSION, "DIRT", "DIRT"));
	public static final Mat DISPENSER = registerValue(new Mat(VERSION, "DISPENSER", "DISPENSER"));
	public static final Mat DOLPHIN_SPAWN_EGG = registerValue(new Mat(VERSION, "DOLPHIN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat DONKEY_SPAWN_EGG = registerValue(new Mat(VERSION, "DONKEY_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat DRAGON_BREATH = registerValue(new Mat(VERSION, "DRAGON_BREATH", "DRAGONS_BREATH"));
	public static final Mat DRAGON_EGG = registerValue(new Mat(VERSION, "DRAGON_EGG", "DRAGON_EGG"));
	public static final Mat DRAGON_HEAD = registerValue(new Mat(VERSION, "DRAGON_HEAD", "SKULL_ITEM", 5));
	public static final Mat DRAGON_WALL_HEAD = registerValue(new Mat(VERSION, "DRAGON_WALL_HEAD", "SKULL_ITEM", 5));
	public static final Mat DRIED_KELP = registerValue(new Mat(VERSION, "DRIED_KELP", "-"));
	public static final Mat DRIED_KELP_BLOCK = registerValue(new Mat(VERSION, "DRIED_KELP_BLOCK", "-"));
	public static final Mat DROPPER = registerValue(new Mat(VERSION, "DROPPER", "DROPPER"));
	public static final Mat DROWNED_SPAWN_EGG = registerValue(new Mat(VERSION, "DROWNED_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat EGG = registerValue(new Mat(VERSION, "EGG", "EGG"));
	public static final Mat ELDER_GUARDIAN_SPAWN_EGG = registerValue(new Mat(VERSION, "ELDER_GUARDIAN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ELYTRA = registerValue(new Mat(VERSION, "ELYTRA", "ELYTRA"));
	public static final Mat EMERALD = registerValue(new Mat(VERSION, "EMERALD", "EMERALD"));
	public static final Mat EMERALD_BLOCK = registerValue(new Mat(VERSION, "EMERALD_BLOCK", "EMERALD_BLOCK"));
	public static final Mat EMERALD_ORE = registerValue(new Mat(VERSION, "EMERALD_ORE", "EMERALD_ORE"));
	public static final Mat EMPTY_MAP = registerValue(new Mat(VERSION, "MAP", "EMPTY_MAP"));
	public static final Mat ENCHANTED_BOOK = registerValue(new Mat(VERSION, "ENCHANTED_BOOK", "ENCHANTED_BOOK"));
	public static final Mat ENCHANTED_GOLDEN_APPLE = registerValue(new Mat(VERSION, "ENCHANTED_GOLDEN_APPLE", "GOLDEN_APPLE", 1));
	public static final Mat ENCHANTING_TABLE = registerValue(new Mat(VERSION, "ENCHANTING_TABLE", "ENCHANTMENT_TABLE"));
	public static final Mat ENDERMAN_SPAWN_EGG = registerValue(new Mat(VERSION, "ENDERMAN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ENDERMITE_SPAWN_EGG = registerValue(new Mat(VERSION, "ENDERMITE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ENDER_CHEST = registerValue(new Mat(VERSION, "ENDER_CHEST", "ENDER_CHEST"));
	public static final Mat ENDER_EYE = registerValue(new Mat(VERSION, "ENDER_EYE", "EYE_OF_ENDER"));
	public static final Mat ENDER_PEARL = registerValue(new Mat(VERSION, "ENDER_PEARL", "ENDER_PEARL"));
	public static final Mat END_CRYSTAL = registerValue(new Mat(VERSION, "END_CRYSTAL", "END_CRYSTAL"));
	public static final Mat END_GATEWAY = registerValue(new Mat(VERSION, "END_GATEWAY", "END_GATEWAY"));
	public static final Mat END_PORTAL = registerValue(new Mat(VERSION, "END_PORTAL", "ENDER_PORTAL"));
	public static final Mat END_PORTAL_FRAME = registerValue(new Mat(VERSION, "END_PORTAL_FRAME", "ENDER_PORTAL_FRAME"));
	public static final Mat END_ROD = registerValue(new Mat(VERSION, "END_ROD", "END_ROD"));
	public static final Mat END_STONE = registerValue(new Mat(VERSION, "END_STONE", "ENDER_STONE"));
	public static final Mat END_STONE_BRICKS = registerValue(new Mat(VERSION, "END_STONE_BRICKS", "END_BRICKS"));
	public static final Mat EVOKER_SPAWN_EGG = registerValue(new Mat(VERSION, "EVOKER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat EXPERIENCE_BOTTLE = registerValue(new Mat(VERSION, "EXPERIENCE_BOTTLE", "EXP_BOTTLE"));
	public static final Mat FARMLAND = registerValue(new Mat(VERSION, "FARMLAND", "SOIL"));
	public static final Mat FEATHER = registerValue(new Mat(VERSION, "FEATHER", "FEATHER"));
	public static final Mat FERMENTED_SPIDER_EYE = registerValue(new Mat(VERSION, "FERMENTED_SPIDER_EYE", "FERMENTED_SPIDER_EYE"));
	public static final Mat FERN = registerValue(new Mat(VERSION, "FERN", "LONG_GRASS", 2));
	public static final Mat FILLED_MAP = registerValue(new Mat(VERSION, "FILLED_MAP", "MAP"));
	public static final Mat FIRE = registerValue(new Mat(VERSION, "FIRE", "FIRE"));
	public static final Mat FIREWORK_ROCKET = registerValue(new Mat(VERSION, "FIREWORK_ROCKET", "FIREWORK"));
	public static final Mat FIREWORK_STAR = registerValue(new Mat(VERSION, "FIREWORK_STAR", "FIREWORK_CHARGE"));
	public static final Mat FIRE_CHARGE = registerValue(new Mat(VERSION, "FIRE_CHARGE", "FIREBALL"));
	public static final Mat FIRE_CORAL = registerValue(new Mat(VERSION, "FIRE_CORAL", "-"));
	public static final Mat FIRE_CORAL_BLOCK = registerValue(new Mat(VERSION, "FIRE_CORAL_BLOCK", "-"));
	public static final Mat FIRE_CORAL_FAN = registerValue(new Mat(VERSION, "FIRE_CORAL_FAN", "-"));
	public static final Mat FIRE_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "FIRE_CORAL_WALL_FAN", "-"));
	public static final Mat FISHING_ROD = registerValue(new Mat(VERSION, "FISHING_ROD", "FISHING_ROD"));
	public static final Mat FLINT = registerValue(new Mat(VERSION, "FLINT", "FLINT"));
	public static final Mat FLINT_AND_STEEL = registerValue(new Mat(VERSION, "FLINT_AND_STEEL", "FLINT_AND_STEEL"));
	public static final Mat FLOWER_POT = registerValue(new Mat(VERSION, "FLOWER_POT", "FLOWER_POT"));
	public static final Mat FLOWER_POT_ITEM = registerValue(new Mat(VERSION, "FLOWER_POT", "FLOWER_POT_ITEM"));
	public static final Mat FROSTED_ICE = registerValue(new Mat(VERSION, "FROSTED_ICE", "FROSTED_ICE"));
	public static final Mat FURNACE = registerValue(new Mat(VERSION, "FURNACE", "FURNACE"));
	public static final Mat FURNACE_MINECART = registerValue(new Mat(VERSION, "FURNACE_MINECART", "POWERED_MINECART"));
	public static final Mat GHAST_SPAWN_EGG = registerValue(new Mat(VERSION, "GHAST_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat GHAST_TEAR = registerValue(new Mat(VERSION, "GHAST_TEAR", "GHAST_TEAR"));
	public static final Mat GLASS = registerValue(new Mat(VERSION, "GLASS", "GLASS"));
	public static final Mat GLASS_BOTTLE = registerValue(new Mat(VERSION, "GLASS_BOTTLE", "GLASS_BOTTLE"));
	public static final Mat GLASS_PANE = registerValue(new Mat(VERSION, "GLASS_PANE", "THIN_GLASS"));
	public static final Mat GLISTERING_MELON_SLICE = registerValue(new Mat(VERSION, "GLISTERING_MELON_SLICE", "SPECKLED_MELON"));
	public static final Mat GLOWSTONE = registerValue(new Mat(VERSION, "GLOWSTONE", "GLOWSTONE"));
	public static final Mat GLOWSTONE_DUST = registerValue(new Mat(VERSION, "GLOWSTONE_DUST", "GLOWSTONE_DUST"));
	public static final Mat GOLDEN_APPLE = registerValue(new Mat(VERSION, "GOLDEN_APPLE", "GOLDEN_APPLE"));
	public static final Mat GOLDEN_AXE = registerValue(new Mat(VERSION, "GOLDEN_AXE", "GOLD_AXE"));
	public static final Mat GOLDEN_BOOTS = registerValue(new Mat(VERSION, "GOLDEN_BOOTS", "GOLD_BOOTS"));
	public static final Mat GOLDEN_CARROT = registerValue(new Mat(VERSION, "GOLDEN_CARROT", "GOLDEN_CARROT"));
	public static final Mat GOLDEN_CHESTPLATE = registerValue(new Mat(VERSION, "GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE"));
	public static final Mat GOLDEN_HELMET = registerValue(new Mat(VERSION, "GOLDEN_HELMET", "GOLD_HELMET"));
	public static final Mat GOLDEN_HOE = registerValue(new Mat(VERSION, "GOLDEN_HOE", "GOLD_HOE"));
	public static final Mat GOLDEN_HORSE_ARMOR = registerValue(new Mat(VERSION, "GOLDEN_HORSE_ARMOR", "GOLD_BARDING"));
	public static final Mat GOLDEN_LEGGINGS = registerValue(new Mat(VERSION, "GOLDEN_LEGGINGS", "GOLD_LEGGINGS"));
	public static final Mat GOLDEN_PICKAXE = registerValue(new Mat(VERSION, "GOLDEN_PICKAXE", "GOLD_PICKAXE"));
	public static final Mat GOLDEN_SHOVEL = registerValue(new Mat(VERSION, "GOLDEN_SHOVEL", "GOLD_SPADE"));
	public static final Mat GOLDEN_SWORD = registerValue(new Mat(VERSION, "GOLDEN_SWORD", "GOLD_SWORD"));
	public static final Mat GOLD_BLOCK = registerValue(new Mat(VERSION, "GOLD_BLOCK", "GOLD_BLOCK"));
	public static final Mat GOLD_INGOT = registerValue(new Mat(VERSION, "GOLD_INGOT", "GOLD_INGOT"));
	public static final Mat GOLD_NUGGET = registerValue(new Mat(VERSION, "GOLD_NUGGET", "GOLD_NUGGET"));
	public static final Mat GOLD_ORE = registerValue(new Mat(VERSION, "GOLD_ORE", "GOLD_ORE"));
	public static final Mat GRANITE = registerValue(new Mat(VERSION, "GRANITE", "STONE", 1));
	public static final Mat GRASS = registerValue(new Mat(VERSION, "GRASS", "GRASS"));
	public static final Mat GRASS_BLOCK = registerValue(new Mat(VERSION, "GRASS_BLOCK", "GRASS"));
	public static final Mat GRASS_PATH = registerValue(new Mat(VERSION, "GRASS_PATH", "GRASS_PATH"));
	public static final Mat GRAVEL = registerValue(new Mat(VERSION, "GRAVEL", "GRAVEL"));
	public static final Mat GRAY_BANNER = registerValue(new Mat(VERSION, "GRAY_BANNER", "BANNER", 8));
	public static final Mat GRAY_BED = registerValue(new Mat(VERSION, "GRAY_BED", "BED", 7));
	public static final Mat GRAY_CARPET = registerValue(new Mat(VERSION, "GRAY_CARPET", "CARPET", 7));
	public static final Mat GRAY_CONCRETE = registerValue(new Mat(VERSION, "GRAY_CONCRETE", "STAINED_CLAY", 7));
	public static final Mat GRAY_CONCRETE_POWDER = registerValue(new Mat(VERSION, "GRAY_CONCRETE_POWDER", "CONCRETE_POWDER", 7));
	public static final Mat GRAY_DYE = registerValue(new Mat(VERSION, "GRAY_DYE", "INK_SACK", 8));
	public static final Mat GRAY_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "GRAY_GLAZED_TERRACOTTA", "GRAY_GLAZED_TERRACOTTA"));
	public static final Mat GRAY_SHULKER_BOX = registerValue(new Mat(VERSION, "GRAY_SHULKER_BOX", "GRAY_SHULKER_BOX"));
	public static final Mat GRAY_STAINED_GLASS = registerValue(new Mat(VERSION, "GRAY_STAINED_GLASS", "STAINED_GLASS", 7));
	public static final Mat GRAY_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 7));
	public static final Mat GRAY_TERRACOTTA = registerValue(new Mat(VERSION, "GRAY_TERRACOTTA", "STAINED_CLAY", 7));
	public static final Mat GRAY_WALL_BANNER = registerValue(new Mat(VERSION, "GRAY_WALL_BANNER", "WALL_BANNER"));
	public static final Mat GRAY_WOOL = registerValue(new Mat(VERSION, "GRAY_WOOL", "WOOL", 7));
	public static final Mat GREEN_BANNER = registerValue(new Mat(VERSION, "GREEN_BANNER", "BANNER", 2));
	public static final Mat GREEN_BED = registerValue(new Mat(VERSION, "GREEN_BED", "BED", 13));
	public static final Mat GREEN_CARPET = registerValue(new Mat(VERSION, "GREEN_CARPET", "CARPET", 13));
	public static final Mat GREEN_CONCRETE = registerValue(new Mat(VERSION, "GREEN_CONCRETE", "STAINED_CLAY", 13));
	public static final Mat GREEN_CONCRETE_POWDER = registerValue(new Mat(VERSION, "GREEN_CONCRETE_POWDER", "CONCRETE_POWDER", 13));
	public static final Mat GREEN_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "GREEN_GLAZED_TERRACOTTA", "GREEN_GLAZED_TERRACOTTA"));
	public static final Mat GREEN_SHULKER_BOX = registerValue(new Mat(VERSION, "GREEN_SHULKER_BOX", "GREEN_SHULKER_BOX"));
	public static final Mat GREEN_STAINED_GLASS = registerValue(new Mat(VERSION, "GREEN_STAINED_GLASS", "STAINED_GLASS", 13));
	public static final Mat GREEN_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "GREEN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 13));
	public static final Mat GREEN_TERRACOTTA = registerValue(new Mat(VERSION, "GREEN_TERRACOTTA", "STAINED_CLAY", 13));
	public static final Mat GREEN_WALL_BANNER = registerValue(new Mat(VERSION, "GREEN_WALL_BANNER", "WALL_BANNER"));
	public static final Mat GREEN_WOOL = registerValue(new Mat(VERSION, "GREEN_WOOL", "WOOL", 13));
	public static final Mat GUARDIAN_SPAWN_EGG = registerValue(new Mat(VERSION, "GUARDIAN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat GUNPOWDER = registerValue(new Mat(VERSION, "GUNPOWDER", "SULPHUR"));
	public static final Mat HAY_BLOCK = registerValue(new Mat(VERSION, "HAY_BLOCK", "HAY_BLOCK"));
	public static final Mat HEART_OF_THE_SEA = registerValue(new Mat(VERSION, "HEART_OF_THE_SEA", "-"));
	public static final Mat HEAVY_WEIGHTED_PRESSURE_PLATE = registerValue(new Mat(VERSION, "HEAVY_WEIGHTED_PRESSURE_PLATE", "IRON_PLATE"));
	public static final Mat HOPPER = registerValue(new Mat(VERSION, "HOPPER", "HOPPER"));
	public static final Mat HOPPER_MINECART = registerValue(new Mat(VERSION, "HOPPER_MINECART", "HOPPER_MINECART"));
	public static final Mat HORN_CORAL = registerValue(new Mat(VERSION, "HORN_CORAL", "-"));
	public static final Mat HORN_CORAL_BLOCK = registerValue(new Mat(VERSION, "HORN_CORAL_BLOCK", "-"));
	public static final Mat HORN_CORAL_FAN = registerValue(new Mat(VERSION, "HORN_CORAL_FAN", "-"));
	public static final Mat HORN_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "HORN_CORAL_WALL_FAN", "-"));
	public static final Mat HORSE_SPAWN_EGG = registerValue(new Mat(VERSION, "HORSE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat HUSK_SPAWN_EGG = registerValue(new Mat(VERSION, "HUSK_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ICE = registerValue(new Mat(VERSION, "ICE", "ICE"));
	public static final Mat INFESTED_CHISELED_STONE_BRICKS = registerValue(new Mat(VERSION, "INFESTED_CHISELED_STONE_BRICKS", "MONSTER_EGGS", 5));
	public static final Mat INFESTED_COBBLESTONE = registerValue(new Mat(VERSION, "INFESTED_COBBLESTONE", "MONSTER_EGGS", 1));
	public static final Mat INFESTED_CRACKED_STONE_BRICKS = registerValue(new Mat(VERSION, "INFESTED_CRACKED_STONE_BRICKS", "MONSTER_EGGS", 4));
	public static final Mat INFESTED_MOSSY_STONE_BRICKS = registerValue(new Mat(VERSION, "INFESTED_MOSSY_STONE_BRICKS", "MONSTER_EGGS", 3));
	public static final Mat INFESTED_STONE = registerValue(new Mat(VERSION, "INFESTED_STONE", "MONSTER_EGGS"));
	public static final Mat INFESTED_STONE_BRICKS = registerValue(new Mat(VERSION, "INFESTED_STONE_BRICKS", "MONSTER_EGGS", 2));
	public static final Mat INK_SAC = registerValue(new Mat(VERSION, "INK_SAC", "INK_SACK"));
	public static final Mat IRON_AXE = registerValue(new Mat(VERSION, "IRON_AXE", "IRON_AXE"));
	public static final Mat IRON_BARS = registerValue(new Mat(VERSION, "IRON_BARS", "IRON_FENCE"));
	public static final Mat IRON_BLOCK = registerValue(new Mat(VERSION, "IRON_BLOCK", "IRON_BLOCK"));
	public static final Mat IRON_BOOTS = registerValue(new Mat(VERSION, "IRON_BOOTS", "IRON_BOOTS"));
	public static final Mat IRON_CHESTPLATE = registerValue(new Mat(VERSION, "IRON_CHESTPLATE", "IRON_CHESTPLATE"));
	public static final Mat IRON_DOOR = registerValue(new Mat(VERSION, "IRON_DOOR", "IRON_DOOR"));
	public static final Mat IRON_HELMET = registerValue(new Mat(VERSION, "IRON_HELMET", "IRON_HELMET"));
	public static final Mat IRON_HOE = registerValue(new Mat(VERSION, "IRON_HOE", "IRON_HOE"));
	public static final Mat IRON_HORSE_ARMOR = registerValue(new Mat(VERSION, "IRON_HORSE_ARMOR", "IRON_BARDING"));
	public static final Mat IRON_INGOT = registerValue(new Mat(VERSION, "IRON_INGOT", "IRON_INGOT"));
	public static final Mat IRON_LEGGINGS = registerValue(new Mat(VERSION, "IRON_LEGGINGS", "IRON_LEGGINGS"));
	public static final Mat IRON_NUGGET = registerValue(new Mat(VERSION, "IRON_NUGGET", "IRON_NUGGET"));
	public static final Mat IRON_ORE = registerValue(new Mat(VERSION, "IRON_ORE", "IRON_ORE"));
	public static final Mat IRON_PICKAXE = registerValue(new Mat(VERSION, "IRON_PICKAXE", "IRON_PICKAXE"));
	public static final Mat IRON_SHOVEL = registerValue(new Mat(VERSION, "IRON_SHOVEL", "IRON_SPADE"));
	public static final Mat IRON_SWORD = registerValue(new Mat(VERSION, "IRON_SWORD", "IRON_SWORD"));
	public static final Mat IRON_TRAPDOOR = registerValue(new Mat(VERSION, "IRON_TRAPDOOR", "IRON_TRAPDOOR"));
	public static final Mat ITEM_FRAME = registerValue(new Mat(VERSION, "ITEM_FRAME", "ITEM_FRAME"));
	public static final Mat JACK_O_LANTERN = registerValue(new Mat(VERSION, "JACK_O_LANTERN", "JACK_O_LANTERN"));
	public static final Mat JUKEBOX = registerValue(new Mat(VERSION, "JUKEBOX", "JUKEBOX"));
	public static final Mat JUNGLE_BOAT = registerValue(new Mat(VERSION, "JUNGLE_BOAT", "BOAT_JUNGLE"));
	public static final Mat JUNGLE_BUTTON = registerValue(new Mat(VERSION, "JUNGLE_BUTTON", "WOOD_BUTTON"));
	public static final Mat JUNGLE_DOOR = registerValue(new Mat(VERSION, "JUNGLE_DOOR", "JUNGLE_DOOR"));
	protected static final Mat JUNGLE_DOOR_ITEM = registerValue(new Mat(VERSION, "JUNGLE_DOOR", "JUNGLE_DOOR_ITEM"));
	public static final Mat JUNGLE_FENCE = registerValue(new Mat(VERSION, "JUNGLE_FENCE", "JUNGLE_FENCE"));
	public static final Mat JUNGLE_FENCE_GATE = registerValue(new Mat(VERSION, "JUNGLE_FENCE_GATE", "JUNGLE_FENCE_GATE"));
	public static final Mat JUNGLE_LEAVES = registerValue(new Mat(VERSION, "JUNGLE_LEAVES", "LEAVES", 3));
	public static final Mat JUNGLE_LOG = registerValue(new Mat(VERSION, "JUNGLE_LOG", "LOG", 3));
	public static final Mat JUNGLE_PLANKS = registerValue(new Mat(VERSION, "JUNGLE_PLANKS", "WOOD", 3));
	public static final Mat JUNGLE_PRESSURE_PLATE = registerValue(new Mat(VERSION, "JUNGLE_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat JUNGLE_SAPLING = registerValue(new Mat(VERSION, "JUNGLE_SAPLING", "SAPLING", 3));
	public static final Mat JUNGLE_SLAB = registerValue(new Mat(VERSION, "JUNGLE_SLAB", "WOOD_STEP", 3));
	public static final Mat JUNGLE_STAIRS = registerValue(new Mat(VERSION, "JUNGLE_STAIRS", "JUNGLE_WOOD_STAIRS"));
	public static final Mat JUNGLE_TRAPDOOR = registerValue(new Mat(VERSION, "JUNGLE_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat JUNGLE_WOOD = registerValue(new Mat(VERSION, "JUNGLE_WOOD", "LOG", 3));
	public static final Mat KELP = registerValue(new Mat(VERSION, "KELP", "-"));
	public static final Mat KELP_PLANT = registerValue(new Mat(VERSION, "KELP_PLANT", "-"));
	public static final Mat KNOWLEDGE_BOOK = registerValue(new Mat(VERSION, "KNOWLEDGE_BOOK", "KNOWLEDGE_BOOK"));
	public static final Mat LADDER = registerValue(new Mat(VERSION, "LADDER", "LADDER"));
	public static final Mat LAPIS_BLOCK = registerValue(new Mat(VERSION, "LAPIS_BLOCK", "LAPIS_BLOCK"));
	public static final Mat LAPIS_LAZULI = registerValue(new Mat(VERSION, "LAPIS_LAZULI", "INK_SACK", 4));
	public static final Mat LAPIS_ORE = registerValue(new Mat(VERSION, "LAPIS_ORE", "LAPIS_ORE"));
	public static final Mat LARGE_FERN = registerValue(new Mat(VERSION, "LARGE_FERN", "DOUBLE_PLANT", 3));
	public static final Mat LAVA = registerValue(new Mat(VERSION, "LAVA", "LAVA"));
	public static final Mat STATIONARY_LAVA = registerValue(new Mat(VERSION, "LAVA", "STATIONARY_LAVA"));
	public static final Mat LAVA_BUCKET = registerValue(new Mat(VERSION, "LAVA_BUCKET", "LAVA_BUCKET"));
	public static final Mat LEAD = registerValue(new Mat(VERSION, "LEAD", "LEASH"));
	public static final Mat LEATHER = registerValue(new Mat(VERSION, "LEATHER", "LEATHER"));
	public static final Mat LEATHER_BOOTS = registerValue(new Mat(VERSION, "LEATHER_BOOTS", "LEATHER_BOOTS"));
	public static final Mat LEATHER_CHESTPLATE = registerValue(new Mat(VERSION, "LEATHER_CHESTPLATE", "LEATHER_CHESTPLATE"));
	public static final Mat LEATHER_HELMET = registerValue(new Mat(VERSION, "LEATHER_HELMET", "LEATHER_HELMET"));
	public static final Mat LEATHER_LEGGINGS = registerValue(new Mat(VERSION, "LEATHER_LEGGINGS", "LEATHER_LEGGINGS"));
	public static final Mat LEVER = registerValue(new Mat(VERSION, "LEVER", "LEVER"));
	public static final Mat LIGHT_BLUE_BANNER = registerValue(new Mat(VERSION, "LIGHT_BLUE_BANNER", "BANNER", 12));
	public static final Mat LIGHT_BLUE_BED = registerValue(new Mat(VERSION, "LIGHT_BLUE_BED", "BED", 3));
	public static final Mat LIGHT_BLUE_CARPET = registerValue(new Mat(VERSION, "LIGHT_BLUE_CARPET", "CARPET", 3));
	public static final Mat LIGHT_BLUE_CONCRETE = registerValue(new Mat(VERSION, "LIGHT_BLUE_CONCRETE", "STAINED_CLAY", 3));
	public static final Mat LIGHT_BLUE_CONCRETE_POWDER = registerValue(new Mat(VERSION, "LIGHT_BLUE_CONCRETE_POWDER", "CONCRETE_POWDER", 3));
	public static final Mat LIGHT_BLUE_DYE = registerValue(new Mat(VERSION, "LIGHT_BLUE_DYE", "INK_SACK", 12));
	public static final Mat LIGHT_BLUE_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "LIGHT_BLUE_GLAZED_TERRACOTTA", "LIGHT_BLUE_GLAZED_TERRACOTTA"));
	public static final Mat LIGHT_BLUE_SHULKER_BOX = registerValue(new Mat(VERSION, "LIGHT_BLUE_SHULKER_BOX", "LIGHT_BLUE_SHULKER_BOX"));
	public static final Mat LIGHT_BLUE_STAINED_GLASS = registerValue(new Mat(VERSION, "LIGHT_BLUE_STAINED_GLASS", "STAINED_GLASS", 3));
	public static final Mat LIGHT_BLUE_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "LIGHT_BLUE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 3));
	public static final Mat LIGHT_BLUE_TERRACOTTA = registerValue(new Mat(VERSION, "LIGHT_BLUE_TERRACOTTA", "STAINED_CLAY", 3));
	public static final Mat LIGHT_BLUE_WALL_BANNER = registerValue(new Mat(VERSION, "LIGHT_BLUE_WALL_BANNER", "BANNER"));
	public static final Mat LIGHT_BLUE_WOOL = registerValue(new Mat(VERSION, "LIGHT_BLUE_WOOL", "WOOL", 3));
	public static final Mat LIGHT_GRAY_BANNER = registerValue(new Mat(VERSION, "LIGHT_GRAY_BANNER", "BANNER", 7));
	public static final Mat LIGHT_GRAY_BED = registerValue(new Mat(VERSION, "LIGHT_GRAY_BED", "BED", 8));
	public static final Mat LIGHT_GRAY_CARPET = registerValue(new Mat(VERSION, "LIGHT_GRAY_CARPET", "CARPET", 8));
	public static final Mat LIGHT_GRAY_CONCRETE = registerValue(new Mat(VERSION, "LIGHT_GRAY_CONCRETE", "STAINED_CLAY", 8));
	public static final Mat LIGHT_GRAY_CONCRETE_POWDER = registerValue(new Mat(VERSION, "LIGHT_GRAY_CONCRETE_POWDER", "CONCRETE_POWDER", 8));
	public static final Mat LIGHT_GRAY_DYE = registerValue(new Mat(VERSION, "LIGHT_GRAY_DYE", "INK_SACK", 7));
	public static final Mat LIGHT_GRAY_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "LIGHT_GRAY_GLAZED_TERRACOTTA", "SILVER_GLAZED_TERRACOTTA"));
	public static final Mat LIGHT_GRAY_SHULKER_BOX = registerValue(new Mat(VERSION, "LIGHT_GRAY_SHULKER_BOX", "SILVER_SHULKER_BOX"));
	public static final Mat LIGHT_GRAY_STAINED_GLASS = registerValue(new Mat(VERSION, "LIGHT_GRAY_STAINED_GLASS", "STAINED_GLASS", 8));
	public static final Mat LIGHT_GRAY_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "LIGHT_GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 8));
	public static final Mat LIGHT_GRAY_TERRACOTTA = registerValue(new Mat(VERSION, "LIGHT_GRAY_TERRACOTTA", "STAINED_CLAY", 8));
	public static final Mat LIGHT_GRAY_WALL_BANNER = registerValue(new Mat(VERSION, "LIGHT_GRAY_WALL_BANNER", "WALL_BANNER"));
	public static final Mat LIGHT_GRAY_WOOL = registerValue(new Mat(VERSION, "LIGHT_GRAY_WOOL", "WOOL", 8));
	public static final Mat LIGHT_WEIGHTED_PRESSURE_PLATE = registerValue(new Mat(VERSION, "LIGHT_WEIGHTED_PRESSURE_PLATE", "GOLD_PLATE"));
	public static final Mat LILAC = registerValue(new Mat(VERSION, "LILAC", "DOUBLE_PLANT", 1));
	public static final Mat LILY_PAD = registerValue(new Mat(VERSION, "LILY_PAD", "WATER_LILY"));
	public static final Mat LIME_BANNER = registerValue(new Mat(VERSION, "LIME_BANNER", "BANNER", 10));
	public static final Mat LIME_BED = registerValue(new Mat(VERSION, "LIME_BED", "BED", 5));
	public static final Mat LIME_CARPET = registerValue(new Mat(VERSION, "LIME_CARPET", "CARPET", 5));
	public static final Mat LIME_CONCRETE = registerValue(new Mat(VERSION, "LIME_CONCRETE", "STAINED_CLAY", 5));
	public static final Mat LIME_CONCRETE_POWDER = registerValue(new Mat(VERSION, "LIME_CONCRETE_POWDER", "CONCRETE_POWDER", 5));
	public static final Mat LIME_DYE = registerValue(new Mat(VERSION, "LIME_DYE", "INK_SACK", 10));
	public static final Mat LIME_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "LIME_GLAZED_TERRACOTTA", "LIME_GLAZED_TERRACOTTA"));
	public static final Mat LIME_SHULKER_BOX = registerValue(new Mat(VERSION, "LIME_SHULKER_BOX", "LIME_SHULKER_BOX"));
	public static final Mat LIME_STAINED_GLASS = registerValue(new Mat(VERSION, "LIME_STAINED_GLASS", "STAINED_GLASS", 5));
	public static final Mat LIME_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 5));
	public static final Mat LIME_TERRACOTTA = registerValue(new Mat(VERSION, "LIME_TERRACOTTA", "STAINED_CLAY", 5));
	public static final Mat LIME_WALL_BANNER = registerValue(new Mat(VERSION, "LIME_WALL_BANNER", "WALL_BANNER"));
	public static final Mat LIME_WOOL = registerValue(new Mat(VERSION, "LIME_WOOL", "WOOL", 5));
	public static final Mat LINGERING_POTION = registerValue(new Mat(VERSION, "LINGERING_POTION", "LINGERING_POTION"));
	public static final Mat LLAMA_SPAWN_EGG = registerValue(new Mat(VERSION, "LLAMA_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat MAGENTA_BANNER = registerValue(new Mat(VERSION, "MAGENTA_BANNER", "BANNER", 13));
	public static final Mat MAGENTA_BED = registerValue(new Mat(VERSION, "MAGENTA_BED", "BED", 2));
	public static final Mat MAGENTA_CARPET = registerValue(new Mat(VERSION, "MAGENTA_CARPET", "CARPET", 2));
	public static final Mat MAGENTA_CONCRETE = registerValue(new Mat(VERSION, "MAGENTA_CONCRETE", "STAINED_CLAY", 2));
	public static final Mat MAGENTA_CONCRETE_POWDER = registerValue(new Mat(VERSION, "MAGENTA_CONCRETE_POWDER", "CONCRETE_POWDER", 2));
	public static final Mat MAGENTA_DYE = registerValue(new Mat(VERSION, "MAGENTA_DYE", "INK_SACK", 13));
	public static final Mat MAGENTA_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "MAGENTA_GLAZED_TERRACOTTA", "MAGENTA_GLAZED_TERRACOTTA"));
	public static final Mat MAGENTA_SHULKER_BOX = registerValue(new Mat(VERSION, "MAGENTA_SHULKER_BOX", "MAGENTA_SHULKER_BOX"));
	public static final Mat MAGENTA_STAINED_GLASS = registerValue(new Mat(VERSION, "MAGENTA_STAINED_GLASS", "STAINED_GLASS", 2));
	public static final Mat MAGENTA_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "MAGENTA_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 2));
	public static final Mat MAGENTA_TERRACOTTA = registerValue(new Mat(VERSION, "MAGENTA_TERRACOTTA", "STAINED_CLAY", 2));
	public static final Mat MAGENTA_WALL_BANNER = registerValue(new Mat(VERSION, "MAGENTA_WALL_BANNER", "WALL_BANNER"));
	public static final Mat MAGENTA_WOOL = registerValue(new Mat(VERSION, "MAGENTA_WOOL", "WOOL", 2));
	public static final Mat MAGMA_BLOCK = registerValue(new Mat(VERSION, "MAGMA_BLOCK", "MAGMA"));
	public static final Mat MAGMA_CREAM = registerValue(new Mat(VERSION, "MAGMA_CREAM", "MAGMA_CREAM"));
	public static final Mat MAGMA_CUBE_SPAWN_EGG = registerValue(new Mat(VERSION, "MAGMA_CUBE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat MAP = registerValue(new Mat(VERSION, "MAP", "MAP"));
	public static final Mat MELON = registerValue(new Mat(VERSION, "MELON", "MELON_BLOCK"));
	public static final Mat MELON_SEEDS = registerValue(new Mat(VERSION, "MELON_SEEDS", "MELON_SEEDS"));
	public static final Mat MELON_SLICE = registerValue(new Mat(VERSION, "MELON_SLICE", "MELON"));
	public static final Mat MELON_STEM = registerValue(new Mat(VERSION, "MELON_STEM", "MELON_STEM"));
	public static final Mat MILK_BUCKET = registerValue(new Mat(VERSION, "MILK_BUCKET", "MILK_BUCKET"));
	public static final Mat MINECART = registerValue(new Mat(VERSION, "MINECART", "MINECART"));
	public static final Mat MOOSHROOM_SPAWN_EGG = registerValue(new Mat(VERSION, "MOOSHROOM_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat MOSSY_COBBLESTONE = registerValue(new Mat(VERSION, "MOSSY_COBBLESTONE", "MOSSY_COBBLESTONE"));
	public static final Mat MOSSY_COBBLESTONE_WALL = registerValue(new Mat(VERSION, "MOSSY_COBBLESTONE_WALL", "COBBLE_WALL", 1));
	public static final Mat MOSSY_STONE_BRICKS = registerValue(new Mat(VERSION, "MOSSY_STONE_BRICKS", "SMOOTH_BRICK", 1));
	public static final Mat MOVING_PISTON = registerValue(new Mat(VERSION, "MOVING_PISTON", "PISTON_MOVING_PIECE"));
	public static final Mat MULE_SPAWN_EGG = registerValue(new Mat(VERSION, "MULE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat MUSHROOM_STEM = registerValue(new Mat(VERSION, "MUSHROOM_STEM", "BROWN_MUSHROOM"));
	public static final Mat MUSHROOM_STEW = registerValue(new Mat(VERSION, "MUSHROOM_STEW", "MUSHROOM_SOUP"));
	public static final Mat MUSIC_DISC_11 = registerValue(new Mat(VERSION, "MUSIC_DISC_11", "GOLD_RECORD"));
	public static final Mat MUSIC_DISC_13 = registerValue(new Mat(VERSION, "MUSIC_DISC_13", "GREEN_RECORD"));
	public static final Mat MUSIC_DISC_BLOCKS = registerValue(new Mat(VERSION, "MUSIC_DISC_BLOCKS", "RECORD_3"));
	public static final Mat MUSIC_DISC_CAT = registerValue(new Mat(VERSION, "MUSIC_DISC_CAT", "RECORD_4"));
	public static final Mat MUSIC_DISC_CHIRP = registerValue(new Mat(VERSION, "MUSIC_DISC_CHIRP", "RECORD_5"));
	public static final Mat MUSIC_DISC_FAR = registerValue(new Mat(VERSION, "MUSIC_DISC_FAR", "RECORD_6"));
	public static final Mat MUSIC_DISC_MALL = registerValue(new Mat(VERSION, "MUSIC_DISC_MALL", "RECORD_7"));
	public static final Mat MUSIC_DISC_MELLOHI = registerValue(new Mat(VERSION, "MUSIC_DISC_MELLOHI", "RECORD_8"));
	public static final Mat MUSIC_DISC_STAL = registerValue(new Mat(VERSION, "MUSIC_DISC_STAL", "RECORD_9"));
	public static final Mat MUSIC_DISC_STRAD = registerValue(new Mat(VERSION, "MUSIC_DISC_STRAD", "RECORD_10"));
	public static final Mat MUSIC_DISC_WAIT = registerValue(new Mat(VERSION, "MUSIC_DISC_WAIT", "RECORD_11"));
	public static final Mat MUSIC_DISC_WARD = registerValue(new Mat(VERSION, "MUSIC_DISC_WARD", "RECORD_12"));
	public static final Mat MUTTON = registerValue(new Mat(VERSION, "MUTTON", "MUTTON"));
	public static final Mat MYCELIUM = registerValue(new Mat(VERSION, "MYCELIUM", "MYCEL"));
	public static final Mat NAME_TAG = registerValue(new Mat(VERSION, "NAME_TAG", "NAME_TAG"));
	public static final Mat NAUTILUS_SHELL = registerValue(new Mat(VERSION, "NAUTILUS_SHELL", "-"));
	public static final Mat NETHERRACK = registerValue(new Mat(VERSION, "NETHERRACK", "NETHERRACK"));
	public static final Mat NETHER_BRICK = registerValue(new Mat(VERSION, "NETHER_BRICK", "NETHER_BRICK"));
	protected static final Mat NETHER_BRICK_ITEM = registerValue(new Mat(VERSION, "NETHER_BRICK", "NETHER_BRICK_ITEM"));
	public static final Mat NETHER_BRICKS = registerValue(new Mat(VERSION, "NETHER_BRICKS", "NETHER_BRICK"));
	public static final Mat NETHER_BRICK_FENCE = registerValue(new Mat(VERSION, "NETHER_BRICK_FENCE", "NETHER_FENCE"));
	public static final Mat NETHER_BRICK_SLAB = registerValue(new Mat(VERSION, "NETHER_BRICK_SLAB", "STEP", 6));
	public static final Mat NETHER_BRICK_STAIRS = registerValue(new Mat(VERSION, "NETHER_BRICK_STAIRS", "NETHER_BRICK_STAIRS"));
	public static final Mat NETHER_PORTAL = registerValue(new Mat(VERSION, "NETHER_PORTAL", "PORTAL"));
	public static final Mat NETHER_QUARTZ_ORE = registerValue(new Mat(VERSION, "NETHER_QUARTZ_ORE", "QUARTZ_ORE"));
	public static final Mat NETHER_STAR = registerValue(new Mat(VERSION, "NETHER_STAR", "NETHER_STAR"));
	public static final Mat NETHER_WART = registerValue(new Mat(VERSION, "NETHER_WART", "NETHER_STALK"));
	public static final Mat NETHER_WARTS = registerValue(new Mat(VERSION, "NETHER_WARTS", "NETHER_WARTS"));
	public static final Mat NETHER_WART_BLOCK = registerValue(new Mat(VERSION, "NETHER_WART_BLOCK", "NETHER_WART_BLOCK"));
	public static final Mat NOTE_BLOCK = registerValue(new Mat(VERSION, "NOTE_BLOCK", "NOTE_BLOCK"));
	public static final Mat OAK_BOAT = registerValue(new Mat(VERSION, "OAK_BOAT", "BOAT"));
	public static final Mat OAK_BUTTON = registerValue(new Mat(VERSION, "OAK_BUTTON", "WOOD_BUTTON"));
	public static final Mat OAK_DOOR = registerValue(new Mat(VERSION, "OAK_DOOR", "WOOD_DOOR"));
	public static final Mat OAK_FENCE = registerValue(new Mat(VERSION, "OAK_FENCE", "FENCE"));
	public static final Mat OAK_FENCE_GATE = registerValue(new Mat(VERSION, "OAK_FENCE_GATE", "FENCE_GATE"));
	public static final Mat OAK_LEAVES = registerValue(new Mat(VERSION, "OAK_LEAVES", "LEAVES"));
	public static final Mat OAK_LOG = registerValue(new Mat(VERSION, "OAK_LOG", "LOG"));
	public static final Mat OAK_PLANKS = registerValue(new Mat(VERSION, "OAK_PLANKS", "WOOD"));
	public static final Mat OAK_PRESSURE_PLATE = registerValue(new Mat(VERSION, "OAK_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat OAK_SAPLING = registerValue(new Mat(VERSION, "OAK_SAPLING", "SAPLING"));
	public static final Mat OAK_SLAB = registerValue(new Mat(VERSION, "OAK_SLAB", "WOOD_STEP"));
	public static final Mat OAK_STAIRS = registerValue(new Mat(VERSION, "OAK_STAIRS", "WOOD_STAIRS"));
	public static final Mat OAK_TRAPDOOR = registerValue(new Mat(VERSION, "OAK_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat OAK_WOOD = registerValue(new Mat(VERSION, "OAK_WOOD", "LOG"));
	public static final Mat OBSERVER = registerValue(new Mat(VERSION, "OBSERVER", "OBSERVER"));
	public static final Mat OBSIDIAN = registerValue(new Mat(VERSION, "OBSIDIAN", "OBSIDIAN"));
	public static final Mat OCELOT_SPAWN_EGG = registerValue(new Mat(VERSION, "OCELOT_SPAWN_EGG", "RECORD_12"));
	public static final Mat ORANGE_BANNER = registerValue(new Mat(VERSION, "ORANGE_BANNER", "BANNER", 14));
	public static final Mat ORANGE_BED = registerValue(new Mat(VERSION, "ORANGE_BED", "BED", 1));
	public static final Mat ORANGE_CARPET = registerValue(new Mat(VERSION, "ORANGE_CARPET", "CARPET", 1));
	public static final Mat ORANGE_CONCRETE = registerValue(new Mat(VERSION, "ORANGE_CONCRETE", "STAINED_CLAY", 1));
	public static final Mat ORANGE_CONCRETE_POWDER = registerValue(new Mat(VERSION, "ORANGE_CONCRETE_POWDER", "CONCRETE_POWDER", 1));
	public static final Mat ORANGE_DYE = registerValue(new Mat(VERSION, "ORANGE_DYE", "INK_SACK", 14));
	public static final Mat ORANGE_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "ORANGE_GLAZED_TERRACOTTA", "ORANGE_GLAZED_TERRACOTTA"));
	public static final Mat ORANGE_SHULKER_BOX = registerValue(new Mat(VERSION, "ORANGE_SHULKER_BOX", "ORANGE_SHULKER_BOX"));
	public static final Mat ORANGE_STAINED_GLASS = registerValue(new Mat(VERSION, "ORANGE_STAINED_GLASS", "STAINED_GLASS", 1));
	public static final Mat ORANGE_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "ORANGE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 1));
	public static final Mat ORANGE_TERRACOTTA = registerValue(new Mat(VERSION, "ORANGE_TERRACOTTA", "STAINED_CLAY", 1));
	public static final Mat ORANGE_TULIP = registerValue(new Mat(VERSION, "ORANGE_TULIP", "RED_ROSE", 5));
	public static final Mat ORANGE_WALL_BANNER = registerValue(new Mat(VERSION, "ORANGE_WALL_BANNER", "WALL_BANNER"));
	public static final Mat ORANGE_WOOL = registerValue(new Mat(VERSION, "ORANGE_WOOL", "WOOL", 1));
	public static final Mat OXEYE_DAISY = registerValue(new Mat(VERSION, "OXEYE_DAISY", "RED_ROSE", 8));
	public static final Mat PACKED_ICE = registerValue(new Mat(VERSION, "PACKED_ICE", "PACKED_ICE"));
	public static final Mat PAINTING = registerValue(new Mat(VERSION, "PAINTING", "PAINTING"));
	public static final Mat PAPER = registerValue(new Mat(VERSION, "PAPER", "PAPER"));
	public static final Mat PARROT_SPAWN_EGG = registerValue(new Mat(VERSION, "PARROT_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat PEONY = registerValue(new Mat(VERSION, "PEONY", "DOUBLE_PLANT", 5));
	public static final Mat PETRIFIED_OAK_SLAB = registerValue(new Mat(VERSION, "PETRIFIED_OAK_SLAB", "-"));
	public static final Mat PHANTOM_MEMBRANE = registerValue(new Mat(VERSION, "PHANTOM_MEMBRANE", "-"));
	public static final Mat PHANTOM_SPAWN_EGG = registerValue(new Mat(VERSION, "PHANTOM_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat PIG_SPAWN_EGG = registerValue(new Mat(VERSION, "PIG_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat PINK_BANNER = registerValue(new Mat(VERSION, "PINK_BANNER", "BANNER", 9));
	public static final Mat PINK_BED = registerValue(new Mat(VERSION, "PINK_BED", "BED", 6));
	public static final Mat PINK_CARPET = registerValue(new Mat(VERSION, "PINK_CARPET", "CARPET", 6));
	public static final Mat PINK_CONCRETE = registerValue(new Mat(VERSION, "PINK_CONCRETE", "STAINED_CLAY", 6));
	public static final Mat PINK_CONCRETE_POWDER = registerValue(new Mat(VERSION, "PINK_CONCRETE_POWDER", "CONCRETE_POWDER", 6));
	public static final Mat PINK_DYE = registerValue(new Mat(VERSION, "PINK_DYE", "INK_SACK", 9));
	public static final Mat PINK_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "PINK_GLAZED_TERRACOTTA", "PINK_GLAZED_TERRACOTTA"));
	public static final Mat PINK_SHULKER_BOX = registerValue(new Mat(VERSION, "PINK_SHULKER_BOX", "PINK_SHULKER_BOX"));
	public static final Mat PINK_STAINED_GLASS = registerValue(new Mat(VERSION, "PINK_STAINED_GLASS", "STAINED_GLASS", 6));
	public static final Mat PINK_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "PINK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 6));
	public static final Mat PINK_TERRACOTTA = registerValue(new Mat(VERSION, "PINK_TERRACOTTA", "STAINED_CLAY", 6));
	public static final Mat PINK_TULIP = registerValue(new Mat(VERSION, "PINK_TULIP", "RED_ROSE", 7));
	public static final Mat PINK_WALL_BANNER = registerValue(new Mat(VERSION, "PINK_WALL_BANNER", "WALL_BANNER"));
	public static final Mat PINK_WOOL = registerValue(new Mat(VERSION, "PINK_WOOL", "WOOL", 6));
	public static final Mat PISTON = registerValue(new Mat(VERSION, "PISTON", "PISTON_BASE"));
	public static final Mat PISTON_HEAD = registerValue(new Mat(VERSION, "PISTON_HEAD", "PISTON_EXTENSION"));
	public static final Mat PLAYER_HEAD = registerValue(new Mat(VERSION, "PLAYER_HEAD", "SKULL_ITEM", 3));
	public static final Mat PLAYER_WALL_HEAD = registerValue(new Mat(VERSION, "PLAYER_WALL_HEAD", "SKULL_ITEM", 3));
	public static final Mat PODZOL = registerValue(new Mat(VERSION, "PODZOL", "DIRT", 2));
	public static final Mat POISONOUS_POTATO = registerValue(new Mat(VERSION, "POISONOUS_POTATO", "POISONOUS_POTATO"));
	public static final Mat POLAR_BEAR_SPAWN_EGG = registerValue(new Mat(VERSION, "POLAR_BEAR_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat POLISHED_ANDESITE = registerValue(new Mat(VERSION, "POLISHED_ANDESITE", "STONE", 6));
	public static final Mat POLISHED_DIORITE = registerValue(new Mat(VERSION, "POLISHED_DIORITE", "STONE", 4));
	public static final Mat POLISHED_GRANITE = registerValue(new Mat(VERSION, "POLISHED_GRANITE", "STONE", 2));
	public static final Mat POPPED_CHORUS_FRUIT = registerValue(new Mat(VERSION, "POPPED_CHORUS_FRUIT", "CHORUS_FRUIT_POPPED"));
	public static final Mat POPPY = registerValue(new Mat(VERSION, "POPPY", "RED_ROSE"));
	public static final Mat PORKCHOP = registerValue(new Mat(VERSION, "PORKCHOP", "PORK"));
	public static final Mat POTATO = registerValue(new Mat(VERSION, "POTATO", "POTATO_ITEM"));
	public static final Mat POTATOES = registerValue(new Mat(VERSION, "POTATOES", "POTATO"));
	public static final Mat POTION = registerValue(new Mat(VERSION, "POTION", "POTION"));
	public static final Mat POTTED_ACACIA_SAPLING = registerValue(new Mat(VERSION, "POTTED_ACACIA_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_ALLIUM = registerValue(new Mat(VERSION, "POTTED_ALLIUM", "FLOWER_POT"));
	public static final Mat POTTED_AZURE_BLUET = registerValue(new Mat(VERSION, "POTTED_AZURE_BLUET", "FLOWER_POT"));
	public static final Mat POTTED_BIRCH_SAPLING = registerValue(new Mat(VERSION, "POTTED_BIRCH_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_BLUE_ORCHID = registerValue(new Mat(VERSION, "POTTED_BLUE_ORCHID", "FLOWER_POT"));
	public static final Mat POTTED_BROWN_MUSHROOM = registerValue(new Mat(VERSION, "POTTED_BROWN_MUSHROOM", "FLOWER_POT"));
	public static final Mat POTTED_CACTUS = registerValue(new Mat(VERSION, "POTTED_CACTUS", "FLOWER_POT"));
	public static final Mat POTTED_DANDELION = registerValue(new Mat(VERSION, "POTTED_DANDELION", "FLOWER_POT"));
	public static final Mat POTTED_DARK_OAK_SAPLING = registerValue(new Mat(VERSION, "POTTED_DARK_OAK_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_DEAD_BUSH = registerValue(new Mat(VERSION, "POTTED_DEAD_BUSH", "FLOWER_POT"));
	public static final Mat POTTED_FERN = registerValue(new Mat(VERSION, "POTTED_FERN", "FLOWER_POT"));
	public static final Mat POTTED_JUNGLE_SAPLING = registerValue(new Mat(VERSION, "POTTED_JUNGLE_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_OAK_SAPLING = registerValue(new Mat(VERSION, "POTTED_OAK_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_ORANGE_TULIP = registerValue(new Mat(VERSION, "POTTED_ORANGE_TULIP", "FLOWER_POT"));
	public static final Mat POTTED_OXEYE_DAISY = registerValue(new Mat(VERSION, "POTTED_OXEYE_DAISY", "FLOWER_POT"));
	public static final Mat POTTED_PINK_TULIP = registerValue(new Mat(VERSION, "POTTED_PINK_TULIP", "FLOWER_POT"));
	public static final Mat POTTED_POPPY = registerValue(new Mat(VERSION, "POTTED_POPPY", "FLOWER_POT"));
	public static final Mat POTTED_RED_MUSHROOM = registerValue(new Mat(VERSION, "POTTED_RED_MUSHROOM", "FLOWER_POT"));
	public static final Mat POTTED_RED_TULIP = registerValue(new Mat(VERSION, "POTTED_RED_TULIP", "FLOWER_POT"));
	public static final Mat POTTED_SPRUCE_SAPLING = registerValue(new Mat(VERSION, "POTTED_SPRUCE_SAPLING", "FLOWER_POT"));
	public static final Mat POTTED_WHITE_TULIP = registerValue(new Mat(VERSION, "POTTED_WHITE_TULIP", "FLOWER_POT"));
	public static final Mat POWERED_RAIL = registerValue(new Mat(VERSION, "POWERED_RAIL", "POWERED_RAIL"));
	public static final Mat PRISMARINE = registerValue(new Mat(VERSION, "PRISMARINE", "PRISMARINE"));
	public static final Mat PRISMARINE_BRICKS = registerValue(new Mat(VERSION, "PRISMARINE_BRICKS", "PRISMARINE", 1));
	public static final Mat PRISMARINE_BRICK_SLAB = registerValue(new Mat(VERSION, "PRISMARINE_BRICK_SLAB", "-"));
	public static final Mat PRISMARINE_BRICK_STAIRS = registerValue(new Mat(VERSION, "PRISMARINE_BRICK_STAIRS", "-"));
	public static final Mat PRISMARINE_CRYSTALS = registerValue(new Mat(VERSION, "PRISMARINE_CRYSTALS", "PRISMARINE_CRYSTALS"));
	public static final Mat PRISMARINE_SHARD = registerValue(new Mat(VERSION, "PRISMARINE_SHARD", "PRISMARINE_SHARD"));
	public static final Mat PRISMARINE_SLAB = registerValue(new Mat(VERSION, "PRISMARINE_SLAB", "-"));
	public static final Mat PRISMARINE_STAIRS = registerValue(new Mat(VERSION, "PRISMARINE_STAIRS", "-"));
	public static final Mat PUFFERFISH = registerValue(new Mat(VERSION, "PUFFERFISH", "RAW_FISH", 3));
	public static final Mat PUFFERFISH_BUCKET = registerValue(new Mat(VERSION, "PUFFERFISH_BUCKET", "-"));
	public static final Mat PUFFERFISH_SPAWN_EGG = registerValue(new Mat(VERSION, "PUFFERFISH_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat PUMPKIN = registerValue(new Mat(VERSION, "PUMPKIN", "PUMPKIN"));
	public static final Mat PUMPKIN_PIE = registerValue(new Mat(VERSION, "PUMPKIN_PIE", "PUMPKIN_PIE"));
	public static final Mat PUMPKIN_SEEDS = registerValue(new Mat(VERSION, "PUMPKIN_SEEDS", "PUMPKIN_SEEDS"));
	public static final Mat PUMPKIN_STEM = registerValue(new Mat(VERSION, "PUMPKIN_STEM", "PUMPKIN_STEM"));
	public static final Mat PURPLE_BANNER = registerValue(new Mat(VERSION, "PURPLE_BANNER", "BANNER", 5));
	public static final Mat PURPLE_BED = registerValue(new Mat(VERSION, "PURPLE_BED", "BED", 10));
	public static final Mat PURPLE_CARPET = registerValue(new Mat(VERSION, "PURPLE_CARPET", "CARPET", 10));
	public static final Mat PURPLE_CONCRETE = registerValue(new Mat(VERSION, "PURPLE_CONCRETE", "STAINED_CLAY", 10));
	public static final Mat PURPLE_CONCRETE_POWDER = registerValue(new Mat(VERSION, "PURPLE_CONCRETE_POWDER", "CONCRETE_POWDER", 10));
	public static final Mat PURPLE_DYE = registerValue(new Mat(VERSION, "PURPLE_DYE", "INK_SACK", 5));
	public static final Mat PURPLE_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "PURPLE_GLAZED_TERRACOTTA", "PURPLE_GLAZED_TERRACOTTA"));
	public static final Mat PURPLE_SHULKER_BOX = registerValue(new Mat(VERSION, "PURPLE_SHULKER_BOX", "PURPLE_SHULKER_BOX"));
	public static final Mat PURPLE_STAINED_GLASS = registerValue(new Mat(VERSION, "PURPLE_STAINED_GLASS", "STAINED_GLASS", 10));
	public static final Mat PURPLE_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "PURPLE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 10));
	public static final Mat PURPLE_TERRACOTTA = registerValue(new Mat(VERSION, "PURPLE_TERRACOTTA", "STAINED_CLAY", 10));
	public static final Mat PURPLE_WALL_BANNER = registerValue(new Mat(VERSION, "PURPLE_WALL_BANNER", "WALL_BANNER"));
	public static final Mat PURPLE_WOOL = registerValue(new Mat(VERSION, "PURPLE_WOOL", "WOOL", 10));
	public static final Mat PURPUR_BLOCK = registerValue(new Mat(VERSION, "PURPUR_BLOCK", "PURPUR_BLOCK"));
	public static final Mat PURPUR_PILLAR = registerValue(new Mat(VERSION, "PURPUR_PILLAR", "PURPUR_PILLAR"));
	public static final Mat PURPUR_SLAB = registerValue(new Mat(VERSION, "PURPUR_SLAB", "PURPUR_SLAB"));
	public static final Mat PURPUR_STAIRS = registerValue(new Mat(VERSION, "PURPUR_STAIRS", "PURPUR_STAIRS"));
	public static final Mat QUARTZ = registerValue(new Mat(VERSION, "QUARTZ", "QUARTZ"));
	public static final Mat QUARTZ_BLOCK = registerValue(new Mat(VERSION, "QUARTZ_BLOCK", "QUARTZ_BLOCK"));
	public static final Mat QUARTZ_PILLAR = registerValue(new Mat(VERSION, "QUARTZ_PILLAR", "QUARTZ_BLOCK", 2));
	public static final Mat QUARTZ_SLAB = registerValue(new Mat(VERSION, "QUARTZ_SLAB", "STEP", 7));
	public static final Mat QUARTZ_STAIRS = registerValue(new Mat(VERSION, "QUARTZ_STAIRS", "QUARTZ_STAIRS"));
	public static final Mat RABBIT = registerValue(new Mat(VERSION, "RABBIT", "RABBIT"));
	public static final Mat RABBIT_FOOT = registerValue(new Mat(VERSION, "RABBIT_FOOT", "RABBIT_FOOT"));
	public static final Mat RABBIT_HIDE = registerValue(new Mat(VERSION, "RABBIT_HIDE", "RABBIT_HIDE"));
	public static final Mat RABBIT_SPAWN_EGG = registerValue(new Mat(VERSION, "RABBIT_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat RABBIT_STEW = registerValue(new Mat(VERSION, "RABBIT_STEW", "RABBIT_STEW"));
	public static final Mat RAIL = registerValue(new Mat(VERSION, "RAIL", "RAILS"));
	public static final Mat REDSTONE = registerValue(new Mat(VERSION, "REDSTONE", "REDSTONE"));
	public static final Mat REDSTONE_BLOCK = registerValue(new Mat(VERSION, "REDSTONE_BLOCK", "REDSTONE_BLOCK"));
	public static final Mat REDSTONE_LAMP = registerValue(new Mat(VERSION, "REDSTONE_LAMP", "REDSTONE_LAMP_OFF"));
	public static final Mat REDSTONE_ORE = registerValue(new Mat(VERSION, "REDSTONE_ORE", "REDSTONE_ORE"));
	public static final Mat GLOWING_REDSTONE_ORE = registerValue(new Mat(VERSION, "GLOWING_REDSTONE_ORE", "REDSTONE_ORE"));
	public static final Mat REDSTONE_TORCH = registerValue(new Mat(VERSION, "REDSTONE_TORCH", "REDSTONE_TORCH_ON")); 
	public static final Mat REDSTONE_WALL_TORCH = registerValue(new Mat(VERSION, "REDSTONE_WALL_TORCH", "REDSTONE_TORCH_ON", 1));
	public static final Mat REDSTONE_WIRE = registerValue(new Mat(VERSION, "REDSTONE_WIRE", "REDSTONE_WIRE"));
	public static final Mat RED_BANNER = registerValue(new Mat(VERSION, "RED_BANNER", "BANNER", 1));
	public static final Mat RED_BED = registerValue(new Mat(VERSION, "RED_BED", "BED", 14));
	public static final Mat RED_CARPET = registerValue(new Mat(VERSION, "RED_CARPET", "CARPET", 14));
	public static final Mat RED_CONCRETE = registerValue(new Mat(VERSION, "RED_CONCRETE", "STAINED_CLAY", 14));
	public static final Mat RED_CONCRETE_POWDER = registerValue(new Mat(VERSION, "RED_CONCRETE_POWDER", "CONCRETE_POWDER", 14));
	public static final Mat RED_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "RED_GLAZED_TERRACOTTA", "RED_GLAZED_TERRACOTTA"));
	public static final Mat RED_MUSHROOM = registerValue(new Mat(VERSION, "RED_MUSHROOM", "RED_MUSHROOM"));
	public static final Mat RED_MUSHROOM_BLOCK = registerValue(new Mat(VERSION, "RED_MUSHROOM_BLOCK", "RED_MUSHROOM"));
	public static final Mat RED_NETHER_BRICKS = registerValue(new Mat(VERSION, "RED_NETHER_BRICKS", "RED_NETHER_BRICK"));
	public static final Mat RED_SAND = registerValue(new Mat(VERSION, "RED_SAND", "SAND", 1));
	public static final Mat RED_SANDSTONE = registerValue(new Mat(VERSION, "RED_SANDSTONE", "RED_SANDSTONE"));
	public static final Mat RED_SANDSTONE_SLAB = registerValue(new Mat(VERSION, "RED_SANDSTONE_SLAB", "STONE_SLAB2"));
	public static final Mat RED_SANDSTONE_STAIRS = registerValue(new Mat(VERSION, "RED_SANDSTONE_STAIRS", "RED_SANDSTONE_STAIRS"));
	public static final Mat RED_SHULKER_BOX = registerValue(new Mat(VERSION, "RED_SHULKER_BOX", "RED_SHULKER_BOX"));
	public static final Mat RED_STAINED_GLASS = registerValue(new Mat(VERSION, "RED_STAINED_GLASS", "STAINED_GLASS", 14));
	public static final Mat RED_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 14));
	public static final Mat RED_TERRACOTTA = registerValue(new Mat(VERSION, "RED_TERRACOTTA", "STAINED_CLAY", 14));
	public static final Mat RED_TULIP = registerValue(new Mat(VERSION, "RED_TULIP", "RED_ROSE", 4));
	public static final Mat RED_WALL_BANNER = registerValue(new Mat(VERSION, "RED_WALL_BANNER", "WALL_BANNER"));
	public static final Mat RED_WOOL = registerValue(new Mat(VERSION, "RED_WOOL", "WOOL", 14));
	public static final Mat REPEATER = registerValue(new Mat(VERSION, "REPEATER", "DIODE"));
	public static final Mat REPEATING_COMMAND_BLOCK = registerValue(new Mat(VERSION, "REPEATING_COMMAND_BLOCK", "COMMAND_REPEATING"));
	public static final Mat ROSE_BUSH = registerValue(new Mat(VERSION, "ROSE_BUSH", "DOUBLE_PLANT", 4));
	public static final Mat ROSE_RED = registerValue(new Mat(VERSION, "ROSE_RED", "INK_SACK", 1));
	public static final Mat ROTTEN_FLESH = registerValue(new Mat(VERSION, "ROTTEN_FLESH", "ROTTEN_FLESH"));
	public static final Mat SADDLE = registerValue(new Mat(VERSION, "SADDLE", "SADDLE"));
	public static final Mat SALMON = registerValue(new Mat(VERSION, "SALMON", "RAW_FISH", 1));
	public static final Mat SALMON_BUCKET = registerValue(new Mat(VERSION, "SALMON_BUCKET", "BUCKET"));
	public static final Mat SALMON_SPAWN_EGG = registerValue(new Mat(VERSION, "SALMON_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SAND = registerValue(new Mat(VERSION, "SAND", "SAND"));
	public static final Mat SANDSTONE = registerValue(new Mat(VERSION, "SANDSTONE", "SANDSTONE"));
	public static final Mat SANDSTONE_SLAB = registerValue(new Mat(VERSION, "SANDSTONE_SLAB", "STEP", 1));
	public static final Mat SANDSTONE_STAIRS = registerValue(new Mat(VERSION, "SANDSTONE_STAIRS", "SANDSTONE_STAIRS"));
	public static final Mat SCUTE = registerValue(new Mat(VERSION, "SCUTE", "-"));
	public static final Mat SEAGRASS = registerValue(new Mat(VERSION, "SEAGRASS", "-"));
	public static final Mat SEA_LANTERN = registerValue(new Mat(VERSION, "SEA_LANTERN", "SEA_LANTERN"));
	public static final Mat SEA_PICKLE = registerValue(new Mat(VERSION, "SEA_PICKLE", "-"));
	public static final Mat SHEARS = registerValue(new Mat(VERSION, "SHEARS", "SHEARS"));
	public static final Mat SHEEP_SPAWN_EGG = registerValue(new Mat(VERSION, "SHEEP_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SHIELD = registerValue(new Mat(VERSION, "SHIELD", "SHIELD"));
	public static final Mat SHULKER_BOX = registerValue(new Mat(VERSION, "SHULKER_BOX", "PURPLE_SHULKER_BOX"));
	public static final Mat SHULKER_SHELL = registerValue(new Mat(VERSION, "SHULKER_SHELL", "SHULKER_SHELL"));
	public static final Mat SHULKER_SPAWN_EGG = registerValue(new Mat(VERSION, "SHULKER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SIGN = registerValue(new Mat(VERSION, "SIGN", "SIGN"));
	public static final Mat SIGN_POST = registerValue(new Mat(VERSION, "SIGN_POST", "SIGN_POST"));
	public static final Mat SILVERFISH_SPAWN_EGG = registerValue(new Mat(VERSION, "SILVERFISH_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SKELETON_HORSE_SPAWN_EGG = registerValue(new Mat(VERSION, "SKELETON_HORSE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SKELETON_SKULL = registerValue(new Mat(VERSION, "SKELETON_SKULL", "SKULL_ITEM"));
	public static final Mat SKELETON_SPAWN_EGG = registerValue(new Mat(VERSION, "SKELETON_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SKELETON_WALL_SKULL = registerValue(new Mat(VERSION, "SKELETON_WALL_SKULL", "SKULL_ITEM"));
	public static final Mat SLIME_BALL = registerValue(new Mat(VERSION, "SLIME_BALL", "SLIME_BALL"));
	public static final Mat SLIME_BLOCK = registerValue(new Mat(VERSION, "SLIME_BLOCK", "SLIME_BLOCK"));
	public static final Mat SLIME_SPAWN_EGG = registerValue(new Mat(VERSION, "SLIME_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SMOOTH_QUARTZ = registerValue(new Mat(VERSION, "SMOOTH_QUARTZ", "-"));
	public static final Mat SMOOTH_RED_SANDSTONE = registerValue(new Mat(VERSION, "SMOOTH_RED_SANDSTONE", "RED_SANDSTONE", 2));
	public static final Mat SMOOTH_SANDSTONE = registerValue(new Mat(VERSION, "SMOOTH_SANDSTONE", "SANDSTONE", 2));
	public static final Mat SMOOTH_STONE = registerValue(new Mat(VERSION, "SMOOTH_STONE", "STEP"));
	public static final Mat SNOW = registerValue(new Mat(VERSION, "SNOW", "SNOW"));
	public static final Mat SNOWBALL = registerValue(new Mat(VERSION, "SNOWBALL", "SNOW_BALL"));
	public static final Mat SNOW_BLOCK = registerValue(new Mat(VERSION, "SNOW_BLOCK", "SNOW_BLOCK"));
	public static final Mat SOUL_SAND = registerValue(new Mat(VERSION, "SOUL_SAND", "SOUL_SAND"));
	public static final Mat SPAWNER = registerValue(new Mat(VERSION, "SPAWNER", "MOB_SPAWNER"));
	public static final Mat SPECTRAL_ARROW = registerValue(new Mat(VERSION, "SPECTRAL_ARROW", "SPECTRAL_ARROW"));
	public static final Mat SPIDER_EYE = registerValue(new Mat(VERSION, "SPIDER_EYE", "SPIDER_EYE"));
	public static final Mat SPIDER_SPAWN_EGG = registerValue(new Mat(VERSION, "SPIDER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat SPLASH_POTION = registerValue(new Mat(VERSION, "SPLASH_POTION", "SPLASH_POTION"));
	public static final Mat SPONGE = registerValue(new Mat(VERSION, "SPONGE", "SPONGE"));
	public static final Mat SPRUCE_BOAT = registerValue(new Mat(VERSION, "SPRUCE_BOAT", "BOAT_SPRUCE"));
	public static final Mat SPRUCE_BUTTON = registerValue(new Mat(VERSION, "SPRUCE_BUTTON", "WOOD_BUTTON"));
	public static final Mat SPRUCE_DOOR = registerValue(new Mat(VERSION, "SPRUCE_DOOR", "SPRUCE_DOOR"));
	protected static final Mat SPRUCE_DOOR_ITEM = registerValue(new Mat(VERSION, "SPRUCE_DOOR", "SPRUCE_DOOR_ITEM"));
	public static final Mat SPRUCE_FENCE = registerValue(new Mat(VERSION, "SPRUCE_FENCE", "SPRUCE_FENCE"));
	public static final Mat SPRUCE_FENCE_GATE = registerValue(new Mat(VERSION, "SPRUCE_FENCE_GATE", "SPRUCE_FENCE_GATE"));
	public static final Mat SPRUCE_LEAVES = registerValue(new Mat(VERSION, "SPRUCE_LEAVES", "LEAVES", 1));
	public static final Mat SPRUCE_LOG = registerValue(new Mat(VERSION, "SPRUCE_LOG", "LOG", 1));
	public static final Mat SPRUCE_PLANKS = registerValue(new Mat(VERSION, "SPRUCE_PLANKS", "WOOD", 1));
	public static final Mat SPRUCE_PRESSURE_PLATE = registerValue(new Mat(VERSION, "SPRUCE_PRESSURE_PLATE", "WOOD_PLATE"));
	public static final Mat SPRUCE_SAPLING = registerValue(new Mat(VERSION, "SPRUCE_SAPLING", "SAPLING", 1));
	public static final Mat SPRUCE_SLAB = registerValue(new Mat(VERSION, "SPRUCE_SLAB", "WOOD_STEP", 1));
	public static final Mat SPRUCE_STAIRS = registerValue(new Mat(VERSION, "SPRUCE_STAIRS", "SPRUCE_WOOD_STAIRS"));
	public static final Mat SPRUCE_TRAPDOOR = registerValue(new Mat(VERSION, "SPRUCE_TRAPDOOR", "TRAP_DOOR"));
	public static final Mat SPRUCE_WOOD = registerValue(new Mat(VERSION, "SPRUCE_WOOD", "LOG", 1));
	public static final Mat SQUID_SPAWN_EGG = registerValue(new Mat(VERSION, "SQUID_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat STICK = registerValue(new Mat(VERSION, "STICK", "STICK"));
	public static final Mat STICKY_PISTON = registerValue(new Mat(VERSION, "STICKY_PISTON", "PISTON_STICKY_BASE"));
	public static final Mat STONE = registerValue(new Mat(VERSION, "STONE", "STONE"));
	public static final Mat STONE_AXE = registerValue(new Mat(VERSION, "STONE_AXE", "STONE_AXE"));
	public static final Mat STONE_BRICKS = registerValue(new Mat(VERSION, "STONE_BRICKS", "SMOOTH_BRICK"));
	public static final Mat STONE_BRICK_SLAB = registerValue(new Mat(VERSION, "STONE_BRICK_SLAB", "STEP", 5));
	public static final Mat STONE_BRICK_STAIRS = registerValue(new Mat(VERSION, "STONE_BRICK_STAIRS", "SMOOTH_STAIRS"));
	public static final Mat STONE_BUTTON = registerValue(new Mat(VERSION, "STONE_BUTTON", "STONE_BUTTON"));
	public static final Mat STONE_HOE = registerValue(new Mat(VERSION, "STONE_HOE", "STONE_HOE"));
	public static final Mat STONE_PICKAXE = registerValue(new Mat(VERSION, "STONE_PICKAXE", "STONE_PICKAXE"));
	public static final Mat STONE_PRESSURE_PLATE = registerValue(new Mat(VERSION, "STONE_PRESSURE_PLATE", "STONE_PLATE"));
	public static final Mat STONE_SHOVEL = registerValue(new Mat(VERSION, "STONE_SHOVEL", "STONE_SPADE"));
	public static final Mat STONE_SLAB = registerValue(new Mat(VERSION, "STONE_SLAB", "STEP"));
	public static final Mat STONE_SWORD = registerValue(new Mat(VERSION, "STONE_SWORD", "STONE_SWORD"));
	public static final Mat STRAY_SPAWN_EGG = registerValue(new Mat(VERSION, "STRAY_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat STRING = registerValue(new Mat(VERSION, "STRING", "STRING"));
	public static final Mat STRIPPED_ACACIA_LOG = registerValue(new Mat(VERSION, "STRIPPED_ACACIA_LOG", "-"));
	public static final Mat STRIPPED_ACACIA_WOOD = registerValue(new Mat(VERSION, "STRIPPED_ACACIA_WOOD", "-"));
	public static final Mat STRIPPED_BIRCH_LOG = registerValue(new Mat(VERSION, "STRIPPED_BIRCH_LOG", "-"));
	public static final Mat STRIPPED_BIRCH_WOOD = registerValue(new Mat(VERSION, "STRIPPED_BIRCH_WOOD", "-"));
	public static final Mat STRIPPED_DARK_OAK_LOG = registerValue(new Mat(VERSION, "STRIPPED_DARK_OAK_LOG", "-"));
	public static final Mat STRIPPED_DARK_OAK_WOOD = registerValue(new Mat(VERSION, "STRIPPED_DARK_OAK_WOOD", "-"));
	public static final Mat STRIPPED_JUNGLE_LOG = registerValue(new Mat(VERSION, "STRIPPED_JUNGLE_LOG", "-"));
	public static final Mat STRIPPED_JUNGLE_WOOD = registerValue(new Mat(VERSION, "STRIPPED_JUNGLE_WOOD", "-"));
	public static final Mat STRIPPED_OAK_LOG = registerValue(new Mat(VERSION, "STRIPPED_OAK_LOG", "-"));
	public static final Mat STRIPPED_OAK_WOOD = registerValue(new Mat(VERSION, "STRIPPED_OAK_WOOD", "-"));
	public static final Mat STRIPPED_SPRUCE_LOG = registerValue(new Mat(VERSION, "STRIPPED_SPRUCE_LOG", "-"));
	public static final Mat STRIPPED_SPRUCE_WOOD = registerValue(new Mat(VERSION, "STRIPPED_SPRUCE_WOOD", "-"));
	public static final Mat STRUCTURE_BLOCK = registerValue(new Mat(VERSION, "STRUCTURE_BLOCK", "STRUCTURE_BLOCK"));
	public static final Mat STRUCTURE_VOID = registerValue(new Mat(VERSION, "STRUCTURE_VOID", "STRUCTURE_VOID"));
	public static final Mat SUGAR = registerValue(new Mat(VERSION, "SUGAR", "SUGAR"));
	public static final Mat SUGAR_CANE = registerValue(new Mat(VERSION, "SUGAR_CANE", "SUGAR_CANE"));
	public static final Mat SUGAR_CANE_BLOCK = registerValue(new Mat(VERSION, "SUGAR_CANE_BLOCK", "SUGAR_CANE_BLOCK"));
	public static final Mat SUNFLOWER = registerValue(new Mat(VERSION, "SUNFLOWER", "DOUBLE_PLANT"));
	public static final Mat TALL_GRASS = registerValue(new Mat(VERSION, "TALL_GRASS", "DOUBLE_PLANT", 2));
	public static final Mat TALL_SEAGRASS = registerValue(new Mat(VERSION, "TALL_SEAGRASS", "-"));
	public static final Mat TERRACOTTA = registerValue(new Mat(VERSION, "TERRACOTTA", "HARD_CLAY"));
	public static final Mat TIPPED_ARROW = registerValue(new Mat(VERSION, "TIPPED_ARROW", "TIPPED_ARROW"));
	public static final Mat TNT = registerValue(new Mat(VERSION, "TNT", "TNT"));
	public static final Mat TNT_MINECART = registerValue(new Mat(VERSION, "TNT_MINECART", "EXPLOSIVE_MINECART"));
	public static final Mat TORCH = registerValue(new Mat(VERSION, "TORCH", "TORCH"));
	public static final Mat TOTEM_OF_UNDYING = registerValue(new Mat(VERSION, "TOTEM_OF_UNDYING", "TOTEM"));
	public static final Mat TRAPPED_CHEST = registerValue(new Mat(VERSION, "TRAPPED_CHEST", "TRAPPED_CHEST"));
	public static final Mat TRIDENT = registerValue(new Mat(VERSION, "TRIDENT", "-"));
	public static final Mat TRIPWIRE = registerValue(new Mat(VERSION, "TRIPWIRE", "TRIPWIRE"));
	public static final Mat TRIPWIRE_HOOK = registerValue(new Mat(VERSION, "TRIPWIRE_HOOK", "TRIPWIRE_HOOK"));
	public static final Mat TROPICAL_FISH = registerValue(new Mat(VERSION, "TROPICAL_FISH", "RAW_FISH"));
	public static final Mat TROPICAL_FISH_BUCKET = registerValue(new Mat(VERSION, "TROPICAL_FISH_BUCKET", "BUCKET"));
	public static final Mat TROPICAL_FISH_SPAWN_EGG = registerValue(new Mat(VERSION, "TROPICAL_FISH_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat TUBE_CORAL = registerValue(new Mat(VERSION, "TUBE_CORAL", "-"));
	public static final Mat TUBE_CORAL_BLOCK = registerValue(new Mat(VERSION, "TUBE_CORAL_BLOCK", "-"));
	public static final Mat TUBE_CORAL_FAN = registerValue(new Mat(VERSION, "TUBE_CORAL_FAN", "-"));
	public static final Mat TUBE_CORAL_WALL_FAN = registerValue(new Mat(VERSION, "TUBE_CORAL_WALL_FAN", "-"));
	public static final Mat TURTLE_EGG = registerValue(new Mat(VERSION, "TURTLE_EGG", "MONSTER_EGG"));
	public static final Mat TURTLE_HELMET = registerValue(new Mat(VERSION, "TURTLE_HELMET", "-"));
	public static final Mat TURTLE_SPAWN_EGG = registerValue(new Mat(VERSION, "TURTLE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat VEX_SPAWN_EGG = registerValue(new Mat(VERSION, "VEX_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat VILLAGER_SPAWN_EGG = registerValue(new Mat(VERSION, "VILLAGER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat VINDICATOR_SPAWN_EGG = registerValue(new Mat(VERSION, "VINDICATOR_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat VINE = registerValue(new Mat(VERSION, "VINE", "VINE"));
	public static final Mat VOID_AIR = registerValue(new Mat(VERSION, "VOID_AIR", "AIR"));
	public static final Mat WALL_SIGN = registerValue(new Mat(VERSION, "WALL_SIGN", "WALL_SIGN"));
	public static final Mat WALL_TORCH = registerValue(new Mat(VERSION, "WALL_TORCH", "TORCH", 1));
	public static final Mat WATER = registerValue(new Mat(VERSION, "WATER", "WATER"));
	public static final Mat STATIONARY_WATER = registerValue(new Mat(VERSION, "WATER", "STATIONARY_WATER"));
	public static final Mat WATER_BUCKET = registerValue(new Mat(VERSION, "WATER_BUCKET", "WATER_BUCKET"));
	public static final Mat WET_SPONGE = registerValue(new Mat(VERSION, "WET_SPONGE", "SPONGE", 1));
	public static final Mat CROPS = registerValue(new Mat(VERSION, "CROPS", "-"));
	public static final Mat WHEAT = registerValue(new Mat(VERSION, "WHEAT", "WHEAT"));
	public static final Mat WHEAT_SEEDS = registerValue(new Mat(VERSION, "WHEAT_SEEDS", "SEEDS"));
	public static final Mat WHITE_BANNER = registerValue(new Mat(VERSION, "WHITE_BANNER", "BANNER", 15));
	public static final Mat WHITE_BED = registerValue(new Mat(VERSION, "WHITE_BED", "BED"));
	public static final Mat WHITE_CARPET = registerValue(new Mat(VERSION, "WHITE_CARPET", "CARPET"));
	public static final Mat WHITE_CONCRETE = registerValue(new Mat(VERSION, "WHITE_CONCRETE", "STAINED_CLAY"));
	public static final Mat WHITE_CONCRETE_POWDER = registerValue(new Mat(VERSION, "WHITE_CONCRETE_POWDER", "CONCRETE_POWDER"));
	public static final Mat WHITE_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "WHITE_GLAZED_TERRACOTTA", "WHITE_GLAZED_TERRACOTTA"));
	public static final Mat WHITE_SHULKER_BOX = registerValue(new Mat(VERSION, "WHITE_SHULKER_BOX", "WHITE_SHULKER_BOX"));
	public static final Mat WHITE_STAINED_GLASS = registerValue(new Mat(VERSION, "WHITE_STAINED_GLASS", "STAINED_GLASS"));
	public static final Mat WHITE_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE"));
	public static final Mat WHITE_TERRACOTTA = registerValue(new Mat(VERSION, "WHITE_TERRACOTTA", "TERRACOTTA"));
	public static final Mat WHITE_TULIP = registerValue(new Mat(VERSION, "WHITE_TULIP", "RED_ROSE", 6));
	public static final Mat WHITE_WALL_BANNER = registerValue(new Mat(VERSION, "WHITE_WALL_BANNER", "WALL_BANNER"));
	public static final Mat WHITE_WOOL = registerValue(new Mat(VERSION, "WHITE_WOOL", "WOOL"));
	public static final Mat WITCH_SPAWN_EGG = registerValue(new Mat(VERSION, "WITCH_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat WITHER_SKELETON_SKULL = registerValue(new Mat(VERSION, "WITHER_SKELETON_SKULL", "SKULL_ITEM", 1));
	public static final Mat WITHER_SKELETON_SPAWN_EGG = registerValue(new Mat(VERSION, "WITHER_SKELETON_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat WITHER_SKELETON_WALL_SKULL = registerValue(new Mat(VERSION, "WITHER_SKELETON_WALL_SKULL", "SKULL_ITEM", 1));
	public static final Mat WOLF_SPAWN_EGG = registerValue(new Mat(VERSION, "WOLF_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat WOODEN_AXE = registerValue(new Mat(VERSION, "WOODEN_AXE", "WOOD_AXE"));
	public static final Mat WOODEN_HOE = registerValue(new Mat(VERSION, "WOODEN_HOE", "WOOD_HOE"));
	public static final Mat WOODEN_PICKAXE = registerValue(new Mat(VERSION, "WOODEN_PICKAXE", "WOOD_PICKAXE"));
	public static final Mat WOODEN_SHOVEL = registerValue(new Mat(VERSION, "WOODEN_SHOVEL", "WOOD_SPADE"));
	public static final Mat WOODEN_SWORD = registerValue(new Mat(VERSION, "WOODEN_SWORD", "WOOD_SWORD"));
	public static final Mat WRITABLE_BOOK = registerValue(new Mat(VERSION, "WRITABLE_BOOK", "BOOK_AND_QUILL"));
	public static final Mat WRITTEN_BOOK = registerValue(new Mat(VERSION, "WRITTEN_BOOK", "WRITTEN_BOOK"));
	public static final Mat YELLOW_BANNER = registerValue(new Mat(VERSION, "YELLOW_BANNER", "BANNER", 11));
	public static final Mat YELLOW_BED = registerValue(new Mat(VERSION, "YELLOW_BED", "BED", 4));
	public static final Mat YELLOW_CARPET = registerValue(new Mat(VERSION, "YELLOW_CARPET", "CARPET", 4));
	public static final Mat YELLOW_CONCRETE = registerValue(new Mat(VERSION, "YELLOW_CONCRETE", "STAINED_CLAY", 4));
	public static final Mat YELLOW_CONCRETE_POWDER = registerValue(new Mat(VERSION, "YELLOW_CONCRETE_POWDER", "CONCRETE_POWDER", 4));
	public static final Mat YELLOW_GLAZED_TERRACOTTA = registerValue(new Mat(VERSION, "YELLOW_GLAZED_TERRACOTTA", "YELLOW_GLAZED_TERRACOTTA"));
	public static final Mat YELLOW_SHULKER_BOX = registerValue(new Mat(VERSION, "YELLOW_SHULKER_BOX", "YELLOW_SHULKER_BOX"));
	public static final Mat YELLOW_STAINED_GLASS = registerValue(new Mat(VERSION, "YELLOW_STAINED_GLASS", "STAINED_GLASS", 4));
	public static final Mat YELLOW_STAINED_GLASS_PANE = registerValue(new Mat(VERSION, "YELLOW_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 4));
	public static final Mat YELLOW_TERRACOTTA = registerValue(new Mat(VERSION, "YELLOW_TERRACOTTA", "STAINED_CLAY", 4));
	public static final Mat YELLOW_WALL_BANNER = registerValue(new Mat(VERSION, "YELLOW_WALL_BANNER", "WALL_BANNER"));
	public static final Mat YELLOW_WOOL = registerValue(new Mat(VERSION, "YELLOW_WOOL", "WOOL", 4));
	public static final Mat ZOMBIE_HEAD = registerValue(new Mat(VERSION, "ZOMBIE_HEAD", "SKULL_ITEM", 2));
	public static final Mat ZOMBIE_HORSE_SPAWN_EGG = registerValue(new Mat(VERSION, "ZOMBIE_HORSE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ZOMBIE_PIGMAN_SPAWN_EGG = registerValue(new Mat(VERSION, "ZOMBIE_PIGMAN_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ZOMBIE_SPAWN_EGG = registerValue(new Mat(VERSION, "ZOMBIE_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ZOMBIE_VILLAGER_SPAWN_EGG = registerValue(new Mat(VERSION, "ZOMBIE_VILLAGER_SPAWN_EGG", "MONSTER_EGG"));
	public static final Mat ZOMBIE_WALL_HEAD = registerValue(new Mat(VERSION, "ZOMBIE_WALL_HEAD", "SKULL_ITEM", 2));
	// 1.14
	public static final Mat BAMBOO = registerValue(new Mat(VERSION, "BAMBOO", "-"));
	public static final Mat BAMBOO_SAPLING = registerValue(new Mat(VERSION, "BAMBOO_SAPLING", "-"));
	public static final Mat JIGSAW = registerValue(new Mat(VERSION, "JIGSAW", "-"));
	public static final Mat SWEET_BERRIES = registerValue(new Mat(VERSION, "SWEET_BERRIES", "-"));
	public static final Mat SWEET_BERRY_BUSH = registerValue(new Mat(VERSION, "SWEET_BERRY_BUSH", "-"));
	public static final Mat BELL = registerValue(new Mat(VERSION, "BELL", "-"));
	public static final Mat COMPOSTER = registerValue(new Mat(VERSION, "COMPOSTER", "-"));
	public static final Mat ANDESITE_SLAB = registerValue(new Mat(VERSION, "ANDESITE_SLAB", "-"));
	public static final Mat CUT_RED_SANDSTONE_SLAB = registerValue(new Mat(VERSION, "CUT_RED_SANDSTONE_SLAB", "-"));
	public static final Mat CUT_SANDSTONE_SLAB = registerValue(new Mat(VERSION, "CUT_SANDSTONE_SLAB", "-"));
	public static final Mat DIORITE_SLAB = registerValue(new Mat(VERSION, "DIORITE_SLAB", "-"));
	public static final Mat END_STONE_BRICK_SLAB = registerValue(new Mat(VERSION, "END_STONE_BRICK_SLAB", "-"));
	public static final Mat GRANITE_SLAB = registerValue(new Mat(VERSION, "GRANITE_SLAB", "-"));
	public static final Mat MOSSY_COBBLESTONE_SLAB = registerValue(new Mat(VERSION, "MOSSY_COBBLESTONE_SLAB", "-"));
	public static final Mat MOSSY_STONE_BRICK_SLAB = registerValue(new Mat(VERSION, "MOSSY_STONE_BRICK_SLAB", "-"));
	public static final Mat POLISHED_ANDESITE_SLAB = registerValue(new Mat(VERSION, "POLISHED_ANDESITE_SLAB", "-"));
	public static final Mat POLISHED_DIORITE_SLAB = registerValue(new Mat(VERSION, "POLISHED_DIORITE_SLAB", "-"));
	public static final Mat POLISHED_GRANITE_SLAB = registerValue(new Mat(VERSION, "POLISHED_GRANITE_SLAB", "-"));
	public static final Mat RED_NETHER_BRICK = registerValue(new Mat(VERSION, "RED_NETHER_BRICK", "-"));
	public static final Mat SMOOTH_QUARTZ_SLAB = registerValue(new Mat(VERSION, "SMOOTH_QUARTZ_SLAB", "-"));
	public static final Mat SMOOTH_RED_SANDSTONE_SLAB = registerValue(new Mat(VERSION, "SMOOTH_RED_SANDSTONE_SLAB", "-"));
	public static final Mat SMOOTH_SANDSTONE_SLAB = registerValue(new Mat(VERSION, "SMOOTH_SANDSTONE_SLAB", "-"));
	public static final Mat SMOOTH_STONE_SLAB = registerValue(new Mat(VERSION, "SMOOTH_STONE_SLAB", "-"));
	public static final Mat SCAFFOLDING = registerValue(new Mat(VERSION, "SCAFFOLDING", "-"));
	public static final Mat ANDESITE_STAIRS = registerValue(new Mat(VERSION, "ANDESITE_STAIRS", "-"));
	public static final Mat DIORITE_STAIRS = registerValue(new Mat(VERSION, "DIORITE_STAIRS", "-"));
	public static final Mat END_STONE_BRICK_STAIRS = registerValue(new Mat(VERSION, "END_STONE_BRICK_STAIRS", "-"));
	public static final Mat GRANITE_STAIRS = registerValue(new Mat(VERSION, "GRANITE_STAIRS", "-"));
	public static final Mat MOSSY_COBBLESTONE_STAIRS = registerValue(new Mat(VERSION, "MOSSY_COBBLESTONE_STAIRS", "-"));
	public static final Mat MOSSY_STONE_BRICK_STAIRS = registerValue(new Mat(VERSION, "MOSSY_STONE_BRICK_STAIRS", "-"));
	public static final Mat POLISHED_ANDESITE_STAIRS = registerValue(new Mat(VERSION, "POLISHED_ANDESITE_STAIRS", "-"));
	public static final Mat POLISHED_DIORITE_STAIRS = registerValue(new Mat(VERSION, "POLISHED_DIORITE_STAIRS", "-"));
	public static final Mat POLISHED_GRANITE_STAIRS = registerValue(new Mat(VERSION, "POLISHED_GRANITE_STAIRS", "-"));
	public static final Mat RED_NETHER_BRICK_STAIRS = registerValue(new Mat(VERSION, "RED_NETHER_BRICK_STAIRS", "-"));
	public static final Mat SMOOTH_QUARTZ_STAIRS = registerValue(new Mat(VERSION, "SMOOTH_QUARTZ_STAIRS", "-"));
	public static final Mat SMOOTH_RED_SANDSTONE_STAIRS = registerValue(new Mat(VERSION, "SMOOTH_RED_SANDSTONE_STAIRS", "-"));
	public static final Mat SMOOTH_SANDSTONE_STAIRS = registerValue(new Mat(VERSION, "SMOOTH_SANDSTONE_STAIRS", "-"));
	public static final Mat CAMPFIRE = registerValue(new Mat(VERSION, "CAMPFIRE", "-"));
	public static final Mat CORNFLOWER = registerValue(new Mat(VERSION, "CORNFLOWER", "-"));
	public static final Mat LILY_OF_THE_VALLEY = registerValue(new Mat(VERSION, "LILY_OF_THE_VALLEY", "-"));
	public static final Mat WITHER_ROSE = registerValue(new Mat(VERSION, "WITHER_ROSE", "WITHER_ROSE"));
	public static final Mat POTTED_WITHER_ROSE = registerValue(new Mat(VERSION, "POTTED_WITHER_ROSE", "-"));
	public static final Mat POTTER_CORNFLOWER = registerValue(new Mat(VERSION, "POTTER_CORNFLOWER", "-"));
	public static final Mat POTTER_LILY_OF_THE_VALLEY = registerValue(new Mat(VERSION, "POTTER_LILY_OF_THE_VALLEY", "-"));
	public static final Mat POTTER_POTTED_WITHER_ROSE = registerValue(new Mat(VERSION, "POTTER_POTTED_WITHER_ROSE", "-"));
	public static final Mat SMOKER = registerValue(new Mat(VERSION, "SMOKER", "-"));
	public static final Mat BLAST_FURNACE = registerValue(new Mat(VERSION, "BLAST_FURNACE", "-"));
	public static final Mat LANTERN = registerValue(new Mat(VERSION, "LANTERN", "-"));
	public static final Mat LOOM = registerValue(new Mat(VERSION, "LOOM", "-"));
	public static final Mat CREEPER_BANNER_PATTERN = registerValue(new Mat(VERSION, "CREEPER_BANNER_PATTERN", "-"));
	public static final Mat FLOWER_BANNER_PATTERN = registerValue(new Mat(VERSION, "FLOWER_BANNER_PATTERN", "-"));
	public static final Mat GLOBE_BANNER_PATTERN = registerValue(new Mat(VERSION, "GLOBE_BANNER_PATTERN", "-"));
	public static final Mat MOJANG_BANNER_PATTERN = registerValue(new Mat(VERSION, "MOJANG_BANNER_PATTERN", "-"));
	public static final Mat SKULL_BANNER_PATTERN = registerValue(new Mat(VERSION, "SKULL_BANNER_PATTERN", "-"));
	public static final Mat GRINDSTONE = registerValue(new Mat(VERSION, "GRINDSTONE", "-"));
	public static final Mat BRICK_WALL = registerValue(new Mat(VERSION, "BRICK_WALL", "-"));
	public static final Mat END_STONE_BRICK_WALL = registerValue(new Mat(VERSION, "END_STONE_BRICK_WALL", "-"));
	public static final Mat MOSSY_STONE_BRICK_WALL = registerValue(new Mat(VERSION, "MOSSY_STONE_BRICK_WALL", "-"));
	public static final Mat NETHER_BRICK_WALL = registerValue(new Mat(VERSION, "NETHER_BRICK_WALL", "-"));
	public static final Mat RED_NETHER_BRICK_WALL = registerValue(new Mat(VERSION, "RED_NETHER_BRICK_WALL", "-"));
	public static final Mat STONE_BRICK_WALL = registerValue(new Mat(VERSION, "STONE_BRICK_WALL", "-"));
	public static final Mat ANDESITE_WALL = registerValue(new Mat(VERSION, "ANDESITE_WALL", "-"));
	public static final Mat DIORITE_WALL = registerValue(new Mat(VERSION, "DIORITE_WALL", "-"));
	public static final Mat GRANITE_WALL = registerValue(new Mat(VERSION, "GRANITE_WALL", "-"));
	public static final Mat PRISMARINE_WALL = registerValue(new Mat(VERSION, "PRISMARINE_WALL", "-"));
	public static final Mat SANDSTONE_WALL = registerValue(new Mat(VERSION, "SANDSTONE_WALL", "-"));
	public static final Mat RED_SANDSTONE_WALL = registerValue(new Mat(VERSION, "RED_SANDSTONE_WALL", "-"));
	public static final Mat ACACIA_SIGN = registerValue(new Mat(VERSION, "ACACIA_SIGN", "-"));
	public static final Mat ACACIA_WALL_SIGN = registerValue(new Mat(VERSION, "ACACIA_WALL_SIGN", "-"));
	public static final Mat BIRCH_SIGN = registerValue(new Mat(VERSION, "BIRCH_SIGN", "-"));
	public static final Mat BIRCH_WALL_SIGN = registerValue(new Mat(VERSION, "BIRCH_WALL_SIGN", "-"));
	public static final Mat DARK_OAK_SIGN = registerValue(new Mat(VERSION, "DARK_OAK_SIGN", "-"));
	public static final Mat DARK_OAK_WALL_SIGN = registerValue(new Mat(VERSION, "DARK_OAK_WALL_SIGN", "-"));
	public static final Mat JUNGLE_SIGN = registerValue(new Mat(VERSION, "JUNGLE_SIGN", "-"));
	public static final Mat JUNGLE_WALL_SIGN = registerValue(new Mat(VERSION, "JUNGLE_WALL_SIGN", "-"));
	public static final Mat OAK_SIGN = registerValue(new Mat(VERSION, "OAK_SIGN", "-"));
	public static final Mat OAK_WALL_SIGN = registerValue(new Mat(VERSION, "OAK_WALL_SIGN", "-"));
	public static final Mat SPRUCE_SIGN = registerValue(new Mat(VERSION, "SPRUCE_SIGN", "-"));
	public static final Mat SPRUCE_WALL_SIGN = registerValue(new Mat(VERSION, "SPRUCE_WALL_SIGN", "-"));
	public static final Mat LECTERN = registerValue(new Mat(VERSION, "LECTERN", "-"));
	public static final Mat CARTOGRAPHY_TABLE = registerValue(new Mat(VERSION, "CARTOGRAPHY_TABLE", "-"));
	public static final Mat SMITHING_TABLE = registerValue(new Mat(VERSION, "SMITHING_TABLE", "-"));
	public static final Mat FLETCHING_TABLE = registerValue(new Mat(VERSION, "FLETCHING_TABLE", "-"));
	public static final Mat STONECUTTER = registerValue(new Mat(VERSION, "STONECUTTER", "-"));
	public static final Mat BARREL = registerValue(new Mat(VERSION, "BARREL", "-"));
	public static final Mat CROSSBOW = registerValue(new Mat(VERSION, "CROSSBOW", "-"));
	public static final Mat CAT_SPAWN_EGG = registerValue(new Mat(VERSION, "CAT_SPAWN_EGG", "-"));
	public static final Mat TRADER_LLAMA_SPAWN_EGG = registerValue(new Mat(VERSION, "TRADER_LLAMA_SPAWN_EGG", "-"));
	public static final Mat WANDERING_TRADER_SPAWN_EGG = registerValue(new Mat(VERSION, "WANDERING_TRADER_SPAWN_EGG", "-"));
	public static final Mat PANDA_SPAWN_EGG = registerValue(new Mat(VERSION, "PANDA_SPAWN_EGG", "-"));
	public static final Mat PILLAGER_SPAWN_EGG = registerValue(new Mat(VERSION, "PILLAGER_SPAWN_EGG", "-"));
	public static final Mat RAVAGER_SPAWN_EGG = registerValue(new Mat(VERSION, "RAVAGER_SPAWN_EGG", "-"));
	public static final Mat FOX_SPAWN_EGG = registerValue(new Mat(VERSION, "FOX_SPAWN_EGG", "-"));
	public static final Mat SUSPICIOUS_STEW = registerValue(new Mat(VERSION, "SUSPICIOUS_STEW", "-"));
	public static final Mat BLACK_DYE = registerValue(new Mat(VERSION, "BLACK_DYE", "-"));
	public static final Mat WHITE_DYE = registerValue(new Mat(VERSION, "WHITE_DYE", "-"));
	public static final Mat BLUE_DYE = registerValue(new Mat(VERSION, "BLUE_DYE", "-"));
	public static final Mat BROWN_DYE = registerValue(new Mat(VERSION, "BROWN_DYE", "-"));
	public static final Mat RED_DYE = registerValue(new Mat(VERSION, "RED_ROSE", "RED_DYE"));
	// forgotten blocks or legacy blocks
	public static final Mat LEGACY_DOUBLE_STEP_0 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP"));
	public static final Mat LEGACY_DOUBLE_STEP_1 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 1));
	public static final Mat LEGACY_DOUBLE_STEP_2 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 2));
	public static final Mat LEGACY_DOUBLE_STEP_3 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 3));
	public static final Mat LEGACY_DOUBLE_STEP_4 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 4));
	public static final Mat LEGACY_DOUBLE_STEP_5 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 5));
	public static final Mat LEGACY_DOUBLE_STEP_6 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 6));
	public static final Mat LEGACY_DOUBLE_STEP_7 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 7));
	public static final Mat LEGACY_DOUBLE_STEP_8 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 8));
	public static final Mat LEGACY_DOUBLE_STEP_9 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 9));
	public static final Mat LEGACY_DOUBLE_STEP_10 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 10));
	public static final Mat LEGACY_DOUBLE_STEP_11 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 11));
	public static final Mat LEGACY_DOUBLE_STEP_12 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 12));
	public static final Mat LEGACY_DOUBLE_STEP_13 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 13));
	public static final Mat LEGACY_DOUBLE_STEP_14 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 14));
	public static final Mat LEGACY_DOUBLE_STEP_15 = registerValue(new Mat(VERSION, "-", "DOUBLE_STEP", 15));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_0 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP"));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_1 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 1));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_2 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 2));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_3 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 3));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_4 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 4));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_5 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 5));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_6 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 6));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_7 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 7));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_8 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 8));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_9 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 9));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_10 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 10));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_11 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 11));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_12 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 12));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_13 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 13));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_14 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 14));
	public static final Mat LEGACY_WOOD_DOUBLE_STEP_15 = registerValue(new Mat(VERSION, "-", "WOOD_DOUBLE_STEP", 15));
	public static final Mat LEGACY_STANDING_BANNER = registerValue(new Mat(VERSION, "-", "STANDING_BANNER"));
	public static final Mat LEGACY_BED_BLOCK = registerValue(new Mat(VERSION, "-", "BED_BLOCK"));
	public static final Mat LEGACY_IRON_DOOR_BLOCK = registerValue(new Mat(VERSION, "-", "IRON_DOOR_BLOCK"));
	public static final Mat LEGACY_SKULL_0 = registerValue(new Mat(VERSION, "-", "SKULL"));
	public static final Mat LEGACY_SKULL_1 = registerValue(new Mat(VERSION, "-", "SKULL", 1));
	public static final Mat LEGACY_SKULL_2 = registerValue(new Mat(VERSION, "-", "SKULL", 2));
	public static final Mat LEGACY_SKULL_3 = registerValue(new Mat(VERSION, "-", "SKULL", 3));
	public static final Mat LEGACY_SKULL_4 = registerValue(new Mat(VERSION, "-", "SKULL", 4));
	public static final Mat LEGACY_SKULL_5 = registerValue(new Mat(VERSION, "-", "SKULL", 5));

	// base
	private MatVersion version;
	private final String modernName;
	private final String legacyName;
	private final int legacyData;
	private int durability;
	private final Material currentMaterial;

	private Mat(MatVersion version, String modernName, String legacyName) {
		this(version, modernName, legacyName, 0);
	}

	private Mat(MatVersion version, String modernName, String legacyName, int legacyData) {
		this(version, modernName, legacyName, legacyData, 0);
	}

	private Mat(MatVersion version, String modernName, String legacyName, int legacyData, int durability) {
		this.version = version;
		this.modernName = modernName;
		this.legacyName = legacyName;
		this.legacyData = legacyData;
		this.durability = durability;
		this.currentMaterial = ServerVersion.IS_1_13 ? Material.getMaterial(modernName) : (!legacyName.equals("-") ? Material.getMaterial(legacyName) : null);
	}

	// get
	public MatVersion getVersion() {
		return version;
	}

	public String getModernName() {
		return modernName;
	}

	public String getLegacyName() {
		return legacyName;
	}

	public int getLegacyData() {
		return legacyData;
	}

	public int getDurability() {
		return durability;
	}

	public Material getCurrentMaterial() {
		return currentMaterial;
	}

	public boolean exists() {
		return currentMaterial == null ? false : (currentMaterial.equals(Material.AIR) ? isAir() : true);
	}

	public boolean isAir() {
		return modernName.contains("AIR");
	}

	public boolean isSkull() {
		return modernName.contains("HEAD") || modernName.contains("SKULL");
	}

	// overriden methods
	@Override
	public String toString() {
		return modernName;
	}

	@Override
	public boolean equals(Object obj) {
		return Utils.instanceOf(obj, Mat.class) ? equals((Mat) obj, true) : false;
	}

	public boolean equals(Mat other, boolean durabilityCheck) {
		return this.modernName.equals(other.modernName) && (durabilityCheck ? this.durability == other.durability : true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + durability;
		result = prime * result + ((modernName == null) ? 0 : modernName.hashCode());
		return result;
	}

	@Override
	public Mat clone() {
		return new Mat(VERSION, modernName, legacyName, legacyData, durability);
	}

	@Override
	public int compareTo(Mat o) {
		return o != null ? String.CASE_INSENSITIVE_ORDER.compare(modernName, o.modernName) : 1;
	}

	// methods
	public boolean isMat(ItemStack item) {
		return item == null ? isAir() : isMat(item.getType(), item.getData().getData(), item.getDurability());
	}

	public boolean isMat(Block block) {
		return block == null ? isAir() : isMat(block.getType(), (int) block.getData(), 0);
	}

	public boolean isMat(Material material) {
		return isMat(material, 0, 0);
	}

	public boolean isMat(Material material, int data, int durability) {
		Mat mat = data > 0 ? from(material, data, durability) : from(material, durability);
		return equals(mat);
	}

	// item methods
	public ItemStack getNewCurrentStack() {
		// null material
		if (!exists()) {
			GCore.inst().error("Mat " + modernName + " doesn't exist. Returning new AIR item stack.");
			return new ItemStack(AIR.getCurrentMaterial());
		}
		// modern
		if (ServerVersion.IS_1_13 || legacyData == 0) {
			return new ItemStack(currentMaterial, 1, (short) durability);
		}
		// legacy
		else {
			return new ItemStack(currentMaterial, 1, (short) durability, (byte) legacyData);
		}
	}

	// block methods
	public void setBlock(Block block) {
		// null material
		if (!exists()) {
			GCore.inst().error("Mat " + modernName + " doesn't exist. Couldn't set block.");
			return;
		}
		// modern
		if (version.isModern()) {
			block.setType(getCurrentMaterial());
		}
		// legacy
		else {
			try {
				Block.class.getMethod("setTypeIdAndData", int.class, byte.class, boolean.class)
				.invoke(block, (int) Material.class.getMethod("getId").invoke(currentMaterial), legacyData > 0 ? (byte) legacyData : (byte) 0, false);
			} catch (Throwable exception) {
				exception.printStackTrace();
				GCore.inst().error("Trying to set block with mat " + toString() + " but an unknown error occured");
				return;
			}
		}
	}

	public void setBlockChange(Block block, Collection<? extends Player> players) {
		// null material
		if (!exists()) {
			GCore.inst().error("Mat " + modernName + " doesn't exist. Couldn't set block change.");
			return;
		}
		// players
		for (Player player : players) {
			player.sendBlockChange(block.getLocation(), getCurrentMaterial(), (byte) (version.isModern() ? 0 : legacyData));
		}
	}

	// static methods
	private static Mat registerValue(Mat mat) {
		// regular
		REGULAR_VALUES.put(mat.modernName, mat);
		// modern
		if (!MODERN.containsKey(mat.modernName)) MODERN.put(mat.modernName, new MatDurabilities());
		MODERN.get(mat.modernName).add(mat);
		// legacy
		if (!LEGACY.containsKey(mat.legacyName)) LEGACY.put(mat.legacyName, new MatDatas());
		LEGACY.get(mat.legacyName).add(mat);
		// return
		return mat;
	}

	public static Collection<Mat> values() {
		return REGULAR_VALUES.values();
	}

	public static Mat valueOf(String name) {
		return REGULAR_VALUES.get(name);
	}

	public static Mat from(String modernName, int durability) {
		// null
		if (modernName == null) {
			return AIR;
		}
		// modern
		if (MODERN.containsKey(modernName)) {
			MatDurabilities durabilities = MODERN.get(modernName);
			// doesn't have one with the same durability, so create it
			if (durability > 0 && !durabilities.hasDurability(durability)) {
				Mat mat = durabilities.withDefaultDurability().clone();
				mat.durability = durability;
				durabilities.add(mat);
			}
			// return durability
			return durabilities.withDurabilityOrDefault(durability);
		}
		// not modern, maybe legacy ?
		if (LEGACY.containsKey(modernName)) {
			MatDatas datas = LEGACY.get(modernName);
			MatDurabilities durabilities = datas.withDefaultData();
			// doesn't have one with the same durability, so create it
			if (durability > 0 && !durabilities.hasDurability(durability)) {
				Mat mat = durabilities.withDefaultDurability().clone();
				mat.durability = durability;
				durabilities.add(mat);
			}
			// return default durability
			return durabilities.withDurabilityOrDefault(durability);
		}
		// allow custom mats
		if (GCore.inst().allowCustomMaterials()) {
			return new Mat(MatVersion.MODERN_CUSTOM, modernName, modernName, 0, durability);
		}
		// couldn't find any
		GCore.inst().debug("[mat, modern] Trying to get Mat from " + modernName + " (durability " + durability + ") but this is not a material recognised by GCore. Please use the correct GCore Mat name. Returning AIR.");
		return AIR;
	}

	public static Mat from(String legacyName, int legacyData, int durability) {
		// null
		if (legacyName == null) {
			return AIR;
		}
		// legacy
		if (LEGACY.containsKey(legacyName)) {
			MatDatas datas = LEGACY.get(legacyName);
			MatDurabilities durabilities = legacyData > 0 && datas.hasData(legacyData) ? datas.withData(legacyData) : datas.withDefaultData();
			// doesn't have one with the same durability, so create it
			if (durability > 0 && !durabilities.hasDurability(durability)) {
				Mat mat = durabilities.withDefaultDurability().clone();
				mat.durability = durability;
				durabilities.add(mat);
			}
			// return durability
			return durabilities.withDurabilityOrDefault(durability);
		}
		// not legacy, maybe modern ?
		if (MODERN.containsKey(legacyName)) {
			MatDurabilities durabilities = MODERN.get(legacyName);
			// doesn't have one with the same durability, so create it
			if (durability > 0 && !durabilities.hasDurability(durability)) {
				Mat mat = durabilities.withDefaultDurability().clone();
				mat.durability = durability;
				durabilities.add(mat);
			}
			// return durability
			return durabilities.withDurabilityOrDefault(durability);
		}
		// allow custom mats
		if (GCore.inst().allowCustomMaterials()) {
			return new Mat(MatVersion.LEGACY_CUSTOM, legacyName, legacyName, legacyData, durability);
		}
		// couldn't find any
		GCore.inst().debug("[mat, legacy] Trying to get Mat from " + legacyName + " (data " + legacyData + ", durability " + durability + ") but this is not a material recognised by GCore. Please use the correct GCore Mat name. Returning AIR.");
		return AIR;
	}

	public static Mat from(Material material) {
		return material == null ? AIR : (ServerVersion.IS_1_13 ? from(material, 0) : from(material, 0, 0));
	}

	public static Mat from(Material material, int durability) {
		// null
		if (material == null) {
			return AIR;
		}
		// name()
		try {
			Mat mat = from(material.name(), durability);
			if (mat != null) return mat;
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("[mat] Trying to get Material from " + material + "(durability " + durability + ") but an unknown error occured. Returning AIR. (material, name)");
			return AIR;
		}
		// toString()
		try {
			Mat mat = from(material.toString(), durability);
			if (mat != null) return mat;
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("[mat] Trying to get Material from " + material + "(durability " + durability + ") but an unknown error occured. Returning AIR. (material, toString)");
			return AIR;
		}
		// allow custom mats
		if (GCore.inst().allowCustomMaterials()) {
			try {
				return new Mat(MatVersion.LEGACY_CUSTOM, material.name(), material.toString(), 0, durability);
			} catch (Throwable exception) {
				return new Mat(MatVersion.LEGACY_CUSTOM, material.toString(), material.toString(), 0, durability);
			}
		}
		// unknown
		GCore.inst().error("[mat] Trying to get Material from " + material + "(durability " + durability + ") but couldn't find any. Returning AIR. (material, unknown)");
		return AIR;
	}

	public static Mat from(Material legacyMaterial, int legacyData, int durability) {
		// null
		if (legacyMaterial == null) {
			return AIR;
		}
		// name()
		Mat mat = from(legacyMaterial.name(), legacyData, durability);
		if (mat != null) return mat;
		// toString()
		mat = from(legacyMaterial.toString(), legacyData, durability);
		if (mat != null) return mat;
		// allow custom mats
		if (GCore.inst().allowCustomMaterials()) {
			try {
				return new Mat(MatVersion.LEGACY_CUSTOM, legacyMaterial.name(), legacyMaterial.toString(), legacyData, durability);
			} catch (Throwable exception) {
				return new Mat(MatVersion.LEGACY_CUSTOM, legacyMaterial.toString(), legacyMaterial.toString(), legacyData, durability);
			}
		}
		// unknown
		GCore.inst().error("[mat] Trying to get Material from " + legacyMaterial + "(data " + legacyData + ", durability " + durability + ") but couldn't find any. Returning AIR. (material, unknown)");
		return AIR;
	}

	public static Mat from(Block raw) {
		return raw == null ? AIR : (ServerVersion.IS_1_13 || raw.getData() == (byte) 0 ? from(raw.getType(), 0) : from(raw.getType(), (int) raw.getData(), 0));
	}

	public static Mat from(ItemStack raw) {
		return raw == null ? AIR : (ServerVersion.IS_1_13 || raw.getData().getData() == (byte) 0 ? from(raw.getType(), (int) raw.getDurability()) : from(raw.getType(), (int) raw.getData().getData(), (int) raw.getDurability()));
	}

	// mat version
	public static enum MatVersion {

		MODERN,
		MODERN_CUSTOM,
		LEGACY,
		LEGACY_CUSTOM;

		private boolean legacy;

		private MatVersion() {
			this.legacy = name().contains("LEGACY");
		}

		public boolean isModern() {
			return !legacy;
		}

		public boolean isLegacy() {
			return legacy;
		}

	}

}
