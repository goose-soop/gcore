package com.guillaumevdn.gcore.lib.parseable.placeholder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.Perm;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.versioncompat.Compat;

public abstract class PlaceholderParser implements Comparable<PlaceholderParser> {

	// base
	private int priority;
	private List<String> description;

	/** @param priority priority for parsing */
	public PlaceholderParser(int priority, List<String> description) {
		this.priority = priority;
		this.description = description;
	}

	// abstract
	/**
	 * Parse the placeholder
	 * @param player the player (a null check should be made)
	 * @param placeholderContent the placeholder content, without begin and end chars
	 * @return a value, or null if the placeholder isn't handled by this method
	 */
	public abstract String parse(Player player, String raw);

	// get
	public int getPriority() {
		return priority;
	}

	public List<String> getDescription() {
		return description;
	}

	// overriden
	@Override
	public int compareTo(PlaceholderParser o) {
		return Integer.compare(priority, o.priority);
	}

	// static base
	private static Map<String, PlaceholderParser> registered = new HashMap<String, PlaceholderParser>();

	public static Map<String, PlaceholderParser> getRegistered() {
		return registered;
	}

	public static void register(String id, PlaceholderParser parser) {
		registered.put(id, parser);
	}

	public static void unregister(String id) {
		registered.remove(id);
	}

	static {
		// player weapon damage
		register("player_weapon_damage", new SimplePlaceholderParser(0, '{', '}', Utils.asList("§7Player weapon dmg : §8{player_weapon_damage}")) {
			@Override
			public String parsePlaceholders(Player player, String placeholderContent) {
				try {
					if (placeholderContent.startsWith("player_weapon_damage")) {
						double damage = 1d;
						try {
							ItemStack item = player.getItemInHand();
							damage = Compat.INSTANCE.getAttackDamage(item == null ? Mat.AIR.getNewCurrentStack() : item);
						} catch (Throwable ignored) {}
						return new BigDecimal(Double.toString(damage)).toPlainString();
					}
				} catch (Throwable ignored) {}
				return null;
			}
		});
		// trienal condition parser ; the perm or values might depend on other placeholders so parse it in the end
		register("trienal_conditions", new SimplePlaceholderParser(997, '{', '}', Utils.asList("§7Condition : §8{permission:PERM,IF_HAS,IF_HASNT}")) {
			@Override
			public String parsePlaceholders(Player player, String placeholderContent) {
				try {
					if (placeholderContent.startsWith("permission:")) {
						String[] split = placeholderContent.substring("permission:".length()).split(",");
						if (new Perm(null, split[0], false).has(player)) {
							return split[1];
						} else {
							return split[2];
						}
					}
				} catch (Throwable ignored) {}
				return null;
			}
		});
		// PlaceholderAPI parser ; parse it after the conditions, just before the math
		register("placeholder_api", new PlaceholderParser(998, Utils.asList("§7PlaceholderAPI placeholders : §8%placeholder%")) {
			@Override
			public String parse(Player player, String raw) {
				return player != null ? Utils.fillPlaceholderAPI(player, raw) : raw;
			}
		});
		// math parsers
		register("random_number", new SimplePlaceholderParser(999, '{', '}', Utils.asList("§7Random number : §8{random_number:MIN,MAX}")) {
			@Override
			public String parsePlaceholders(Player player, String placeholderContent) {
				try {
					if (placeholderContent.startsWith("random_number:")) {
						String[] split = placeholderContent.substring("random_number:".length()).split(",");
						return new BigDecimal(String.valueOf(Utils.randomDouble(Double.parseDouble(split[0]), Double.parseDouble(split[1])))).toPlainString();
					}
				} catch (Throwable ignored) {}
				return null;
			}
		});
		register("math", new SimplePlaceholderParser(1000, '{', '}', Utils.asList("§7Math expression : §8{math:EXPRESSION}")) {
			@Override
			public String parsePlaceholders(Player player, String placeholderContent) {
				if (placeholderContent.startsWith("math:")) {
					try {
						return Utils.round(Utils.calculateExpression(placeholderContent.substring("math:".length())));
					} catch (Throwable ignored) {}
					return "{invalid_math_expression}";
				}
				return null;
			}
		});
	}

	// static methods
	public static String parseAll(Player player, String raw) {
		if (raw == null) return null;
		for (PlaceholderParser parser : Utils.asSortedList(registered.values())) {
			try {
				raw = parser.parse(player, raw);
			} catch (Throwable ignored) {}
		}
		return raw;
	}

	public static List<String> describeAll() {
		List<String> desc = Utils.emptyList();
		for (PlaceholderParser parser : Utils.asSortedList(registered.values())) {
			desc.addAll(parser.getDescription());
		}
		return desc;
	}

}
