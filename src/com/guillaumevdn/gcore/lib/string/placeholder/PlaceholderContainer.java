package com.guillaumevdn.gcore.lib.string.placeholder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.SortedHashMap;
import com.guillaumevdn.gcore.lib.location.Point;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.player.PlayerUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderContainer implements Comparable<PlaceholderContainer> {

	private String id;
	private int priority;
	private boolean needPlayer;
	private List<String> description;
	private BiFunction<String, Player, String> replacer;

	public PlaceholderContainer(String id, int priority, boolean needPlayer, List<String> description, BiFunction<String, Player, String> replacer) {
		this.id = id.toLowerCase();
		this.priority = priority;
		this.needPlayer = needPlayer;
		this.description = description;
		this.replacer = replacer;
	}

	// get
	public String getId() {
		return id;
	}

	public int getPriority() {
		return priority;
	}

	public boolean needPlayer() {
		return needPlayer;
	}

	public List<String> getDescription() {
		return description;
	}

	// methods
	public String parse(String string, Player player) {
		try {
			return needPlayer && player == null ? string : replacer.apply(string, player);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't parse '" + string + "' for placeholder container " + id + " (invalid placeholder ?)", exception);
			return string;
		}
	}

	@Override
	public int compareTo(PlaceholderContainer other) {
		return Integer.compare(priority, other.priority);
	}

	// static methods
	public static String parseAll(String line, Player player) {
		if (line != null) {
			for (PlaceholderContainer replacer : registered.values()) {
				line = replacer.parse(line, player);
			}
		}
		return line;
	}

	public static List<String> parseAll(List<String> list, Player player) {
		if (list == null) {
			return null;
		}
		List<String> result = new ArrayList<>();
		list.forEach(line -> result.add(parseAll(line, player)));
		return result;
	}

	public static void parseAllNoClone(List<String> list, Player player) {
		if (list == null) {
			return;
		}
		for (int i = 0; i < list.size(); ++i) {
			list.set(i, parseAll(list.get(i), player));
		}
	}

	public static List<String> parseAll(String[] array, Player player) {
		if (array == null) {
			return null;
		}
		List<String> result = new ArrayList<>();
		for (String line : array) {
			result.add(parseAll(line, player));
		}
		return result;
	}

	public static List<String> describeAll() {
		List<String> desc = new ArrayList<>();
		for (PlaceholderContainer replacer : registered.values()) {
			desc.addAll(replacer.getDescription());
		}
		return desc;
	}

	// registration
	private static SortedHashMap<String, PlaceholderContainer> registered = SortedHashMap.valueSorted();

	public static SortedHashMap<String, PlaceholderContainer> values() {
		return registered;
	}

	public static <T extends PlaceholderContainer> T register(T container) {
		registered.put(container.getId(), container);
		return container;
	}

	public static void unregister(String id) {
		registered.remove(id);
	}

	// values
	public static final PlaceholderContainerBrackets PLAYER_LOCATION = register(new PlaceholderContainerBrackets("player_location", 1, true, CollectionUtils.asList("§7Player location : §8{player_location}"), (placeholderContent, player) -> {
		if (placeholderContent.equalsIgnoreCase("player_location")) {
			return Serializer.POINT.serialize(new Point(player.getLocation()));
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets PLAYER = register(new PlaceholderContainerBrackets("player", 1, true, CollectionUtils.asList("§7Player name : §8{player}"), (placeholderContent, player) -> {
		if (placeholderContent.equalsIgnoreCase("player")) {
			return player.getName();
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets RANDOM_IN_LIST = register(new PlaceholderContainerBrackets("random_in_list", 1, false, CollectionUtils.asList("§7Random in list : §8{random_in_list:value1,value2,value3,...}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("random_in_list:")) {
			String[] split = placeholderContent.substring("random_in_list:".length()).split(",");
			return split.length == 0 ? "0" : split[NumberUtils.random(0, split.length - 1)];
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets TRIENAL_CONDITIONS = register(new PlaceholderContainerBrackets("trienal_conditions", 2, true, CollectionUtils.asList("§7Condition : §8{permission:PERM,IF_HAS,IF_HASNT}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("permission:")) {
			String[] split = placeholderContent.substring("permission:".length()).split(",");
			if (PlayerUtils.hasPermission(player, split[0])) {
				return split[1];
			} else {
				return split[2];
			}
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainer PLACEHOLDER_API = register(new PlaceholderContainer("placeholderapi", 998, true, CollectionUtils.asList("§7Supports Placeholder API placeholders"), (string, player) -> {
		return PlaceholderAPIUtils.parse(player, string);
	}));

	public static final PlaceholderContainerBrackets RANDOM_INTEGER = register(new PlaceholderContainerBrackets("random_integer", 999, false, CollectionUtils.asList("§7Random number : §8{random_integer:MIN,MAX}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("random_integer:")) {
			String[] split = placeholderContent.substring("random_integer:".length()).split(",");
			return new BigDecimal(String.valueOf(NumberUtils.random(Integer.parseInt(split[0]), Integer.parseInt(split[1])))).toPlainString();
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets RANDOM_DOUBLE = register(new PlaceholderContainerBrackets("random_double", 999, false, CollectionUtils.asList("§7Random number : §8{random_double:MIN,MAX}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("random_double:")) {
			String[] split = placeholderContent.substring("random_double:".length()).split(",");
			return new BigDecimal(String.valueOf(NumberUtils.random(Double.parseDouble(split[0]), Double.parseDouble(split[1])))).toPlainString();
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets MATH = register(new PlaceholderContainerBrackets("math", 1000, false, CollectionUtils.asList("§7Math expression : §8{math:EXPRESSION}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("math:")) {
			return new BigDecimal(NumberUtils.calculateExpression(placeholderContent.substring("math:".length()))).toPlainString();
		}
		return null;  // no match;
	}));

	public static final PlaceholderContainerBrackets ROUND = register(new PlaceholderContainerBrackets("mathround", 1001, false, CollectionUtils.asList("§7Math round : §8{mathround:places,EXPRESSION}"), (placeholderContent, player) -> {
		if (placeholderContent.toLowerCase().startsWith("mathround:")) {
			String content = placeholderContent.substring("mathround:".length());
			int index = content.indexOf(',');
			if (index != -1) {
				int places = NumberUtils.integerOrElse(content.substring(0, index), 0);
				double calc = NumberUtils.calculateExpression(content.substring(index + 1));
				return "" + NumberUtils.round(calc, places);
			}
		}
		return null;  // no match;
	}));

}
