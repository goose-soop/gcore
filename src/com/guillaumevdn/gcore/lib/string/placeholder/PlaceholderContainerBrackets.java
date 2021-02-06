package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderContainerBrackets extends PlaceholderContainer {

	//private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");
	// - actually don't use that pattern, for instance {math:{sub} * 2} will match {math:{sub}
	// - and I'm too lazy to search how to do that using a proper regex, and my solution is kinda clean so ¯\_(ツ)_/¯

	public PlaceholderContainerBrackets(String id, int priority, boolean needPlayer, List<String> description, BiFunction<String, Player, String> replacer) {
		super(id, priority, needPlayer, description, (og, player) -> {
			if (og == null || !StringUtils.hasBracketPlaceholders(og)) {
				return og;
			}
			Stack<Integer> starts = new Stack<>();
			for (int i = 0; i < og.length(); ++i) {
				char c = og.charAt(i);
				if (c == '{') {
					starts.push(i);
				} else if (c == '}') {
					if (starts.isEmpty()) {
						return og;  // invalid placeholder string
					}
					int start = starts.pop();
					String placeholder = og.substring(start + 1, i);
					// found match, replace and continue
					String replacement = replacer.apply(placeholder, player);
					if (replacement != null) {
						og = og.substring(0, start) + replacement + og.substring(i + 1);  // this will not contain the placeholder
						i = start;  // restart at start, maybe we replaced a new placeholder
					}
					// no match ; just continue, ignore this placeholder
				}
			}
			return og;
			/*// find matches
			Matcher matcher = PATTERN.matcher(og);
			StringBuffer result = new StringBuffer(og.length());
			while (matcher.find()) {
				String placeholder = matcher.group();
				placeholder = placeholder.substring(1, placeholder.length() - 1);  // matcher group obviously contains brackets as well
				// find match
				String replacement = replacer.apply(placeholder, player);
				if (replacement == null) {
					continue;
				}
				// replace in string
				matcher.appendReplacement(result, replacement);
			}
			// done
			return matcher.appendTail(result).toString();*/
		});
	}

}
