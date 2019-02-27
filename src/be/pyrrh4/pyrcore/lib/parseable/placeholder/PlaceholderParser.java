package be.pyrrh4.pyrcore.lib.parseable.placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.Perm;
import be.pyrrh4.pyrcore.lib.util.Utils;

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
	protected abstract String parse(Player player, String raw);

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
		// Trienal condition parser
		register("trienal_conditions", new SimplePlaceholderParser(0, '{', '}', Utils.asList("§7Condition : §8{permission:PERM,IF_HAS,IF_HASNT}")) {
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
		// PlaceholderAPI parser
		register("placeholder_api", new PlaceholderParser(500, Utils.asList("§7PlaceholderAPI placeholders : §8%placeholder%")) {
			@Override
			public String parse(Player player, String raw) {
				return player != null ? Utils.fillPlaceholderAPI(player, raw) : raw;
			}
		});
		// Math parser
		register("math", new SimplePlaceholderParser(1000, '{', '}', Utils.asList("§7Math expression : §8{math:EXPRESSION}", "§8 (accepts +,-,*,/,(),sin,cos,tan,log,ceil,floor)")) {
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
