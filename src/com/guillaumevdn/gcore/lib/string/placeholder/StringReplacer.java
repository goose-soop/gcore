package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class StringReplacer {

	private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

	private LowerCaseHashMap<Supplier<Object>> exactMatch = new LowerCaseHashMap<>(5, 1f);
	private CustomMatcher customMatcher = null;
	private String customMatcherDesc = null;
	private boolean formatNumbers = true;

	private StringReplacer() {
	}

	// ----- get
	public Set<String> getExactMatchKeys() {
		return Collections.unmodifiableSet(exactMatch.keySet());
	}

	// ----- set
	public StringReplacer with(String placeholder, Supplier<Object> replacer) {
		exactMatch.put(placeholder, replacer);
		return this;
	}

	public StringReplacer with(String customMatcherDesc, CustomMatcher customMatcher) {
		if (this.customMatcher == null) {
			this.customMatcher = customMatcher;
			this.customMatcherDesc = customMatcherDesc;
		} else {
			CustomMatcher prevMatcher = this.customMatcher;
			this.customMatcher = str -> {
				Object prevResult = prevMatcher.applyCheckOverflow(str);
				return prevResult != null ? prevResult : customMatcher.applyCheckOverflow(str);
			};
			this.customMatcherDesc = customMatcherDesc + " > " + this.customMatcherDesc;
		}
		return this;
	}

	public StringReplacer replaceCustom(String customMatcherDesc, CustomMatcher customMatcher) {
		this.customMatcher = customMatcher;
		this.customMatcherDesc = customMatcherDesc;
		return this;
	}

	public StringReplacer with(StringReplacer replacer) {
		exactMatch.putAll(replacer.exactMatch);
		if (replacer.customMatcher != null) {
			with(replacer.customMatcherDesc, replacer.customMatcher);
		}
		return this;
	}

	public StringReplacer formatNumbers(boolean formatNumbers) {
		this.formatNumbers = formatNumbers;
		return this;
	}

	public StringReplacer clone() {
		StringReplacer clone = new StringReplacer();
		clone.exactMatch.putAll(exactMatch);
		clone.customMatcher = customMatcher;
		return clone;
	}

	// ----- do
	public String apply(String og) {
		if (og == null) {
			return og;
		}
		if (!StringUtils.hasBracketPlaceholders(og)) {
			return og;
		}
		// find matches
		Matcher matcher = PATTERN.matcher(og);
		StringBuffer result = new StringBuffer(og.length());
		while (matcher.find()) {
			String placeholder = matcher.group();
			// find match
			Object match = null;
			Supplier<Object> exactMatcher = exactMatch.get(placeholder);
			if (exactMatcher != null) {
				match = exactMatcher.get();
				if (match == null) throw new NullPointerException("exact match returned null for '" + placeholder + "'");
			} else if (customMatcher != null) {
				match = customMatcher.applyCheckOverflow(placeholder);
			}
			// replace in string
			String replacement = match == null ? placeholder : StringUtils.replacementToString(match, formatNumbers).replace("$", "\\$");
			matcher.appendReplacement(result, replacement);
		}
		// done
		return matcher.appendTail(result).toString();
	}

	public List<String> apply(List<String> og) {
		return apply(og, true);
	}

	public List<String> apply(List<String> og, boolean clone) {
		if (og == null) {
			return null;
		}
		List<String> result = clone ? CollectionUtils.asList(og) : og;
		// process lines
		main: for (int i = 0; i < result.size(); ++i) {
			String ogLine = result.get(i);
			// null or no placeholders
			if (ogLine == null || !StringUtils.hasBracketPlaceholders(ogLine)) {
				continue;
			}
			// find matches
			Matcher matcher = PATTERN.matcher(ogLine);
			int offset = 0;
			String resultLine = ogLine;
			while (matcher.find()) {
				int start = matcher.start() + offset;
				int end = matcher.end() + offset;
				String prefix = resultLine.substring(0, start);
				String suffix = resultLine.substring(end);
				String placeholder = resultLine.substring(start, end);
				// find match
				Supplier<Object> exactMatcher = exactMatch.get(placeholder);
				Object match = null;
				if (exactMatcher != null) {
					match = exactMatcher.get();
					if (match == null) throw new NullPointerException("exact match returned null for '" + placeholder + "'");
				} else if (customMatcher != null) {
					match = customMatcher.applyCheckOverflow(placeholder);
				}
				// match is a collection
				Collection<?> collectionMatch = null;
				if (match instanceof Collection<?>) {
					collectionMatch = (Collection<?>) match;
				} else if (match instanceof Object[]) {
					collectionMatch = CollectionUtils.asList((Object[]) match);
				}
				if (collectionMatch != null) {
					// if collection match is empty or single, skip it and eventually process is later ; this avoids suffix being put on the next line even though the replacing value is only one line
					if (collectionMatch.isEmpty() || (collectionMatch.size() == 1 && collectionMatch.iterator().next().equals(""))) {
						match = "";
						collectionMatch = null;
					} else if (collectionMatch.size() == 1) {
						match = collectionMatch.iterator().next();
						collectionMatch = null;
					}
					// process collection match if size is at least 2
					else {
						String colors = StringUtils.getLastColors(prefix);
						// process collection
						List<String> collectionReplacement = new ArrayList<>();
						for (Object obj : collectionMatch) {
							String str = obj instanceof Double ? "" + NumberUtils.round((Double) obj, 3) : obj.toString();
							if (collectionReplacement.isEmpty()) {
								str = prefix + str;
							} else if (collectionReplacement.size() == collectionMatch.size() - 1) {
								str = colors + str + suffix;
							} else {
								str = colors + str;
							}
							//if (!str.trim().isEmpty()) {  // fuck it
							collectionReplacement.add(str);
							//}
						}
						// delete current line and insert result ; result will contain prefix and suffix so no need to re-add it
						result.remove(i);
						result.addAll(i, collectionReplacement);
						// decrement i to reprocess this line (might be more placeholders in suffix or even in replacement), and continue
						--i;
						continue main;
					}
				}
				// match isn't a collection, process it
				String replacement = match == null ? placeholder : StringUtils.replacementToString(match, formatNumbers);
				// replace in string
				resultLine = prefix + replacement + suffix;
				offset += replacement.length() - placeholder.length();
				// however, if no prefix, no suffix and empty replacement, skip line
				if (resultLine.isEmpty()) {
					result.remove(i);  // remove from list and decrement index to reprocess
					--i;
					continue main;
				}
			}
			// set result line
			result.set(i, resultLine);
		}
		// done
		return result;
	}

	// ----- object
	@Override
	public String toString() {
		return "(" + StringUtils.toTextString(", ", exactMatch.keySet())
		+ (customMatcher == null ? "" : " -- custom : " + customMatcherDesc)
		+ ")";
	}

	// ----- maker
	public static StringReplacer empty() {
		return new StringReplacer();
	}

	public static StringReplacer of(String placeholder, Supplier<Object> replacer) {
		return new StringReplacer().with(placeholder, replacer);
	}

	public static StringReplacer of(String customMatcherDesc, CustomMatcher customMatcher) {
		return new StringReplacer().with(customMatcherDesc, customMatcher);
	}

}
