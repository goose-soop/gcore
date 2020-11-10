package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderContainerBrackets extends PlaceholderContainer {

	private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

	public PlaceholderContainerBrackets(String id, int priority, boolean needPlayer, List<String> description, BiFunction<String, Player, String> replacer) {
		super(id, priority, needPlayer, description, (og, player) -> {
			if (og == null || !StringUtils.hasBracketPlaceholders(og)) {
				return og;
			}
			// find matches
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
			return matcher.appendTail(result).toString();
		});
	}

}
