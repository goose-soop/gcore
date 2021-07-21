package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderContainerSingleChar extends PlaceholderContainer {

	public PlaceholderContainerSingleChar(String id, int priority, boolean needPlayer, List<String> description, char placeholderBegin, char placeholderEnd, BiFunction<String, Player, String> replacer) {
		super(id, priority, needPlayer, description, (og, player) -> {
			if (og == null || !StringUtils.hasBracketPlaceholders(og)) {
				return og;
			}
			Stack<Integer> starts = new Stack<>();
			for (int i = 0; i < og.length(); ++i) {
				char c = og.charAt(i);
				if (c == placeholderBegin) {
					starts.push(i);
				} else if (c == placeholderEnd) {
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
		});
	}

}
