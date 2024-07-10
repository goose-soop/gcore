package com.guillaumevdn.gcore.lib.logic;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;
import com.guillaumevdn.gcore.lib.tuple.Triple;

/**
 * @author GuillaumeVDN
 */
public final class LogicUtils {

	public static boolean match(String logicString, Replacer replacer) {
		return match(logicString, replacer, null);
	}

	public static boolean match(String logicString, Replacer replacer, List<Triple<Double, Double, ComparisonType>> addToListIfMatch) {
		// empty ?
		logicString = logicString.trim();
		if (logicString.isEmpty()) {
			return true;
		}

		// read base groups
		Merger merger = null;
		int mergerCount = 0;
		List<String> baseGroups = new ArrayList<>(); // groups found with mergers parenthesis, such as [...] AND/OR [...] or simply [...], or implicit groups
														// (<elem1> AND <elem2> with no parenthesis [...])
		int depth = 0;
		int beginGroup = 0;
		char[] chars = logicString.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			char ch = chars[i];
			// begin group
			if (ch == '[') {
				if (depth == 0) { // base group
					if (mergerCount != baseGroups.size()) { // missing merger, means we found something like '[...] [...]'
						throw new LogicError("invalid [ found at index " + i + " in '" + logicString + "'");
					}
					beginGroup = i;
				}
				++depth;
			}
			// end group
			else if (ch == ']') {
				--depth;
				if (depth == 0) { // base group, so add
					baseGroups.add(logicString.substring(beginGroup + 1, i).trim());
					beginGroup = i + 1; // mark beginning of potential new implicit group
				} else if (depth < 0) { // invalid group end, too much
					throw new LogicError("invalid ] found at index " + i + " in '" + logicString + "'");
				}
			}
			// something else ; only check if depth is 0 to decode mergers
			else if (depth == 0) {
				// whitespace
				if (Character.isWhitespace(ch)) {
					continue;
				}

				// merger
				if (mergerCount <= baseGroups.size() && i + 1 < chars.length && ((ch == '&' && chars[i + 1] == '&') || (ch == '|' && chars[i + 1] == '|'))) {
					++i;
					Merger newMerger = ch == '&' ? Merger.AND : Merger.OR;
					if (merger != null && !merger.equals(newMerger)) {
						throw new LogicError("merger was '" + merger + "' but found " + (ch == '&' ? "AND" : "OR") + " for the same group at index " + i
								+ " in '" + logicString + "' ; use [] to enclose different mergers");
					}
					merger = newMerger;
					++mergerCount;

					// consume implicit group if depth is 0
					if (depth == 0) {
						String implicitGroup = logicString.substring(beginGroup, i - 1).trim();

						if (!implicitGroup.isEmpty()) { // might be the case with strings such as [merged conditions] && <implicit group>
							baseGroups.add(implicitGroup);
						}
						beginGroup = i + 1; // mark beginning of new potential implicit group
					}
				}

				// no merger found ; maybe depth is 0 and it's an unknown character because there's just no merger eyyyy
				continue;
				// throw new LogicError("invalid character '" + ch + "' found at index " + i + " in '" + logicString + "'");
			}
		}
		if (depth > 0) {
			throw new LogicError("missing closing ] in '" + logicString + "'");
		}

		// add last implicit group if needed
		if (beginGroup < chars.length) {
			baseGroups.add(logicString.substring(beginGroup, chars.length).trim());
		}

		// no group was found, add a single one with the entire thing
		if (baseGroups.isEmpty()) {
			baseGroups.add(logicString);
		}

		// single group, but still has mergers (might still remain with strings such as [<some logic string>], wrapped entierly)
		if (baseGroups.size() == 1 && (logicString.contains("&&") || logicString.contains("||"))) {
			return match(baseGroups.get(0), replacer, addToListIfMatch);
		}

		// no subgroups
		if (merger == null) {
			return singleMatch(baseGroups.get(0), replacer, addToListIfMatch);
		}
		// merger
		else {
			if (merger.equals(Merger.AND)) {
				for (String sub : baseGroups) {
					if (!match(sub, replacer, addToListIfMatch)) {
						return false;
					}
				}
				return true;
			} else {
				for (String sub : baseGroups) {
					if (match(sub, replacer, addToListIfMatch)) {
						return true;
					}
				}
				return true;
			}
		}
	}

	private static boolean singleMatch(String logicString, Replacer replacer, List<Triple<Double, Double, ComparisonType>> addToListIfMatch) {
		logicString = replacer.parse(logicString).trim();

		// 'in' operator (|in|)
		String[] inSplit = logicString.split("\\|in\\|");
		if (inSplit.length > 1) {
			if (inSplit.length != 3) {
				throw new LogicError("invalid |in| usage in '" + logicString + "'");
			}
			String value = inSplit[0].trim();
			for (String in : inSplit[2].split("\\|")) {
				if (in.trim().equals(value)) {
					return true;
				}
			}
			return false;
		}

		// find comparison
		List<Pair<Integer, ComparisonType>> comparisons = new ArrayList<>();
		main: for (int index = 0; index < logicString.length() - 1; ++index) {
			for (ComparisonType comp : ComparisonType.values()) {
				if (!comp.equals(ComparisonType.IN_RANGE)) {
					if (index + comp.getSymbol().length() + 1 < logicString.length() && logicString.charAt(index + comp.getSymbol().length()) == ' ' // to avoid
																																						// interpreting
																																						// '<='
																																						// as
																																						// '<'
																																						// for
																																						// instance
							&& logicString.substring(index, index + comp.getSymbol().length()).equals(comp.getSymbol())) {
						comparisons.add(Pair.of(index, comp));
						index += comp.getSymbol().length() - 1; // -1 because ++index
						continue main;
					}
				}
			}
		}

		// no comparisons found
		if (comparisons.isEmpty()) {
			throw new LogicError("no valid operator found '" + logicString + "'");
		}

		// go through comparisons
		int beginNext = 0;
		for (int i = 0; i < comparisons.size(); ++i) {
			Pair<Integer, ComparisonType> pair = comparisons.get(i);
			// find left/right
			String left = logicString.substring(beginNext, pair.getA()).trim();
			String right;
			if (i + 1 < comparisons.size()) {
				right = logicString.substring(beginNext, comparisons.get(i + 1).getA()).trim();
				beginNext = pair.getA() + pair.getB().getSymbol().length();
			} else {
				right = logicString.substring(pair.getA() + pair.getB().getSymbol().length()).trim();
			}
			// compare
			if (pair.getB().equals(ComparisonType.EQUALS)) {
				if (!left.equals(right)) {
					return false;
				}
			} else if (pair.getB().equals(ComparisonType.DIFFERENT)) {
				if (left.equals(right)) {
					return false;
				}
			} else {
				Double leftD = NumberUtils.doubleOrNull(left);
				Double rightD = NumberUtils.doubleOrNull(right);
				if (!pair.getB().compare(leftD != null ? leftD : 0d, rightD != null ? rightD : 0d, null)) {
					return false;
				}
				if (addToListIfMatch != null) {
					addToListIfMatch.add(Triple.of(leftD, rightD, pair.getB()));
				}
			}
		}

		// good
		return true;
	}

	private static enum Merger {
		AND, OR;
	}

	public static class LogicError extends Error {

		private static final long serialVersionUID = -8958202336274552915L;

		public LogicError(String message) {
			super(message);
		}

	}

}
