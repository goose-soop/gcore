package com.guillaumevdn.gcore.lib.string;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.tuple.LongIntegerPair;

/**
 * @author GuillaumeVDN
 */
public final class StringUtils {

	// ----- color formatting
	public static final List<Character> FORMAT_CODES = CollectionUtils.asUnmodifiableList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r');
	public static final List<Character> FORMAT_CODES_HEX = CollectionUtils.asUnmodifiableList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

	public static String format(String string) {
		if (string == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder(Version.ATLEAST_1_16 ? string.length() + countChar(string, '#') * 6 : string.length());
		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			if (chars[i] == '&' && i + 1 < chars.length) {
				char format = Character.toLowerCase(chars[i + 1]);
				if (FORMAT_CODES.contains(format)) {
					builder.append('§');
					builder.append(format); // to lower case
					++i;
				} else if (format == '#') {
					if (Version.ATLEAST_1_16 && i + 7 < chars.length) {
						try {
							StringBuilder code = new StringBuilder(6), hex = new StringBuilder(12);
							for (int j = 2 /* 2 to skip the &# */; j < 8; ++j) {
								code.append(chars[i + j]);
								hex.append('§').append(chars[i + j]);
							}
							Integer.parseInt(code.toString(), 16); // make sure it's actually a correct code
							builder.append("§x").append(hex);
							i += 7;
						} catch (NumberFormatException ignored) {
							ignored.printStackTrace();
							builder.append(chars[i]);
						}
					}
				} else {
					builder.append(chars[i]);
				}
			} else {
				builder.append(chars[i]);
			}
		}
		return builder.toString();
	}

	/**
	 * @return the same list but formatted
	 */
	public static void format(List<String> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); ++i) {
				list.set(i, format(list.get(i)));
			}
		}
	}

	/**
	 * @return a formatted copy of the list
	 */
	public static List<String> formatCopy(List<String> list) {
		if (list == null) {
			return null;
		}
		List<String> copy = CollectionUtils.asList(list);
		format(copy);
		return copy;
	}

	public static String unformat(String string) {
		return string == null ? null : ChatColor.stripColor(string);
	}

	public static List<String> unformatCopy(List<String> list) {
		if (list == null) {
			return null;
		}
		List<String> copy = new ArrayList<>();
		for (String str : list) {
			copy.add(unformat(str));
		}
		return copy;
	}

	public static String retranslateColorCodes(String string) {
		char[] chars = string.toCharArray();
		for (int i = 0; i + 1 < chars.length; ++i) {
			if (chars[i] == '§') {
				char format = Character.toLowerCase(chars[i + 1]);
				if (FORMAT_CODES_HEX.contains(format)) {
					chars[i] = '&';
					chars[i + 1] = format; // to lower case
				}
				// TODO : retranslate HEX codes
			}
		}
		return new String(chars);
	}

	/**
	 * @return the same list but with retranslated color codes
	 */
	public static List<String> retranslateColorCodes(List<String> list) {
		if (list != null) {
			for (int i = 0; i < list.size(); ++i) {
				list.set(i, retranslateColorCodes(list.get(i)));
			}
		}
		return list;
	}

	/**
	 * @return a copy of the list with retranslated color codes
	 */
	public static List<String> retranslateColorCodesCopy(List<String> list) {
		return list == null ? null : retranslateColorCodes(CollectionUtils.asList(list));
	}

	public static Character findFirstColorCode(String string) {
		for (int i = 0; i < string.length(); ++i) {
			char c = string.charAt(i);
			if (c == ChatColor.COLOR_CHAR && i + 1 < string.length()) {
				return string.charAt(++i);
			}
		}
		return null;
	}

	public static List<Integer> findChars(String string, char ch) {
		if (string == null) {
			return null;
		}
		List<Integer> result = new ArrayList<>();
		char[] array = string.toCharArray();
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == ch) {
				result.add(i);
			}
		}
		return result;
	}

	private static final Pattern regexColor = Pattern.compile("§[a-f0-9]{1}");
	private static final Pattern regexFormat = Pattern.compile("§[klmnor]{1}");

	public static String getLastColors(String string) {
		string = format(string);
		// find matches
		Matcher matcherColor = regexColor.matcher(string);
		String lastColor = null;
		int lastColorStart = -1;
		while (matcherColor.find()) {
			lastColor = matcherColor.group();
			lastColorStart = matcherColor.start();
		}
		Matcher matcherFormat = regexFormat.matcher(string);
		String lastFormat = null;
		int lastFormatStart = -1;
		while (matcherFormat.find()) {
			lastFormat = matcherFormat.group();
			lastFormatStart = matcherFormat.start();
		}
		// has format
		if (lastFormat != null) {
			// has color
			if (lastColor != null) {
				// before format, return both
				if (lastColorStart < lastFormatStart) {
					return lastColor + lastFormat;
				}
				// after format, return color
				return lastColor;
			}
			// no color, just return format
			return lastFormat;
		}
		// just color maybe
		return lastColor != null ? lastColor : "";
	}

	// ----- numbers
	private static final BigDecimal MILLION = new BigDecimal("1000000");
	private static final BigDecimal BILLION = new BigDecimal("1000000000");
	private static final BigDecimal TRILLION = new BigDecimal("1000000000000");

	public static String formatNumber(int number) {
		return formatNumber((double) number);
	}

	public static String formatNumber(double number) {
		try {
			String[] split = new BigDecimal(Math.abs(number)).setScale(ConfigGCore.numberFormattingDecimals, RoundingMode.HALF_DOWN).toPlainString().split("\\.");
			BigDecimal integer = new BigDecimal(split[0]);
			// big numbers
			String suffix = "";
			if (ConfigGCore.numberFormattingBigNumbers) {
				if (integer.compareTo(TRILLION) >= 0) {
					suffix = " trillion";
					integer = integer.divide(TRILLION);
				} else if (integer.compareTo(BILLION) >= 0) {
					suffix = " billion";
					integer = integer.divide(BILLION);
				} else if (integer.compareTo(MILLION) >= 0) {
					suffix = " million";
					integer = integer.divide(MILLION);
				}
			}
			// separate thousands
			String integerFormat = integer.toPlainString();
			if (ConfigGCore.numberFormattingSeparateThousands != null) {
				String todo = integerFormat;
				integerFormat = "";
				while (todo.length() > 3) {
					integerFormat = ConfigGCore.numberFormattingSeparateThousands + todo.substring(todo.length() - 3) + integerFormat;
					todo = todo.substring(0, todo.length() - 3);
				}
				integerFormat = todo + integerFormat;
			}
			// done
			Integer decimals = split.length == 2 ? NumberUtils.integerOrNull(split[1]) : null;
			return (number < 0d ? "-" : "") + integerFormat + (decimals != null && decimals != 0 ? "." + split[1] : "") + suffix;
		} catch (NumberFormatException ignored) {  // #1110
			return "" + number;
		}
	}

	public static String makeProgressBar(double percentage, int barLength, String barChar, String barColor, String barEmpty) {
		int ok = (int) Math.floor(percentage / 100d * barLength);
		String bar = barColor;
		for (int i = 0; i < ok; ++i) bar += barChar;
		bar += barEmpty;
		for (int i = 0; i < (barLength - ok); ++i) bar += barChar;
		return bar;
	}

	// https://stackoverflow.com/questions/11089399/count-with-a-b-c-d-instead-of-0-1-2-3-with-javascript
	private static final char[] ALPHABETIC = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static String alphabeticCountFor(int i) {
		int mod = i % 26;
		int pow = i / 26 | 0;
		char out;
		if (mod != 0) {
			out = ALPHABETIC[mod - 1];
		} else {
			--pow;
			out = 'z';
		}
		return pow != 0 ? alphabeticCountFor(pow) + out : "" + out;
	}

	// ----- time
	public static String formatDurationTicks(long ticks) {
		return formatDurationTicks(ticks, true);
	}

	public static String formatDurationTicks(long ticks, boolean allowSecs) {
		int seconds = (int) (ticks / 20L);
		float secondsDecimals = !allowSecs ? 0f : (float) (((double) (ticks % 20L)) / 20d);
		return formatDurationSeconds(seconds, secondsDecimals);
	}

	public static String formatDurationMillis(long millis) {
		return formatDurationSeconds((int) (millis / 1000L), 0f);
	}

	public static String formatDurationMillisWithSecondDecimals(long millis) {
		int seconds = (int) (millis / 1000L);
		float secondsDecimals = (float) (((double) (millis % 1000L)) / 1000d);
		return formatDurationSeconds(seconds, secondsDecimals);
	}

	public static String formatDurationSeconds(int seconds) {
		return formatDurationSeconds(seconds, 0f);
	}

	public static String formatDurationSeconds(int seconds, float secondsDecimals) {
		if (seconds < 0) {
			return ConfigGCore.unknownPlaceholderResult;
		} else if (seconds < 60) {
			return TextGeneric.durationFormatS
					.replace("{seconds}", () -> (seconds % 60) + (secondsDecimals > 0f ? toTextStringDecimals(secondsDecimals, 2) : ""))
					.parseLine();
		} else if (seconds < 3600) {
			return TextGeneric.durationFormatMS
					.replace("{minutes}", () -> ((seconds % 3600) / 60))
					.replace("{seconds}", () -> twoDigitString(seconds % 60) + (secondsDecimals > 0f ? toTextStringDecimals(secondsDecimals, 2) : ""))
					.parseLine();
		} else if (seconds < 86400) {
			return TextGeneric.durationFormatHMS
					.replace("{hours}", () -> (seconds / 3600))
					.replace("{minutes}", () -> twoDigitString((seconds % 3600) / 60))
					.replace("{seconds}", () -> twoDigitString(seconds % 60) + (secondsDecimals > 0f ? toTextStringDecimals(secondsDecimals, 2) : ""))
					.parseLine();
		} else {
			return TextGeneric.durationFormatDHMS
					.replace("{days}", () -> (seconds / 86400))
					.replace("{hours}", () -> twoDigitString((seconds % 86400) / 3600))
					.replace("{minutes}", () -> twoDigitString((seconds % 3600) / 60))
					.replace("{seconds}", () -> twoDigitString(seconds % 60) + (secondsDecimals > 0f ? toTextStringDecimals(secondsDecimals, 2) : ""))
					.parseLine();
		}
	}

	private static String twoDigitString(int number) {
		if (number == 0) {
			return "00";
		} else if (number < 10) {
			return "0" + number;
		} else {
			return String.valueOf(number);
		}
	}

	// ----- object
	public static String replacementToString(Object object, boolean formatNumbers) {
		if (object == null) {
			return null;
		}
		if (object instanceof Integer) {
			return !formatNumbers ? ((Integer) object).toString() : formatNumber(((Integer) object).intValue());
		} else if (object instanceof Double) {
			return !formatNumbers ? BigDecimal.valueOf((Double) object).toPlainString() : formatNumber(((Double) object).doubleValue());
		} else if (object instanceof Collection<?>) {
			return toTextString(", ", (Collection<?>) object);
		} else if (object instanceof Object[]) {
			return toTextString(", ", (Object[]) object);
		}
		return object.toString();
	}

	// ----- alphanumeric
	private static final char[] alphanumericCharacters = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

	public static String generateRandomAlphanumericString(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			builder.append(alphanumericCharacters[NumberUtils.random(0, alphanumericCharacters.length - 1)]);
		}
		return builder.toString();
	}

	public static boolean isAlphanumeric(String string) {
		if (string == null) return false;
		for (char ch : string.toLowerCase().toCharArray()) {
			if (!isAlphanumeric(ch)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAlphabetic(String string) {
		if (string == null) return false;
		for (char ch : string.toCharArray()) {
			if (!isAlphabetic(ch)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAlphabetic(char ch) {
		ch = Character.toLowerCase(ch);
		return ch >= 'a' && ch <= 'z';
	}

	public static boolean isAlphanumeric(char ch) {
		ch = Character.toLowerCase(ch);
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z');
	}

	// ----- version
	public static Integer getUniqueVersionNumber(String version) {
		return getUniqueVersionNumber(version, 3);
	}

	public static Integer getUniqueVersionNumber(String version, int partLength) {
		if (version.contains("-")) {
			version = version.split("-")[0];
		}
		String result = "1"; // add 1 to consider the eventual 0 before
		String[] temp = new String[3]; // up to 3 entries maximum, example : 1.0.0 will be converted to 1 100 000 000
		int i = 0;
		for (String str : version.split("\\.")) {
			temp[i++] = repeatString("0", partLength - str.length()) + str;
		}
		for (i = 0; i < temp.length; i++) {
			if (temp[i] == null) temp[i] = repeatString("0", partLength);
			result += temp[i];
		}
		return NumberUtils.integerOrNull(result);
	}

	// ----- text
	public static String getReadableName(Class clazz) {
		return separateAtCaps(clazz.getSimpleName());
	}

	public static String separateAtCaps(String string) {
		StringBuilder builder = new StringBuilder(string.length());
		boolean last = false;
		for (char ch : string.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				if (builder.length() != 0 && !last) {
					builder.append(' ');
				}
				builder.append(Character.toLowerCase(ch));
				last = true;
			} else {
				builder.append(ch);
				last = false;
			}
		}
		return builder.toString();
	}

	public static String separateAtUnderscore(String string) {
		StringBuilder builder = new StringBuilder(string.length());
		for (char ch : string.toCharArray()) {
			if (ch == '_') {// monkaX
				builder.append(' ');
			} else {
				builder.append(Character.toLowerCase(ch));
			}
		}
		return builder.toString();
	}

	public static String pluralizeAmountDesc(String singularDesc, int count) {
		return count + " " + singularDesc + pluralize(count);
	}

	public static String pluralize(String string, int count) {
		return string + pluralize(count);
	}

	public static String pluralize(int count) {
		return count > 1 ? "s" : "";
	}

	public static String toTextString(String separator, Object... objects) {
		return toTextString(separator, CollectionUtils.asList(objects));
	}

	public static String toTextString(String separator, Stream<?> stream) {
		StringBuilder builder = new StringBuilder();
		stream.forEachOrdered(elem -> {
			toTextStringObject(elem, builder);
			builder.append(separator);
		});
		if (builder.length() != 0) {
			String str = builder.toString();
			return str.substring(0, str.length() - separator.length());
		}
		return "";
	}

	public static String toTextString(String separator, Iterable collection) {
		StringBuilder builder = new StringBuilder();
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			toTextStringObject(iterator.next(), builder);
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public static String toTextString(String separator, String keySeparator, Map map) {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry next = iterator.next();
			toTextStringObject(next.getKey(), builder);
			builder.append(keySeparator);
			toTextStringObject(next.getValue(), builder);
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public static void toTextStringObject(Object object, StringBuilder builder) {
		if (object != null && object.getClass().getName().contains("TextComponent")) {
			try {
				builder.append(ReflectionObject.of(object).getField("text").justGet());
			} catch (Throwable ignored) {
				builder.append(object);
			}
		} else {
			builder.append(object);
		}
	}

	private static Map<Integer, DecimalFormat> doubleFormats = new HashMap<>();
	private static Map<Integer, DecimalFormat> doubleDecimalsFormats = new HashMap<>();

	public static DecimalFormat getDoubleFormat(int places) {
		DecimalFormat format = doubleFormats.get(places);
		if (format == null) {
			doubleFormats.put(places, format = new DecimalFormat("#." + repeatString("#", places), DecimalFormatSymbols.getInstance(Locale.US)));
			format.setRoundingMode(RoundingMode.HALF_EVEN);
		}
		return format;
	}

	public static DecimalFormat getDoubleDecimalsFormat(int places) {
		DecimalFormat format = doubleDecimalsFormats.get(places);
		if (format == null) {
			doubleDecimalsFormats.put(places, format = new DecimalFormat("." + repeatString("#", places), DecimalFormatSymbols.getInstance(Locale.US)));
			format.setRoundingMode(RoundingMode.HALF_EVEN);
		}
		return format;
	}

	public static String toTextString(double value, int roundPlaces) {
		return value == 1d ? String.valueOf(value) : getDoubleFormat(roundPlaces).format(value);
	}

	public static String toTextStringDecimals(double value, int roundPlaces) {
		return value == 1d ? String.valueOf(value) : getDoubleDecimalsFormat(roundPlaces).format(value);
	}

	public static String capitalize(String string) {
		if (string.isEmpty()) {
			return string;
		} else if (string.length() == 1) {
			return string.toUpperCase();
		}
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	public static String snakeCaseToCamelCase(String string) {
		StringBuilder builder = new StringBuilder(string.length() * 2);  // worst case
		boolean upNext = false;
		for (char ch : string.toCharArray()) {
			if (ch == '_') {
				upNext = true;
			} else if (upNext) {
				builder.append(Character.toUpperCase(ch));
				upNext = false;
			} else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}

	public static String camelCaseToSnakeCase(String string) {
		StringBuilder builder = new StringBuilder(string.length() * 2);  // worst case
		for (char ch : string.toCharArray()) {
			if (Character.isUpperCase(ch)) {
				builder.append('_');
				builder.append(Character.toLowerCase(ch));
			} else {
				builder.append(ch);
			}
		}
		return builder.toString();
	}

	// ----- collection
	public static List<Integer> findPlaceholderLines(List<String> list) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < list.size(); ++i) {
			String line = list.get(i);
			if (hasPlaceholders(line)) {
				result.add(i);
			}
		}
		return result;
	}

	public static boolean hasPlaceholders(List<String> list) {
		if (list == null) {
			return false;
		}
		for (String line : list) {
			if (hasPlaceholders(line)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasBracketPlaceholders(String line) {
		return countChar(line, '{') >= 1 && countChar(line, '}') >= 1;
	}

	public static boolean hasPlaceholders(String line) {
		return hasBracketPlaceholders(line) || countChar(line, '%') >= 2;
	}

	public static boolean hasPlaceholders(ItemStack item) {
		ItemMeta meta = item == null ? null : item.getItemMeta();
		return meta != null && (hasPlaceholders(meta.getDisplayName()) || hasPlaceholders(meta.getLore()));
	}

	@Nonnull
	public static Set<String> getPlaceholders(String string) {
		Set<String> placeholders = new HashSet<>();
		if (string != null) {
			doGetPlaceholders(string, '{', '}', placeholders);
			doGetPlaceholders(string, '%', '%', placeholders);
		}
		return placeholders;
	}

	@Nonnull
	public static Set<String> getPlaceholders(List<String> list) {
		Set<String> placeholders = new HashSet<>();
		if (list != null) {
			list.forEach(line -> {
				doGetPlaceholders(line, '{', '}', placeholders);
				doGetPlaceholders(line, '%', '%', placeholders);
			});
		}
		return placeholders;
	}

	private static Set<String> doGetPlaceholders(String string, char begin, char end, Set<String> placeholders) {
		LinkedList<Integer> lastStarts = new LinkedList<>();
		for (int i = 0; i < string.length(); ++i) {
			char c = string.charAt(i);
			if (c == begin) {
				if (i + 1 < string.length() && isAlphanumeric(string.charAt(i + 1))) {
					lastStarts.add(i);
				}
			} else if (c == end) {
				if (!lastStarts.isEmpty()) {
					placeholders.add(string.substring(lastStarts.removeLast(), i + 1));
				}
			}
		}
		return placeholders;
	}

	@Nonnull
	public static Set<String> getPlaceholders(ItemStack item) {
		Set<String> placeholders = new HashSet<>();
		ItemMeta meta = item == null ? null : item.getItemMeta();
		if (meta != null) {
			if (meta.hasDisplayName()) {
				doGetPlaceholders(meta.getDisplayName(), '{', '}', placeholders);
				doGetPlaceholders(meta.getDisplayName(), '%', '%', placeholders);
			}
			if (meta.hasLore()) {
				meta.getLore().forEach(line -> {
					doGetPlaceholders(line, '{', '}', placeholders);
					doGetPlaceholders(line, '%', '%', placeholders);
				});
			}
		}
		return placeholders;
	}

	public static List<String> split(String string, String separator, int limit) {
		if (string == null) return new ArrayList<>();
		if (limit <= 0) limit = Integer.MAX_VALUE;
		List<String> result = new ArrayList<>();
		int index = -1;
		while ((index = string.indexOf(separator)) != -1 && --limit >= 0) {
			result.add(string.substring(0, index));
			string = string.substring(index + separator.length());
		}
		if (!string.isEmpty()) {
			result.add(string);
		}
		return result;
	}

	/** @return the same list but with its elements altered */
	public static List<String> alterElements(List<String> list, String prefix, String suffix) {
		for (int i = 0; i < list.size(); ++i) {
			list.set(i, prefix + list.get(i) + suffix);
		}
		return list;
	}

	public static List<String> asStringList(Collection collection) {
		List<String> list = new ArrayList<>();
		if (collection != null) {
			for (Object object : collection) {
				if (object == null) {
					list.add("null");
				} else if (object instanceof Collection) {
					list.addAll(asStringList((Collection) object));
				} else {
					list.add(String.valueOf(object));
				}
			}
		}
		return list;
	}

	public static List<String> splitLongText(List<String> list, int startSplittingLength) {
		return splitLongText(list, startSplittingLength, null);
	}

	public static List<String> splitLongText(List<String> list, int startSplittingLength, UnaryOperator<String> operateSplitted) {
		List<String> result = new ArrayList<>();
		for (String line : list) {
			result.addAll(splitLongText(line, startSplittingLength, operateSplitted));
		}
		return result;
	}

	public static List<String> splitLongText(String string, int startSplittingLength) {
		return splitLongText(string, startSplittingLength, null);
	}

	public static List<String> splitLongText(String string, int startSplittingLength, UnaryOperator<String> operateSplitted) {
		List<String> result = new ArrayList<>();
		// split with spaces before
		while (string.length() > startSplittingLength) {
			String unformat = unformat(string);
			int index = unformat.indexOf(' ', startSplittingLength);
			if (index != -1) index = string.lastIndexOf(' ', index);
			if (index != -1) {
				string = splitLongTextPart(string, result, index, true, operateSplitted);
			} else {
				break;
			}
		}
		// split with spaces after
		while (string.length() > startSplittingLength) {
			int index = string.lastIndexOf(' ', startSplittingLength);
			if (index != -1) {
				string = splitLongTextPart(string, result, index, true, operateSplitted);
			} else {
				break;
			}
		}
		// string is still too long
		if (string.length() > startSplittingLength) {
			while (string.length() > startSplittingLength) {
				string = splitLongTextPart(string, result, startSplittingLength, false, operateSplitted);
			}
		}
		// string has remaining, add it
		if (!string.trim().isEmpty()) {
			String colors = result.isEmpty() ? "" : StringUtils.getLastColors(result.get(result.size() - 1));
			string = colors + string;  // so operateSplitted operates with colors at the beginning of the string, like other parts before
			result.add(result.isEmpty() || operateSplitted == null ? string : operateSplitted.apply(string));
		}
		// done
		return result;
	}

	private static String splitLongTextPart(String string, List<String> result, int index, boolean skipChar, UnaryOperator<String> operateSplitted) {
		String sub = string.substring(0, index);
		if (!result.isEmpty()) {
			String colors = StringUtils.getLastColors(result.get(result.size() - 1));
			if (colors != null) {
				sub = colors + sub;
			}
		}
		result.add(result.isEmpty() || operateSplitted == null ? sub : operateSplitted.apply(sub));
		return string.substring(skipChar ? index + 1 : index);
	}

	public static List<String> splitLongTextMaxLength(String text, int maxLength, UnaryOperator<String> operateSplitted) {
		if (text.length() <= maxLength) {
			return CollectionUtils.asList(text);
		}
		if (operateSplitted == null) {
			operateSplitted = s -> s;
		}
		List<String> result = new ArrayList<>();
		String left = text;
		while (left.length() > maxLength) {
			int index = left.lastIndexOf(" ", maxLength);
			if (index == -1) index = maxLength;
			result.add((result.isEmpty() ? "" : operateSplitted.apply(getLastColors(result.get(result.size() - 1)))) + left.substring(0, index));
			left = left.substring(index + 1);
		}
		result.add((result.isEmpty() ? "" : operateSplitted.apply(getLastColors(result.get(result.size() - 1))) + left));
		return result;
	}

	// ----- string
	public static String nonEmptyOrNull(String string) {
		if (string == null || string.isEmpty()) return null;
		string = string.trim();
		return string.isEmpty() ? null : string;
	}

	public static String nonEmptyOr(String string, String def) {
		if (string == null || string.isEmpty()) return def;
		string = string.trim();
		return string.isEmpty() ? def : string;
	}

	public static String nonEmptyAlphanumericOrNull(String string) {
		if (string == null || string.isEmpty()) return null;
		string = string.trim();
		return string.isEmpty() || !isAlphanumeric(string) ? null : string;
	}

	public static String repeatString(String string, int repetitions) {
		if (repetitions < 1) {
			return "";
		}
		if (repetitions == 1) {
			return string;
		}
		StringBuilder builder = new StringBuilder(string.length());
		for (int i = 0; i < repetitions; ++i) {
			builder.append(string);
		}
		return builder.toString();
	}

	public static String repeatStringSeparated(String string, String separator, int repetitions) {
		if (repetitions <= 1) {
			return string;
		}
		StringBuilder builder = new StringBuilder(string.length());
		for (int i = 0; i < repetitions; ++i) {
			builder.append(string);
			if (i + 1 < repetitions) builder.append(separator);
		}
		return builder.toString();
	}

	public static boolean hasChar(String string, char ch) {
		if (string == null) {
			return false;
		}
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == ch) {
				return true;
			}
		}
		return false;
	}

	public static int countChar(String string, char ch) {
		if (string == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == ch) {
				++count;
			}
		}
		return count;
	}

	public static int countLeadingChar(String string, char ch) {
		int count = 0;
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == ch) {
				++count;
			} else {
				break;
			}
		}
		return count;
	}

	/**
	 * @return null if no number was found, or a pair containing the number and length of substring
	 */
	public static LongIntegerPair findNumberFrom(char[] val, int index) {
		StringBuilder builder = new StringBuilder();
		int len = 0;  // because we skip leading zeros
		--index;
		while (++index < val.length) {
			char ch = val[index];
			if (Character.isDigit(ch)) {
				++len;
				if (ch == '0' && builder.length() == 0) {
					continue;
				}
				builder.append(ch);
			} else {
				break;
			}
		}
		if (builder.length() == 0) {  // maybe no number at all, or only zeros
			return len != 0 ? LongIntegerPair.of(0L, len) : null;
		}
		try {
			return LongIntegerPair.of(Long.parseLong(builder.toString()), len);
		} catch (NumberFormatException ignored) {
			return null;  // just compare chars if too big
		}
	}

	// ----- compare
	public static boolean equalsIgnoreCaseNullable(String a, String b) {
		return a == null || b == null ? a == b : a.equalsIgnoreCase(b);
	}

	public static final Comparator<String> STRING_WITHNUMBERS = (a, b) -> compareAlphabeticallyWithNumbers(a, b);
	public static final Comparator<String> STRING_WITHNUMBERS_IGNORECASE = (a, b) -> compareAlphabeticallyWithNumbersIgnoreCase(a, b);
	public static final Comparator<Object> OBJECTSTRING_WITHNUMBERS = (a, b) -> STRING_WITHNUMBERS.compare(String.valueOf(a), String.valueOf(b));
	public static final Comparator<Object> OBJECTSTRING_WITHNUMBERS_IGNORECASE = (a, b) -> STRING_WITHNUMBERS_IGNORECASE.compare(String.valueOf(a), String.valueOf(b));

	public static int compareAlphabeticallyWithNumbers(String a, String b) {
		return compareAlphabeticallyWithNumbers(a, b, false);
	}

	public static int compareAlphabeticallyWithNumbersIgnoreCase(String a, String b) {
		return compareAlphabeticallyWithNumbers(a, b, true);
	}

	private static int compareAlphabeticallyWithNumbers(String a, String b, boolean ignoreCase) {
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return a == null ? 0 : -1;

		char[] val1 = a.toCharArray();
		char[] val2 = b.toCharArray();
		int maxLength = Math.max(val1.length, val2.length);
		int index1 = -1, index2 = -1;

		while (++index1 < maxLength && ++index2 < maxLength) {
			// too short
			if (index1 >= val1.length) return -1;
			if (index2 >= val2.length) return 1;

			// same characters
			char c1 = ignoreCase ? Character.toLowerCase(val1[index1]) : val1[index1];
			char c2 = ignoreCase ? Character.toLowerCase(val2[index2]) : val2[index2];

			// compare numbers
			LongIntegerPair nb1 = findNumberFrom(val1, index1), nb2 = findNumberFrom(val2, index2);
			if (nb1 != null) {
				if (nb2 == null) return c1 - c2;  // second isn't a number, compare chars
				if (nb1.getA() != nb2.getA()) {  // not the same numbers, compare them
					long diff = nb1.getA() - nb2.getA();
					if (diff >= Integer.MAX_VALUE)  {  // if diff is too big to be converted to int, compare characters directly
						String aa = a.substring(index1, index1 + nb1.getB());
						String bb = b.substring(index2, index2 + nb2.getB());
						return ignoreCase ? aa.compareToIgnoreCase(bb) : aa.compareTo(bb);
					}
					return (int) diff;
				}
				index1 += nb1.getB() - 1;  // - 1 because the loop already increments index
				index2 += nb2.getB() - 1;  // - 1 because the loop already increments index
			}
			// compare characters
			else if (c1 != c2) {
				return c1 - c2;
			}
		}

		return 0;
	}

}
